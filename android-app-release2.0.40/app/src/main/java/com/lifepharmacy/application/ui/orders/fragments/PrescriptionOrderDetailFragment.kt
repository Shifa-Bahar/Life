package com.lifepharmacy.application.ui.orders.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentOrderPrescriptionsBinding
import com.lifepharmacy.application.managers.analytics.prescriptionOrderDetailScreenOpen
import com.lifepharmacy.application.ui.orders.viewmodels.OrdersViewModel
import com.lifepharmacy.application.ui.prescription.adapters.ClickItemImage
import com.lifepharmacy.application.ui.prescription.adapters.ImagesAdapter
import com.lifepharmacy.application.ui.prescription.fragments.ClickContactLayout
import com.lifepharmacy.application.ui.prescription.fragments.ClickPrescriptionFiles
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.IntentAction
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.livechatinc.inappchat.ChatWindowErrorType
import com.livechatinc.inappchat.ChatWindowView
import com.livechatinc.inappchat.models.NewMessageModel
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class PrescriptionOrderDetailFragment : BaseFragment<FragmentOrderPrescriptionsBinding>(),
  ClickTool, ClickItemImage,
  ClickContactLayout, ClickPrescriptionFiles,
  ChatWindowView.ChatWindowEventsListener {
  private val viewModel: OrdersViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  lateinit var imagesAdapter: ImagesAdapter
  private var fullScreenChatWindow: ChatWindowView? = null

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
    viewModel.appManager.analyticsManagers.prescriptionOrderDetailScreenOpen()
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
    }
    observers()
    reselectionProfileObserver()
    reselectionObserver()
    return mView
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    observers()
  }

  private fun initUI() {
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.tollBar.click = this
    binding.tollBar.tvToolbarTitle.text = getString(R.string.prescription)
    binding.llContact.click = this
    binding.isThereImages = viewModel.isThereFiles
    viewModel.isThereFiles.set(false)
    binding.tollBar.imgBack.visibility = View.VISIBLE
    binding.note = viewModel.note
    viewModel.note.setEditText(binding.llPrescriptionImages.edNote)
    binding.llPrescriptionImages.edNote.isFocusable = false
    initImagesRV()
    initImagePrescription()
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
  fun reselectionObserver() {
    viewModel.appManager.loadingState.homeReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_home)
          viewModel.appManager.loadingState.homeReselected.value = false
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
    imagesAdapter = ImagesAdapter(requireActivity(), this, false)
    binding.llPrescriptionImages.rvImages.adapter = imagesAdapter
  }

  private fun observers() {
    if (profileViewModel.isLoggedIn.get() != true) {
      findNavController().navigate(R.id.nav_login_sheet)
    }
    viewModel.selectedOrder.observe(viewLifecycleOwner, {
      viewModel.isThereFiles.set(!it.prescription.isNullOrEmpty())
      viewModel.cardFrontUrl.set(it.emiratesIdFront)
      viewModel.cardBackUrl.set(it.emiratesIdBack)
      viewModel.insuranceURl.set(it.insuranceCardFront)
      viewModel.insuranceBackUrl.set(it.insuranceCardBack)
      binding.llItemPrescription.item = it
      viewModel.note.setValue(it.notes)
      imagesAdapter.setDataChanged(it.prescription)

    })
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_order_prescriptions
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickBack() {
    handleCustomBackPress()


  }


  override fun onClickCross(file: String) {
    openImageActionSheet(file)
  }

  override fun onClickImage(file: String) {
    openImageActionSheet(file)
  }


  private fun handleCustomBackPress() {
    findNavController().navigateUp()

  }


  private fun openImageActionSheet(url: String) {
    val uriString = ArrayList<String>()
    uriString.add(url)
    StfalconImageViewer.Builder(context, uriString) { view, image ->

      Glide.with(view.context)
        .load(image)
        .into(view)
//      Glide.get().load(image).into(view)
    }.show()
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


  override fun onClickUploadImage() {
  }

  override fun onClickUploadCardBackImage() {
    viewModel.cardBackUrl.get()?.let { openImageActionSheet(it) }
  }

  override fun onClickUploadCardFrontImage() {
    viewModel.cardFrontUrl.get()?.let { openImageActionSheet(it) }
  }

  override fun onClickUploadInsurance() {
    viewModel.insuranceURl.get()?.let { openImageActionSheet(it) }
  }

  override fun onClickUploadInsuranceBack() {
    viewModel.insuranceBackUrl.get()?.let { openImageActionSheet(it) }
  }
//  private fun docSelected(doc: DocumentModel) {
//    docsViewModel.selectedDoc.value = doc
//  }
}



