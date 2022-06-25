package com.lifepharmacy.application.ui.profile.fragments

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
import androidx.multidex.BuildConfig
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentProfileBinding
import com.lifepharmacy.application.managers.analytics.profileScreenOpen
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import com.lifepharmacy.application.ui.address.AddressSelectionActivity
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.cart.fragmets.AnimationOfferCompleteCouponDialog.Companion.TAG
import com.lifepharmacy.application.ui.dashboard.viewmodel.DashboardViewModel
import com.lifepharmacy.application.ui.documents.viewmodel.DocumentsViewModel
import com.lifepharmacy.application.ui.livechat.LiveChatActivity
import com.lifepharmacy.application.ui.pages.fragment.PageFragment
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.splash.SplashActivity
import com.lifepharmacy.application.utils.URLs
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
class ProfileFragment : BaseFragment<FragmentProfileBinding>(),
  ClickProfileFragment, ClickProfileHeader, ChatWindowView.ChatWindowEventsListener,
  ClickSocialMedia {
  private val viewModel: ProfileViewModel by activityViewModels()
  private val addressViewModel: AddressViewModel by activityViewModels()
  private val dashboardViewModel: DashboardViewModel by activityViewModels()
  private val docsViewModel: DocumentsViewModel by activityViewModels()
  private var fullScreenChatWindow: ChatWindowView? = null
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

  private val addressContract =
    registerForActivityResult(AddressSelectionActivity.Contract()) { result ->
      result?.address.let {
        addressViewModel.deliveredAddressMut.value = it
      }
    }


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()

    }
    viewModel.appManager.analyticsManagers.profileScreenOpen()
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      val verifyOTPResponse = VerifyOTPResponse()
      verifyOTPResponse.user = viewModel.appManager.persistenceManager.getLoggedInUser()!!
      viewModel.userObjectMut.value = verifyOTPResponse.user

      viewModel.isLoggedIn.set(true)
    } else {
      viewModel.isLoggedIn.set(false)
    }
    Log.e(
      TAG,
      "onCreateView:outletENABLED ${viewModel.appManager.storageManagers.config.outletENABLED}"
    )
    setUI()
    observers()
    dashboardViewModel.selectedFragmentIndexMut.value = 3
    reselectionObserver()
    reselectionProfileObserver()
    return mView
  }

  private fun initUI() {
    viewModel.updateCurrentUser()
    binding.click = this
    binding.lyHeader.click = this
    binding.lyOptions.click = this
    addressViewModel.isSelecting.set(true)
    binding.clickSocial = this
    binding.isLoggedIn = viewModel.isLoggedIn
    binding.lyHeader.isLoggedIn = viewModel.isLoggedIn
    binding.lyOptions.isLoggedIn = viewModel.isLoggedIn

    if (viewModel.appManager.persistenceManager.isLoggedIn() && viewModel.appManager.storageManagers.config.outletENABLED == true) {
      binding.lyOptions.tvMembership.visibility = View.VISIBLE
      binding.lyOptions.tvMemview.visibility = View.VISIBLE
    } else {
      binding.lyOptions.tvMembership.visibility = View.GONE
      binding.lyOptions.tvMemview.visibility = View.GONE
    }
    setRadioButtonFromPref()
    binding.build =
      "${getString(R.string.version)} ${com.lifepharmacy.application.BuildConfig.VERSION_NAME} ( ${
        getString(R.string.build)
      } ${com.lifepharmacy.application.BuildConfig.VERSION_CODE} ) "
  }

  private fun setUI() {
    binding.lyHeader.mVieModel = viewModel
    binding.lyHeader.lifecycleOwner = this

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

  private fun setRadioButtonFromPref() {
    when (appManager.persistenceManager.getLang()) {
      "ar" -> {
        binding.lyHeader.btnArabic.isChecked = true
      }
      "en" -> {
        binding.lyHeader.btnEnglish.isChecked = true
      }
    }
  }

  private fun observers() {
    binding.lyHeader.segmented2.setOnCheckedChangeListener { group, checkedId ->
      when (checkedId) {
        R.id.btn_english -> {
          if (appManager.persistenceManager.getLang() != "en") {
            languageChangeBox("en")
          }
        }
        R.id.btn_arabic -> {
          if (appManager.persistenceManager.getLang() != "ar") {
            languageChangeBox("ar")
          }
        }

      }
    }
  }

  private fun languageChangeBox(string: String) {
    MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
      .setTitle(getString(R.string.change_language))
      .setMessage(getString(R.string.are_you_sure))
      .setNegativeButton(getString(R.string.no)) { dialog, which ->
        setRadioButtonFromPref()
      }
      .setPositiveButton(getString(R.string.yes)) { dialog, which ->
        appManager.persistenceManager.saveLang(string)
        SplashActivity.open(requireActivity())
      }
      .show()
  }

  private fun openLiveChat() {

    LiveChatActivity.open(
      activity = requireActivity(),
      name = viewModel.appManager.persistenceManager.getLoggedInUser()?.name ?: "",
      email = viewModel.appManager.persistenceManager.getLoggedInUser()?.email ?: "",
      phone = viewModel.appManager.persistenceManager.getLoggedInUser()?.phone ?: "",
    )

//    val customParamsMap = HashMap<String, String>()
//    customParamsMap["phone"] = appManager.persistenceManager.getLoggedInUser()?.phone.toString()
//    customParamsMap["source"] = "profile"
//    val configuration = ChatWindowConfiguration(
//      ConstantsUtil.LIVE_CHAT_LICENSE,
//      "0",
//      appManager.persistenceManager.getLoggedInUser()?.name,
//      appManager.persistenceManager.getLoggedInUser()?.email,
//      customParamsMap
//    )
//    if (fullScreenChatWindow == null) {
//      fullScreenChatWindow = ChatWindowView.createAndAttachChatWindowInstance(requireActivity())
//      fullScreenChatWindow!!.setUpWindow(configuration)
//      fullScreenChatWindow!!.setUpListener(this)
//      fullScreenChatWindow!!.initialize()
//    }
//    fullScreenChatWindow?.showChatWindow()
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_profile
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickEdit() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      findNavController().navigate(R.id.userBasicFragment)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }

  }

  override fun onClickLogin() {
    findNavController().navigate(R.id.nav_login_sheet)
  }

  override fun onClickOrders() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      findNavController().navigate(R.id.nav_orders)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }

  }

  override fun onClickAddress() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      addressViewModel.isSelecting.set(false)
      addressContract.launch(false)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }

  }

  override fun onClickBuyItAgain() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      findNavController().navigate(R.id.buyItAgainFragment)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }

  }

  override fun onClickRewards() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      findNavController().navigate(R.id.nav_rewards)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }

  }

  override fun onClickVouchers() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      findNavController().navigate(R.id.nav_voucher)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }

  }

  override fun onClickWallet() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      findNavController().navigate(R.id.nav_wallet)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }

  }

  override fun onClickReturns() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      findNavController().navigate(R.id.nav_returns)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }

  }

  override fun onClickWishList() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      findNavController().navigate(R.id.wishListFragment)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }

  }

  override fun onClickAppoinment() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      findNavController().navigate(R.id.nav_appoinment)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }
  }

  override fun onClickOutlet() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      findNavController().navigate(R.id.nav_outlet)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }
  }

  override fun onClickDocuments() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      docsViewModel.isProfile = true
      findNavController().navigate(R.id.nav_docs)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }
  }

  override fun onClickNotifications() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      findNavController().navigate(R.id.notificationsFragment)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }

  }

  override fun onClickLogout() {
    MaterialAlertDialogBuilder(
      requireActivity(),
      R.style.ThemeOverlay_App_MaterialInfoDialog
    )
      .setTitle(getString(R.string.logout))
      .setMessage(getString(R.string.are_you_sure))
      .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
        dialog.dismiss()
      }
      .setPositiveButton(getString(R.string.ok)) { dialog, which ->
        viewModel.appManager.persistenceManager.clearPrefs()
        viewModel.appManager.offersManagers.clear()
        viewModel.isLoggedIn.set(false)
        SplashActivity.open(requireActivity())
      }
      .show()

  }

  override fun onClickContactUs() {
//    IntentAction.openChatActivity(requireActivity(), viewModel.appManager)
//    findNavController().navigate(R.id.pageFragment, PageFragment.getPageFragmentBundle("contact-us"))
  }

  override fun onClickLiveChat() {
//    IntentAction.openLiveChat(viewModel.appManager,fullScreenChatWindow,requireActivity(),this)
    openLiveChat()
  }

  override fun onClickAboutUs() {
    findNavController().navigate(R.id.pageFragment, PageFragment.getPageFragmentBundle("about-us"))
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

  override fun onClickInstagram() {
    IntentAction.openLink(URLs.INST_LINK, requireActivity())
  }

  override fun onClickFacebook() {
    IntentAction.openLink(URLs.FACEBOOK_LINK, requireActivity())
  }

  override fun onClickLinkedIn() {
    IntentAction.openLink(URLs.LINKEDIN_LINK, requireActivity())
  }

  override fun onClickTermsAndConditions() {
    findNavController().navigate(
      R.id.pageFragment,
      PageFragment.getPageFragmentBundle("terms-and-conditions")
    )
  }

  override fun onClickTwitter() {
    IntentAction.openLink(URLs.TWITTER_LINK, requireActivity())
  }

}



