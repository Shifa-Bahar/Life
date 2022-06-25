package com.lifepharmacy.application.ui.prescription.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.lifepharmacy.application.databinding.FragmentChoosePrescriptionsBinding
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.managers.analytics.choosePrescriptionScreenOpen
import com.lifepharmacy.application.ui.address.AddressSelectionActivity
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.address.ClickSelectedAddress
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.prescription.viewmodel.PrescriptionViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.IntentAction
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.livechatinc.inappchat.ChatWindowErrorType
import com.livechatinc.inappchat.ChatWindowView
import com.livechatinc.inappchat.models.NewMessageModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class ChoosePrescriptionFragment : BaseFragment<FragmentChoosePrescriptionsBinding>(),
  ClickChoosePrescriptionFragment, ClickSelectedAddress, ClickContactLayout, ClickTool,
  ChatWindowView.ChatWindowEventsListener {
  private val viewModel: PrescriptionViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  private val viewModelAddress: AddressViewModel by activityViewModels()
  private var fullScreenChatWindow: ChatWindowView? = null
  private val addressContract =
    registerForActivityResult(AddressSelectionActivity.Contract()) { result ->
      result?.address?.let {
        viewModelAddress.deliveredAddressMut.value = it
        viewModelAddress.addressSelection = AddressSelection.NON
      }
    }

  //  lateinit var intentHandler: IntentHandler
//  lateinit var intentStarter: IntentStarter
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Override this method to customize back press
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      if (fullScreenChatWindow != null) {
        fullScreenChatWindow!!.hideChatWindow()
      } else {
        findNavController().navigateUp()
//        Utils.exitApp(requireActivity())
      }
    }

  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.choosePrescriptionScreenOpen()
//    intentHandler = IntentHandler(requireContext(), this, viewModel.appManager.mediaManager)
//    intentStarter = IntentStarter(requireContext(), this)
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      viewModel.resetPrescription()
    }
    observers()
    proceedButtonStatus()
    reselectionObserver()
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
    binding.clickContact = this
    binding.layoutLocation.click = this
    binding.layoutLocation.mViewModel = viewModelAddress
    binding.layoutLocation.lifecycleOwner = this
    binding.tollBar.click = this
    binding.tollBar.imgBack.visibility = View.VISIBLE
    binding.tollBar.isBackVisible = false
    binding.tollBar.tvToolbarTitle.text = getString(R.string.medicines)

    var imageUrl = appManager.storageManagers.config.prescriptionBanner ?: ""
    if (imageUrl.isNotEmpty()) {
      activity?.let { Glide.with(it).load(imageUrl).into(binding.imageView18) }
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
    if (profileViewModel.isLoggedIn.get() != true) {
      findNavController().navigate(R.id.nav_login_sheet)
    }
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_choose_prescriptions
  }

  override fun permissionGranted(requestCode: Int) {
//    if (requestCode == Constants.PERMISSION_PICTURE_REQUEST_CODE) {
//      intentStarter.openImageSelectionBottomSheet()
//    }
  }

  override fun onClickHavePrescription() {
    findNavController().navigate(R.id.prescriptionFragment)
  }

  override fun onClickDontHavePrescription() {
    findNavController().navigate(R.id.prescriptionWithoutFragment)
  }

  override fun onClickLogin() {
    findNavController().navigate(R.id.nav_login_sheet)
  }


  private fun proceedButtonStatus() {
    viewModel.isProceedEnable.value =
      !(viewModel.cardFrontUrl.get().isNullOrEmpty() || viewModel.cardBackUrl.get()
        .isNullOrEmpty() || viewModel.filesListLive.value.isNullOrEmpty())
  }

  override fun onClickChangeAddress() {
    addressContract.launch(true)
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

  override fun onClickBack() {
    Log.e(TAG, "onClickBack: hiii")
//    findNavController().navigateUp()
    findNavController().navigate(R.id.nav_home)
  }
}



