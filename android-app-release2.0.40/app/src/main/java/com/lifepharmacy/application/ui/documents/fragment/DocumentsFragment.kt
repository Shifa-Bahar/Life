package com.lifepharmacy.application.ui.documents.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentDocumentsBinding
import com.lifepharmacy.application.enums.DocTypeFilter
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.documentScreenOpen
import com.lifepharmacy.application.model.docs.DocumentModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.documents.adapters.ClickItemDoc
import com.lifepharmacy.application.ui.documents.adapters.DocsAdapter
import com.lifepharmacy.application.ui.documents.viewmodel.DocumentsViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.IntentHandler
import com.lifepharmacy.application.utils.IntentStarter
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Utils
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class DocumentsFragment : BaseFragment<FragmentDocumentsBinding>(), ClickTool, ClickItemDoc,
  ClickDocumentFragment {

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

  private val viewModel: DocumentsViewModel by activityViewModels()
  lateinit var docsAdapter: DocsAdapter
  lateinit var intentHandler: IntentHandler
  lateinit var intentStarter: IntentStarter
  private var layoutManager: GridLayoutManager? = null
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.documentScreenOpen()
    intentHandler = IntentHandler(requireContext(), this, viewModel.appManager.mediaManager)
    intentStarter = IntentStarter(requireContext(), this)
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
    }
    viewModel.selectedDoc.value = null
    if (viewModel.isProfile) {
      viewModel.docTypeFilter.value = DocTypeFilter.NON
    }
    viewModel.showBottomView.value = false
    observers()
    reselectionProfileObserver()
    return mView
  }

  private fun initUI() {
    binding.click = this
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.tollBar.tvToolbarTitle.text = getString(R.string.documents)
    binding.tollBar.click = this
    binding.docName = viewModel.docTitle
    viewModel.docTitle.setEditText(binding.edDocName)
    initDocsRV()
  }
  fun reselectionProfileObserver() {
    viewModel.appManager.loadingState.profileReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_profile)
          viewModel.appManager.loadingState.profileReselected.value = false
        }
      }
    }
  }
  private fun initDocsRV() {
    docsAdapter = DocsAdapter(requireActivity(), this, viewModel.isProfile)
    layoutManager = GridLayoutManager(requireContext(), 2)
    layoutManager!!.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int {
        when (position) {
          0 -> return 2
          else -> return 1
        }
      }
    }
    binding.rvDocs.layoutManager = layoutManager
    binding.rvDocs.adapter = docsAdapter
  }

  private fun observers() {
    viewModel.getDocs().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.data?.let { it1 -> handleDocListing(it1) }

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

  override fun getLayoutRes(): Int {
    return R.layout.fragment_documents
  }

  override fun permissionGranted(requestCode: Int) {
    if (requestCode == ConstantsUtil.PERMISSION_PICTURE_REQUEST_CODE && Permisions) {

    }
  }

  fun handlePicturePermissions() {
    intentStarter.openImageSelectionBottomSheet()
  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

  override fun onClickUpload() {

    requestPhotoPermissions.launch(ConstantsUtil.RequiredPermissionsPicture)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

//    if (data != null) {
    if (resultCode == Activity.RESULT_OK) {
      when (requestCode) {
        ConstantsUtil.GALLERY_PIC -> {
          var file = intentHandler.handleGalleryIntent(data)
          if (file != null) {
            uploadFile(file)
          }

        }
        ConstantsUtil.CAMERA_PIC -> {
//          intentStarter.photoFile?.let {
//            MediaManagerKT.startCropImage(
//              it,
//              requireContext(),
//              this@PrescriptionFragment
//            )
//          }
          if (intentStarter.photoFile != null) {
            compressUpload(intentStarter.photoFile!!)

          }
        }
        ConstantsUtil.CROP_IMAGE -> {
          val resultUri = UCrop.getOutput(data!!)
          if (resultUri != null) {
            uploadFile(resultUri.toFile())
//              viewModel.setFileList(resultUri.toFile())
          }
        }
      }
    }
//    }

  }

  override fun onClickDocType() {
    findNavController().navigate(R.id.docTypeBottomSheet)
  }

  override fun onClickCloseBottomView() {
    viewModel.showBottomView.value = false
  }

  override fun onClickUploadNew() {
    viewModel.showBottomView.value = true
  }

  override fun onClickDeleteDoc(doc: DocumentModel) {
    viewModel.selectedDoc.value = doc
    MaterialAlertDialogBuilder(
      requireContext(),
      R.style.ThemeOverlay_App_MaterialAlertDialog
    )
      .setTitle(getString(R.string.delete_document))
      .setMessage(getString(R.string.are_you_sure))
      .setNegativeButton(getString(R.string.no)) { dialog, which ->
      }
      .setPositiveButton(getString(R.string.delete)) { dialog, which ->
        deleteDoc()
      }
      .show()

  }

  override fun onClickDoc(doc: DocumentModel, post: Int) {
    docSelected(doc, post)
//    viewModel.selectedDoc.value = doc
//    docsAdapter.selectedItem(post)
//    if (!viewModel.isProfile) {
//      CoroutineScope(Dispatchers.Main.immediate).launch {
//        delay(500)
//        findNavController().navigateUp()
//      }
//    }

  }

  private fun uploadFile(file: File) {
    if (file != null && file.exists()) {
      file?.let { fileInternal ->
        viewModel.uploadFile(fileInternal).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
          it?.let {
            when (it.status) {
              Result.Status.SUCCESS -> {
                hideLoading()
                it.data?.data?.file?.let { it1 ->
                  handleFileUploadedResult(it1)
                }
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

  private fun handleFileUploadedResult(url: String) {
    viewModel.fileUrl.value = url
    createDoc()
  }

  private fun deleteDoc() {
    viewModel.deleteDoc().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.data?.let { it1 -> handleDocListing(it1) }

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

  private fun createDoc() {
    viewModel.createDoc().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.data?.let { it1 ->
              handleDocListing(it1)
              docSelected(it1.first(), 0)
            }
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

  private fun handleDocListing(list: ArrayList<DocumentModel>) {
    viewModel.docTypeFilter.observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it) {
          DocTypeFilter.EID_FRONT -> {
            val filteredArray = list.filter { item ->
              item.fileType == Utils.getDocTypeForSend(getString(R.string.emirates_id_front))
            }
            viewModel.docType.value = getString(R.string.emirates_id_front)
            docsAdapter.setDataChanged(filteredArray as ArrayList<DocumentModel>)
          }
          DocTypeFilter.EID_BACK -> {
            val filteredArray = list.filter { item ->
              item.fileType == Utils.getDocTypeForSend(getString(R.string.emirates_id_back))
            }
            viewModel.docType.value = getString(R.string.emirates_id_back)
            docsAdapter.setDataChanged(filteredArray as ArrayList<DocumentModel>)
          }
          DocTypeFilter.INSURANCE_FRONT -> {
            val filteredArray = list.filter { item ->
              item.fileType == Utils.getDocTypeForSend(getString(R.string.insurance_card_front))
            }
            viewModel.docType.value = getString(R.string.insurance_card_front)

            docsAdapter.setDataChanged(filteredArray as ArrayList<DocumentModel>)
          }
          DocTypeFilter.INSURANCE_BACK -> {
            val filteredArray = list.filter { item ->
              item.fileType == Utils.getDocTypeForSend(getString(R.string.insurance_card_back))
            }
            viewModel.docType.value = getString(R.string.insurance_card_back)
            docsAdapter.setDataChanged(filteredArray as ArrayList<DocumentModel>)
          }
          DocTypeFilter.NON -> {
            docsAdapter.setDataChanged(list)

          }
        }
      }
    })


//    docsAdapter.selectedItem(0)
    binding.showEmpty = docsAdapter.arrayList.isEmpty()

  }

  private fun docSelected(doc: DocumentModel, position: Int) {
    viewModel.selectedDoc.value = doc
    docsAdapter.selectedItem(position)
    if (!viewModel.isProfile) {
      CoroutineScope(Dispatchers.Main.immediate).launch {
        delay(500)
        findNavController().navigateUp()
      }
    }
  }

  private fun compressUpload(file: File) {
    uploadFile(file)
  }

}
