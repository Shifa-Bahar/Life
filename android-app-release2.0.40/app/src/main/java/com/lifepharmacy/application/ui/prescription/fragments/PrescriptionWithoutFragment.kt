package com.lifepharmacy.application.ui.prescription.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentPrescriptionWithoutBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.enums.ImageUploadingType
import com.lifepharmacy.application.managers.analytics.withOutPrescriptionScreenOpen
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.prescription.adapters.ClickItemImage
import com.lifepharmacy.application.ui.prescription.adapters.ImagesAdapter
import com.lifepharmacy.application.ui.prescription.viewmodel.PrescriptionViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.utils.dailogs.ImageActionBottomSheet
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.IntentHandler
import com.lifepharmacy.application.utils.IntentStarter
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.IntentAction
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.livechatinc.inappchat.ChatWindowErrorType
import com.livechatinc.inappchat.ChatWindowView
import com.livechatinc.inappchat.models.NewMessageModel
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class PrescriptionWithoutFragment : BaseFragment<FragmentPrescriptionWithoutBinding>(),
  ClickPrescriptionWithoutFragment, ClickTool, ClickItemImage, ClickContactLayout,
  ChatWindowView.ChatWindowEventsListener {
  private val viewModel: PrescriptionViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  private val viewModelAddress: AddressViewModel by activityViewModels()
  lateinit var intentHandler: IntentHandler
  lateinit var intentStarter: IntentStarter
  lateinit var imagesAdapter: ImagesAdapter
  private var fullScreenChatWindow: ChatWindowView? = null
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Override this method to customize back press
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      if (fullScreenChatWindow != null) {
        fullScreenChatWindow!!.hideChatWindow()
      } else {
        findNavController().navigateUp()
      }
    }

  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.withOutPrescriptionScreenOpen()
    intentHandler = IntentHandler(requireContext(), this, viewModel.appManager.mediaManager)
    intentStarter = IntentStarter(requireContext(), this)
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      viewModel.resetPrescription()
    }
    observers()
    proceedButtonStatus()
    reselectionPrescriptionObserver()
    return mView
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    proceedButtonStatus()
  }

  private fun initUI() {
    binding.viewModel = viewModel
    binding.profileViewModel = profileViewModel
    binding.lifecycleOwner = this
    binding.click = this
    binding.tollBar.click = this
    binding.tollBar.tvToolbarTitle.text = getString(R.string.prescription)
    binding.llContact.click = this
    binding.isThereImages = viewModel.isThereFiles

    viewModel.isClaimInsurance.set(false)
    viewModel.isNotClaimInsurance.set(false)
    viewModel.isThereFiles.set(false)
    binding.tollBar.imgBack.visibility = View.VISIBLE
    binding.note = viewModel.note
    viewModel.note.setEditText(binding.edNote)
    initImagesRV()
  }

  private fun initImagesRV() {
    imagesAdapter = ImagesAdapter(requireActivity(), this)
    binding.rvImages.adapter = imagesAdapter
  }
  fun reselectionPrescriptionObserver() {
    viewModel.appManager.loadingState.prescriptionReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_prescription)
          viewModel.appManager.loadingState.prescriptionReselected.value = false
        }
      }
    }
  }
  private fun observers() {
    viewModel.filesListLive.observe(viewLifecycleOwner, Observer {
      it?.let {
        proceedButtonStatus()
        if (it.isNotEmpty()) {
          viewModel.isThereFiles.set(true)
          imagesAdapter.setDataChanged(it)
        } else {
          viewModel.isThereFiles.set(false)
        }
      }
    })
    if (profileViewModel.isLoggedIn.get() != true) {
      findNavController().navigate(R.id.nav_login_sheet)
    }
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_prescription_without
  }

  override fun permissionGranted(requestCode: Int) {

  }

  fun handlePicturePermissions() {
    intentStarter.openImageSelectionBottomSheet()
  }

  override fun onClickBack() {
    handleCustomBackPress()


  }


  override fun onClickUploadImage() {
//    viewModel.imageUploadingType.value = ImageUploadingType.PRESCRIPTION
    requestPhotoPermissions.launch(ConstantsUtil.RequiredPermissionsPicture)

//    PermissionX.init(requireActivity())
//      .permissions(RequiredPermissionsPictureX)
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


  override fun onClickProceed() {
    if (viewModelAddress.deliveredAddressMut.value == null) {
      viewModelAddress.isSelecting.set(true)
      findNavController().navigate(R.id.nav_address)
    } else {
      uploadPrescription()
    }


  }

  override fun onClickLogin() {
    findNavController().navigate(R.id.nav_login_sheet)
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
            uploadFile(intentStarter.photoFile!!)
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

  override fun onClickCross(file: String) {
    viewModel.imageUploadingType.value = ImageUploadingType.PRESCRIPTION
    handleImageDeleteAction(file)
  }

  override fun onClickImage(file: String) {
    viewModel.imageUploadingType.value = ImageUploadingType.PRESCRIPTION
    openImageActionSheet(file)
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
                  proceedButtonStatus()
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

//  private fun createDoc() {
//    docsViewModel.createDoc().observe(viewLifecycleOwner, Observer {
//      it?.let {
//        when (it.status) {
//          Result.Status.SUCCESS -> {
//            hideLoading()
//            it.data?.data?.let { it1 ->
//              docSelected(it1.first())
//            }
//          }
//          Result.Status.ERROR -> {
//            it.message?.let { it1 ->
//              AlertManager.showErrorMessage(
//                requireActivity(),
//                it1
//              )
//            }
//            hideLoading()
//          }
//          Result.Status.LOADING -> {
//            showLoading()
//          }
//        }
//      }
//    })
//  }

  private fun uploadPrescription() {
    viewModel.uploadPrescription(viewModelAddress.deliveredAddressMut.value?.id.toString())
      .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
        it?.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
              hideLoading()
              viewModel.prescriptionID.value = it.data?.data?.id.toString()
              it.data?.message?.let { it1 ->
                AlertManager.showSuccessMessage(
                  requireActivity(),
                  it1
                )
              }
              viewModel.resetPrescription()
              findNavController().navigate(R.id.dialogPrescriptionUploaded)
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

  private fun handleCustomBackPress() {
//    if (viewModel.isNotClaimInsurance.get() == true || viewModel.isClaimInsurance.get() == true) {
//      viewModel.isNotClaimInsurance.set(false)
//      viewModel.isClaimInsurance.set(false)
//    } else {
    findNavController().navigateUp()
//      Utils.exitApp(requireActivity())
//    }
  }

  private fun handleFileUploadedResult(url: String) {
    viewModel.setFileList(url)
//    createDoc()


  }

//  private fun handleSelectedDoc(url: String) {
//
//    viewModel.setFileList(url)
//  }

  private fun openImageActionSheet(url: String) {
    var selectImage: ImageActionBottomSheet = ImageActionBottomSheet.newInstance(
      url
    )
    selectImage.show(
      childFragmentManager,
      ImageActionBottomSheet.TAG
    )
    selectImage.setImageSelectListener(object :
      ImageActionBottomSheet.OnImageActionSelected {
      override fun onClickView(url: String) {
//        val fullImageIntent = Intent(requireActivity(), FullScreenImageViewActivity::class.java)
        var uriString = ArrayList<String>()
        uriString.add(url)
//// uriString is an ArrayList<String> of URI of all images
//// uriString is an ArrayList<String> of URI of all images
//        fullImageIntent.putExtra(FullScreenImageViewActivity.URI_LIST_DATA, uriString)
//// pos is the position of image will be showned when open
//// pos is the position of image will be showned when open
////        fullImageIntent.putExtra(FullScreenImageViewActivity.IMAGE_FULL_SCREEN_CURRENT_POS, pos)
//        startActivity(fullImageIntent)
        StfalconImageViewer.Builder(context, uriString) { view, image ->
          Glide.with(view.context)
            .load(image)
            .into(view)
        }.show()
      }

      override fun onDelete(url: String) {
        handleImageDeleteAction(url)
      }

      override fun onDismissed() {

      }

    })
  }

  private fun handleImageDeleteAction(url: String) {

    viewModel.removeFile(url)


  }

  private fun proceedButtonStatus() {
    viewModel.isProceedEnable.value = viewModel.filesListLive.value.isNullOrEmpty()
  }

  override fun onClickCall() {
    appManager.storageManagers.config.contactPhone?.let {
      IntentAction.callNumber(
        requireActivity(),
        it
      )
    }
  }

  override fun onClickChat() {
    openLiveChat()
//    IntentAction.openLiveChat(viewModel.appManager,fullScreenChatWindow,requireActivity(),this)
  }

  override fun onChatWindowVisibilityChanged(visible: Boolean) {

  }

  override fun onNewMessage(message: NewMessageModel?, windowVisible: Boolean) {

  }

  override fun onStartFilePickerActivity(intent: Intent?, requestCode: Int) {

  }

  override fun onError(
    errorType: ChatWindowErrorType?,
    errorCode: Int,
    errorDescription: String?
  ): Boolean {
    return false
  }

  override fun handleUri(uri: Uri?): Boolean {
    return false
  }

  private fun openLiveChat() {
    val customParamsMap = HashMap<String, String>()
    customParamsMap["phone"] = appManager.persistenceManager.getLoggedInUser()?.phone.toString()
    customParamsMap["source"] = "profile"
    val configuration = ChatWindowConfiguration(
      ConstantsUtil.LIVE_CHAT_LICENSE,
      "0",
      appManager.persistenceManager.getLoggedInUser()?.name,
      appManager.persistenceManager.getLoggedInUser()?.email,
      customParamsMap
    )
    if (fullScreenChatWindow == null) {
      fullScreenChatWindow = ChatWindowView.createAndAttachChatWindowInstance(requireActivity());
      fullScreenChatWindow!!.setUpWindow(configuration);
      fullScreenChatWindow!!.setUpListener(this);
      fullScreenChatWindow!!.initialize();
    }
    fullScreenChatWindow?.showChatWindow()
  }
//  private fun docSelected(doc: DocumentModel) {
//    docsViewModel.selectedDoc.value = doc
//  }
}



