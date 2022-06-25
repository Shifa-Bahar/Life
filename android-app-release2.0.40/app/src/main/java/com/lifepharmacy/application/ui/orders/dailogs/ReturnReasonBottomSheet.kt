package com.lifepharmacy.application.ui.orders.dailogs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toFile
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomWithLoading
import com.lifepharmacy.application.databinding.BottomSheetReturnReasonBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.orders.adapters.ClickReturnReason
import com.lifepharmacy.application.ui.orders.adapters.ReturnReasonAdapter
import com.lifepharmacy.application.ui.orders.viewmodels.OrderDetailViewModel
import com.lifepharmacy.application.ui.prescription.adapters.ClickItemImage
import com.lifepharmacy.application.ui.prescription.adapters.ImagesAdapter
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.IntentHandler
import com.lifepharmacy.application.utils.IntentStarter
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class ReturnReasonBottomSheet : BaseBottomWithLoading<BottomSheetReturnReasonBinding>(),
  ClickReturnReasonBottom, ClickReturnReason, ClickItemImage {
  private val viewModel: OrderDetailViewModel by activityViewModels()
  private lateinit var returnReasonAdapter: ReturnReasonAdapter
  lateinit var intentHandler: IntentHandler
  lateinit var intentStarter: IntentStarter
  lateinit var imagesAdapter: ImagesAdapter

  private val requestPhotoPermissions =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
      var result = true
      granted.entries.forEach {
        if (!it.value) {
          result = false
          return@forEach
        }
      }
      if (result) {
        handlePicturePermissions()
      } else {
        AlertManager.permissionRequestPopup(requireActivity())
      }

    }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    intentHandler = IntentHandler(requireContext(), this, viewModel.appManager.mediaManager)
    intentStarter = IntentStarter(requireContext(), this)
    isCancelable = true
    initLayout()
    observers()

  }

  private fun initLayout() {
    binding.click = this
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.returnNotes = viewModel.returnNote
    viewModel.returnNote.setEditText(binding.edReturnNote)
    viewModel.isThereFiles.value = false
    returnReasonAdapter = ReturnReasonAdapter(requireActivity(), this)
    binding.rvReasons.adapter = returnReasonAdapter
    initImagesRV()
  }

  private fun initImagesRV() {
    imagesAdapter = ImagesAdapter(requireActivity(), this)
    binding.rvImages.adapter = imagesAdapter
  }

  private fun observers() {
    viewModel.filesListLive.observe(viewLifecycleOwner, Observer {
      it?.let {
        if (it.isNotEmpty()) {
          viewModel.isThereFiles.value = true
          imagesAdapter.setDataChanged(it)
        } else {
          viewModel.isThereFiles.value = true

        }
      }
    })
    viewModel.getReturnReasons().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            returnReasonAdapter.setDataChanged(it.data)
          }
          Result.Status.ERROR -> {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
          }
          Result.Status.LOADING -> {

          }
        }
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_return_reason
  }

  override fun permissionGranted(requestCode: Int) {

  }

  fun handlePicturePermissions() {
    openImageSheet()
  }

  override fun onClickSubmit() {
    submitReturns()
  }

  override fun onClickAddImage() {
    requestPhotoPermissions.launch(ConstantsUtil.RequiredPermissionsPicture)

//    PermissionX.init(requireActivity())
//      .permissions(Constants.RequiredPermissionsPictureX)
//      .request(RequestCallback { allGranted: Boolean, list: List<String?>?, list1: List<String?>? ->
//        if (allGranted) {
//          intentStarter.openImageSelectionBottomSheet()
//
//        } else {
//          Toast.makeText(
//            requireActivity(),
//            "Please provide  permission ",
//            Toast.LENGTH_LONG
//          ).show()
//        }
//      })
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode == Activity.RESULT_OK) {
      when (requestCode) {
        ConstantsUtil.GALLERY_PIC -> {
          intentHandler.handleGalleryIntent(data)

        }
        ConstantsUtil.CAMERA_PIC -> {
          intentStarter.photoFile?.let { uploadFile(it) }
//          intentStarter.photoFile?.let {
//            MediaManagerKT.startCropImage(
//              it,
//              requireContext(),
//              this@ReturnReasonBottomSheet
//            )
//          }
        }
        ConstantsUtil.CROP_IMAGE -> {
          val resultUri = UCrop.getOutput(data!!)
          if (resultUri != null) {
            uploadFile(resultUri.toFile())
          }
        }
      }
    }

  }

  override fun onClickCross(file: String) {
    viewModel.removeFile(file)
  }

  override fun onClickImage(file: String) {

  }

  override fun onClickReason(position: Int, reason: String) {
    returnReasonAdapter.setItemSelected(position)
    viewModel.cancelReason = reason
    viewModel.setReturnNoteError(reason)
  }

  private fun uploadFile(file: File) {
    if (file != null && file.exists()) {
      file?.let { fileInternal ->
        viewModel.uploadFile(fileInternal).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
          it?.let {
            when (it.status) {
              Result.Status.SUCCESS -> {
                hideLoading()
                it.data?.data?.file?.let { it1 -> viewModel.setFileList(it1) }
              }
              Result.Status.ERROR -> {
                it.message?.let { it1 ->
                  AlertManager.showErrorMessage(
                    requireActivity(),
                    it1
                  )
                }
                hideLoading()
              }
              Result.Status.LOADING -> {
                showLoading()
              }
            }
          }

        })
      }
    }

  }

  private fun submitReturns() {
    viewModel.returnItems().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            AnalyticsUtil.setEvenWithNamePropertyValue(
              requireContext(),
              "OrderReturn",
              viewModel.orderId,
              "order_id"
            )
            findNavController().navigate(R.id.returnProcessingDialog)
          }
          Result.Status.ERROR -> {
            findNavController().navigateUp()
            it.message?.let { it1 ->
              AlertManager.showErrorMessage(
                requireActivity(),
                it1
              )
            }
            hideLoading()
          }
          Result.Status.LOADING -> {
            showLoading()
          }
        }
      }

    })
  }

  private fun openImageSheet() {
    PermissionX.init(requireActivity())
      .permissions(ConstantsUtil.RequiredPermissionsPictureX)
      .request(RequestCallback { allGranted: Boolean, list: List<String?>?, list1: List<String?>? ->
        if (allGranted) {
          intentStarter.openImageSelectionBottomSheet()

        } else {
          Toast.makeText(
            requireActivity(),
            "Please provide  permission ",
            Toast.LENGTH_LONG
          ).show()
        }
      })
  }

  override fun getLoadingLayout(): ConstraintLayout {
    return binding.llLoading.clLoading
  }
}