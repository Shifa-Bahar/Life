package com.lifepharmacy.application.ui.prescription.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.lifepharmacy.application.databinding.FragmentPrescriptionsBinding
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.enums.DocTypeFilter
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.documents.viewmodel.DocumentsViewModel
import com.lifepharmacy.application.enums.ImageUploadingType
import com.lifepharmacy.application.managers.analytics.prescriptionScreenOpen
import com.lifepharmacy.application.model.docs.DocumentModel
import com.lifepharmacy.application.ui.address.AddressSelectionActivity
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.address.ClickSelectedAddress
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
import com.lifepharmacy.application.utils.universal.Utils
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_rewards_front.view.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class PrescriptionFragment : BaseFragment<FragmentPrescriptionsBinding>(),
  ClickPrescriptionFragment, ClickTool, ClickItemImage, ClickSelectedAddress,
  ClickPrescriptionFiles {
  private val viewModel: PrescriptionViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  private val docsViewModel: DocumentsViewModel by activityViewModels()
  private val viewModelAddress: AddressViewModel by activityViewModels()
  lateinit var intentHandler: IntentHandler
  lateinit var intentStarter: IntentStarter
  lateinit var imagesAdapter: ImagesAdapter
  private val addressContract =
    registerForActivityResult(AddressSelectionActivity.Contract()) { result ->
      result?.address?.let {
        viewModelAddress.deliveredAddressMut.value = it
        viewModelAddress.addressSelection = AddressSelection.NON
      }
    }

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

  // Override this method to customize back press
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Override this method to customize back press
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      handleCustomBackPress()
    }

  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.prescriptionScreenOpen()
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
    binding.layoutLocation.click = this
    binding.layoutLocation.mViewModel = viewModelAddress
    binding.layoutLocation.lifecycleOwner = this
    binding.tollBar.tvToolbarTitle.text = getString(R.string.prescription)
    var imageUrl = appManager.storageManagers.config.prescriptionBanner ?: ""

    Log.e("shifa", "initUI: "+imageUrl)
    try {

      if (imageUrl.isNotEmpty()) {
        activity?.let { Glide.with(it).load(imageUrl).into(binding.imageView18)}
      }
    } catch (e: Exception) {
    }


//    binding.lyPrescriptionDetails.click = this
//    binding.lyPrescriptionDetails.layoutInsurance.click = this
//    binding.isEnableProceedButton = viewModel.isProceedEnable
//    binding.lyPrescriptionDetails.isClaimingInsurance = viewModel.isClaimInsurance
//    binding.lyPrescriptionDetails.isNotClaimingInsurance = viewModel.isNotClaimInsurance
//    binding.isThereImages = viewModel.isThereFiles

//    binding.lyPrescriptionDetails.insuranceNumber = viewModel.insuranceNumber
//    viewModel.insuranceNumber.setEditText(binding.lyPrescriptionDetails.edInsurance)
//    binding.lyPrescriptionDetails.eRxNumber = viewModel.eRxNumber
//    viewModel.eRxNumber.setEditText(binding.lyPrescriptionDetails.edRxnumber)

    viewModel.isClaimInsurance.set(false)
    viewModel.isNotClaimInsurance.set(false)
    viewModel.isThereFiles.set(false)
//    viewModel.isProceedEnable.set(false)
    binding.tollBar.imgBack.visibility = View.VISIBLE
    binding.llPrescriptionImages.note = viewModel.note
    viewModel.note.setEditText(binding.llPrescriptionImages.edNote)
    initImagesRV()
    initImagePrescription()
//    viewModel.isLoggedIn.value = appManager.persistenceManager.isLoggedIn()
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

  private fun initImagePrescription() {
    binding.llPrescriptionImages.click = this
    binding.llPrescriptionImages.isThereImages = viewModel.isThereFiles
    binding.llPrescriptionImages.cardFrontUrl = viewModel.cardFrontUrl
    binding.llPrescriptionImages.cardBackUrl = viewModel.cardBackUrl
    binding.llPrescriptionImages.insuranceURl = viewModel.insuranceURl
    binding.llPrescriptionImages.insuranceBackUrl = viewModel.insuranceBackUrl
  }

  private fun initImagesRV() {
    imagesAdapter = ImagesAdapter(requireActivity(), this)
    binding.llPrescriptionImages.rvImages.adapter = imagesAdapter
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
    docsViewModel.selectedDoc.observe(viewLifecycleOwner, Observer {
      it?.let {
        it.fileUrl?.let { it1 ->
          handleSelectedDoc(it1)
        }
      }
    })
    docsViewModel.getDocs().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.data?.let { it1 ->
              docsViewModel.listOfDocs = it1
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
//            showLoading()
          }
        }
      }
    })
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_prescriptions
  }

  override fun permissionGranted(requestCode: Int) {

  }

  fun handlePicturePermissions() {
    intentStarter.openImageSelectionBottomSheet()
  }

  override fun onClickBack() {
    handleCustomBackPress()


  }


  override fun onClickClaimInsurance() {
    viewModel.isClaimInsurance.set(true)
  }

  override fun onClickUploadImage() {
    viewModel.imageUploadingType.value = ImageUploadingType.PRESCRIPTION

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

  override fun onClickUploadCardBackImage() {
    viewModel.imageUploadingType.value = ImageUploadingType.EID_BACK
    docsViewModel.docTypeFilter.value = DocTypeFilter.EID_BACK
    docsViewModel.isProfile = false
    docsViewModel.docType.value = getString(R.string.emirates_id_back)
    val filteredArray = docsViewModel.listOfDocs.filter { item ->
      item.fileType == Utils.getDocTypeForSend(getString(R.string.emirates_id_back))
    }
    if (filteredArray.isNullOrEmpty()) {
      requestPhotoPermissions.launch(ConstantsUtil.RequiredPermissionsPicture)
    } else {
      findNavController().navigate(R.id.nav_docs)
    }

//    if (!viewModel.cardBackUrl.value.isNullOrEmpty()) {
//      openImageActionSheet(viewModel.cardBackUrl.value!!)
//    } else {
//      requestSpecificPermission(
//        Constants.PERMISSION_PICTURE_REQUEST_CODE,
//        Constants.RequiredPermissionsPicture
//      )
//    }
  }

  override fun onClickUploadCardFrontImage() {
    viewModel.imageUploadingType.value = ImageUploadingType.EID_FRONT
    docsViewModel.docTypeFilter.value = DocTypeFilter.EID_FRONT
    docsViewModel.isProfile = false
    docsViewModel.docType.value = getString(R.string.emirates_id_front)
    val filteredArray = docsViewModel.listOfDocs.filter { item ->
      item.fileType == Utils.getDocTypeForSend(getString(R.string.emirates_id_front))
    }
    if (filteredArray.isNullOrEmpty()) {
      requestPhotoPermissions.launch(ConstantsUtil.RequiredPermissionsPicture)
    } else {
      findNavController().navigate(R.id.nav_docs)
    }


//    if (!viewModel.cardFrontUrl.value.isNullOrEmpty()) {
//      openImageActionSheet(viewModel.cardFrontUrl.value!!)
//    } else {
//      requestSpecificPermission(
//        Constants.PERMISSION_PICTURE_REQUEST_CODE,
//        Constants.RequiredPermissionsPicture
//      )
//    }
  }

  override fun onClickUploadInsurance() {
    viewModel.imageUploadingType.value = ImageUploadingType.INSURANCE_FRONT
    docsViewModel.docTypeFilter.value = DocTypeFilter.INSURANCE_FRONT
    docsViewModel.docType.value = getString(R.string.insurance_card_front)
    docsViewModel.isProfile = false
    val filteredArray = docsViewModel.listOfDocs.filter { item ->
      item.fileType == Utils.getDocTypeForSend(getString(R.string.insurance_card_front))
    }
    if (filteredArray.isNullOrEmpty()) {
      requestPhotoPermissions.launch(ConstantsUtil.RequiredPermissionsPicture)
    } else {
      findNavController().navigate(R.id.nav_docs)
    }
//    if (!viewModel.insuranceURl.value.isNullOrEmpty()) {
//      openImageActionSheet(viewModel.insuranceURl.value!!)
//    } else {
//      requestSpecificPermission(
//        Constants.PERMISSION_PICTURE_REQUEST_CODE,
//        Constants.RequiredPermissionsPicture
//      )
//    }
  }

  override fun onClickUploadInsuranceBack() {
    viewModel.imageUploadingType.value = ImageUploadingType.INSURANCE_BACK
    docsViewModel.docTypeFilter.value = DocTypeFilter.INSURANCE_BACK
    docsViewModel.docType.value = getString(R.string.insurance_card_back)
    docsViewModel.isProfile = false
    val filteredArray = docsViewModel.listOfDocs.filter { item ->
      item.fileType == Utils.getDocTypeForSend(getString(R.string.insurance_card_back))
    }
    if (filteredArray.isNullOrEmpty()) {
      requestPhotoPermissions.launch(ConstantsUtil.RequiredPermissionsPicture)
    } else {
      findNavController().navigate(R.id.nav_docs)
    }

//    if (!viewModel.insuranceURl.value.isNullOrEmpty()) {
//      openImageActionSheet(viewModel.insuranceURl.value!!)
//    } else {
//      requestSpecificPermission(
//        Constants.PERMISSION_PICTURE_REQUEST_CODE,
//        Constants.RequiredPermissionsPicture
//      )
//    }
  }

  override fun onNotClaimInsurance() {
    viewModel.isNotClaimInsurance.set(true)
  }

  override fun onClickProceed() {
    if (viewModelAddress.deliveredAddressMut.value == null) {
      viewModelAddress.isSelecting.set(true)
      addressContract.launch(true)
    } else {
      uploadPrescription()
    }
  }

  override fun onClickLogin() {
    findNavController().navigate(R.id.nav_login_sheet)
  }

  override fun onClickMayBeLater() {
    handleCustomBackPress()
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

  private fun createDoc() {
    docsViewModel.createDoc().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.data?.let { it1 ->
              docSelected(it1.first())
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
    if (viewModel.isNotClaimInsurance.get() == true || viewModel.isClaimInsurance.get() == true) {
      viewModel.isNotClaimInsurance.set(false)
      viewModel.isClaimInsurance.set(false)
    } else {
      findNavController().navigateUp()
//      Utils.exitApp(requireActivity())
    }
  }

  private fun handleFileUploadedResult(url: String) {
    docsViewModel.fileUrl.value = url
    createDoc()
  }

  private fun handleSelectedDoc(url: String) {
    when (viewModel.imageUploadingType.value) {
      ImageUploadingType.INSURANCE_FRONT -> {
        viewModel.insuranceURl.set(url)
      }
      ImageUploadingType.INSURANCE_BACK -> {
        viewModel.insuranceBackUrl.set(url)
      }
      ImageUploadingType.EID_FRONT -> {
        viewModel.cardFrontUrl.set(url)
      }
      ImageUploadingType.EID_BACK -> {
        viewModel.cardBackUrl.set(url)
      }
      ImageUploadingType.PRESCRIPTION -> {
        viewModel.setFileList(url)
      }
    }
  }

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
    when (viewModel.imageUploadingType.value) {
      ImageUploadingType.INSURANCE_FRONT -> {
        viewModel.insuranceURl.set(null)
      }
      ImageUploadingType.INSURANCE_BACK -> {
        viewModel.insuranceBackUrl.set(null)
      }
      ImageUploadingType.EID_FRONT -> {
        viewModel.cardFrontUrl.set(null)
      }
      ImageUploadingType.EID_BACK -> {
        viewModel.cardBackUrl.set(null)
      }
      ImageUploadingType.PRESCRIPTION -> {
        viewModel.removeFile(url)
      }
    }
  }

  private fun proceedButtonStatus() {
    viewModel.isProceedEnable.value =
      !(viewModel.cardFrontUrl.get().isNullOrEmpty() || viewModel.cardBackUrl.get()
        .isNullOrEmpty() || viewModel.filesListLive.value.isNullOrEmpty())
  }

  override fun onClickChangeAddress() {
    viewModelAddress.isSelecting.set(true)
    addressContract.launch(true)
  }

  private fun docSelected(doc: DocumentModel) {
    docsViewModel.selectedDoc.value = doc
  }
}



