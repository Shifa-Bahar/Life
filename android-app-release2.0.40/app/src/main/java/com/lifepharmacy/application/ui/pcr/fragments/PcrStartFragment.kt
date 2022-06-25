package com.lifepharmacy.application.ui.pcr.fragments

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
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentPcrStartBinding
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.managers.analytics.choosePrescriptionScreenOpen
import com.lifepharmacy.application.ui.address.AddressSelectionActivity
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.address.ClickSelectedAddress
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.pcr.viewmodel.PcrListingViewModel
import com.lifepharmacy.application.ui.prescription.fragments.ClickChoosePrescriptionFragment
import com.lifepharmacy.application.ui.prescription.fragments.ClickContactLayout
import com.lifepharmacy.application.ui.prescription.viewmodel.PrescriptionViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
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
class PcrStartFragment : BaseFragment<FragmentPcrStartBinding>(),
  ClickChoosePrescriptionFragment, ClickSelectedAddress, ClickContactLayout,
  ChatWindowView.ChatWindowEventsListener {
  companion object {
    fun getPcrListingBundle(
      id: String?
    ): Bundle {
      val bundle = Bundle()
      id?.let {
        bundle.putString("ids", id)
      }
      return bundle
    }
  }
  private val viewModel: PcrListingViewModel by activityViewModels()
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
    arguments?.let {
      viewModel.colid = it.getString("ids")
      Log.e(TAG, "onCreateView: ${viewModel.colid}")
//      viewModel.id = it.getString("id")
    }
//   viewModel.appManager.analyticsManagers.choosePrescriptionScreenOpen()
//    intentHandler = IntentHandler(requireContext(), this, viewModel.appManager.mediaManager)
//    intentStarter = IntentStarter(requireContext(), this)
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
//      viewModel.resetPrescription()
    }
    observers()
    proceedButtonStatus()
    return mView
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    proceedButtonStatus()
  }

  private fun initUI() {
    Log.e(TAG, "initUI:pcrfragment ")
   binding.viewModel = viewModel
    binding.profileViewModel = profileViewModel
    binding.lifecycleOwner = this
    binding.click = this
//    binding.clickContact = this
    binding.tollBar.isBackVisible = false
    binding.tollBar.tvToolbarTitle.text = getString(R.string.covidrtpcr)

  }


  private fun observers() {
    if (profileViewModel.isLoggedIn.get() != true) {
      findNavController().navigate(R.id.nav_login_sheet)
    }
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_pcr_start
  }

  override fun permissionGranted(requestCode: Int) {
//    if (requestCode == Constants.PERMISSION_PICTURE_REQUEST_CODE) {
//      intentStarter.openImageSelectionBottomSheet()
//    }
  }

  override fun onClickHavePrescription() {
    findNavController().navigate(
      R.id.pcrlistFragment, PcrListFragment.getPcrListingBundle(viewModel.colid)
    )
//    findNavController().navigate(R.id.pcrlistFragment)
  }

  override fun onClickDontHavePrescription() {

  }

  override fun onClickLogin() {
    findNavController().navigate(R.id.nav_login_sheet)
  }


  private fun proceedButtonStatus() {

  }

  override fun onClickChangeAddress() {

  }

  override fun onClickCall() {

  }

  override fun onClickChat() {

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

  }
}



