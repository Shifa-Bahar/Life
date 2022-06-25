package com.lifepharmacy.application.ui.auth.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentFilter
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.maps.model.LatLng
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.broadCastReciever.ReadSms
import com.lifepharmacy.application.broadCastReciever.SmsBroadcastReceiver
import com.lifepharmacy.application.databinding.FragmentOtpBinding
import com.lifepharmacy.application.interfaces.ClickToolBarTrans
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.auth.viewmodel.AuthViewModel
import com.lifepharmacy.application.ui.dashboard.DashboardActivity
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.broadCastReciever.SmsBroadcastReceiver.SmsBroadcastReceiverListener
import com.lifepharmacy.application.managers.LocationCustomManager
import com.lifepharmacy.application.managers.analytics.otpScreenOpen
import com.lifepharmacy.application.managers.analytics.userVerified
import com.lifepharmacy.application.managers.analytics.verifyButtonClicked
import com.lifepharmacy.application.utils.universal.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class OTPFragment : BaseFragment<FragmentOtpBinding>(), ClickOTPFragment, ClickToolBarTrans,
  LocationCustomManager.CustomeLocationCallback {

  companion object {
    fun makBundle(sentTo: String, title: String, isPhone: Boolean = true): Bundle {
      val bundle = Bundle()
      bundle.putString("sendTo", sentTo)
      bundle.putString("title", title)
      bundle.putBoolean("isPhone", isPhone)
      return bundle
    }
  }

  private val REQ_USER_CONSENT = 200
  var smsBroadcastReceiver: SmsBroadcastReceiver? = null
  private var isLocationChangeFoundNearest = true
  private val viewModel: AuthViewModel by activityViewModels()
  private val viewModelProfile: ProfileViewModel by activityViewModels()
  private var sentTo: String? = ""
  private var title: String? = ""
  private var isPhone: Boolean = true
  private var readSms: ReadSms? = null
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.otpScreenOpen()
    mView = super.onCreateView(inflater, container, savedInstanceState)
    arguments?.let {
      sentTo = it.getString("sendTo")
      title = it.getString("title")
      isPhone = it.getBoolean("isPhone", true)
    }
//    requestSpecificPermission(
//      ConstantsUtil.PERMISSION_READ_SMS,
//      ConstantsUtil.requestPermissionReadSMS
//    )
    initUI()
    observers()
//    readSMS()
    startSmsUserConsent()
    return mView
  }

  fun initUI() {
    binding.click = this
    binding.tollBar.click = this
    binding.tollBar.title = title
    binding.value = " $sentTo"
    binding.pinView.requestFocus()
    KeyboardUtils.showKeyboard(requireActivity())
    startCounter()

  }


  private fun observers() {
    binding.pinView.addTextChangedListener(EditTextWatcher(object : EditTextCallBack {
      override fun onTextChange(text: String?) {
        text?.let {
          if (text.length == 4) {
            KeyboardUtils.hideKeyboard(requireActivity(), binding.pinView)
            verifyLogin()
          }
        }
      }
    }))
  }


  private fun verifyLogin() {
    if (viewModel.isOTPValid(binding.pinView.text.toString())) {
      viewModel.appManager.analyticsManagers.verifyButtonClicked()
      viewModel.verifyOTP(binding.pinView.text.toString())
        .observe(viewLifecycleOwner, Observer {
          when (it.status) {
            Result.Status.SUCCESS -> {
              it.data?.data?.token?.let { token ->
                viewModel.appManager.persistenceManager.saveToken(
                  token
                )
              }
              it.data?.data?.user?.let { user ->
                viewModel.appManager.persistenceManager.saveLoggedInUser(user)
              }
              hideLoading()
              viewModelProfile.isLoggedIn.set(true)
              it.data?.data?.let { verifyOtp ->
                viewModel.appManager.analyticsManagers.userVerified(verifyOtp, isPhone)
              }
              DashboardActivity.open(requireActivity())
            }
            Result.Status.ERROR -> {
              hideLoading()
              it.message?.let { it1 ->
                AlertManager.showErrorMessage(
                  requireActivity(),
                  it1
                )
              }
            }
            Result.Status.LOADING -> {
              showLoading()
            }
          }
        })
    } else {
      Toast.makeText(requireContext(), getString(R.string.invalid_otp), Toast.LENGTH_SHORT)
        .show()
    }
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_otp
  }

  override fun permissionGranted(requestCode: Int) {
    if (requestCode == ConstantsUtil.PERMISSION_READ_SMS) {
      readSMS()
    }
  }

  override fun onCLickLogin() {
    verifyLogin()

  }

  override fun onClickSendAgain() {
    viewModel.resendOTP()
    startCounter()

  }

  override fun onClickBack() {
    requireActivity().onBackPressed()
  }

  private fun readSMS() {

    val intentFilter =
      IntentFilter("android.provider.Telephony.SMS_RECEIVED")
    intentFilter.priority = 1000
    requireActivity().registerReceiver(readSms, intentFilter)
    try {
      // read the sms, on  sms receive. then split message and set otp to the edit test.and check if it is 5 digit then calling verification service

      readSms = object : ReadSms() {
        override fun onSmsReceived(s: String?) {
//          Toast.makeText(requireContext(),"$s",Toast.LENGTH_SHORT).show()
          if (s != null) {
            val subStringFromMessage = s.replace("[^0-9]".toRegex(), "")
            if (subStringFromMessage.length >= 4) {
              val digitString = subStringFromMessage.subSequence(0, 4)
              if (Utils.isNumberString(digitString.toString())) {
                binding.pinView.setText(digitString)
              }
            }
          }
        }

      }

    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun startSmsUserConsent() {
    val client = SmsRetriever.getClient(requireActivity())
    //We can add sender phone number or leave it blank
    // I'm adding null here
    client.startSmsUserConsent(null)
      .addOnSuccessListener {
//        Toast.makeText(
//          requireContext(),
//          "On Success",
//          Toast.LENGTH_LONG
//        ).show()
      }.addOnFailureListener {
//        Toast.makeText(
//          requireContext(),
//          "On OnFailure",
//          Toast.LENGTH_LONG
//        ).show()
      }
  }

  private fun registerBroadcastReceiver() {
    smsBroadcastReceiver =
      SmsBroadcastReceiver()
    smsBroadcastReceiver!!.smsBroadcastReceiverListener = object : SmsBroadcastReceiverListener {
      override fun onSuccess(intent: Intent) {
        startActivityForResult(intent, REQ_USER_CONSENT)
      }

      override fun onFailure() {}
    }
    val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    requireActivity().registerReceiver(smsBroadcastReceiver, intentFilter)
  }

  override fun onStart() {
    super.onStart()
    registerBroadcastReceiver()
  }

  override fun onStop() {
    super.onStop()
    requireActivity().unregisterReceiver(smsBroadcastReceiver)
  }

  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    @Nullable data: Intent?
  ) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQ_USER_CONSENT) {
      if (resultCode == RESULT_OK && data != null) {
        //That gives all message to us.
        // We need to get the code from inside with regex
        val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
//        Toast.makeText(
//          requireContext(),
//          message,
//          Toast.LENGTH_LONG
//        ).show()
        if (message != null) {
          getOtpFromMessage(message)
        }
      }
    }
  }

  private fun getOtpFromMessage(message: String) {
    // This will match any 6 digit number in the message
    val pattern: Pattern = Pattern.compile("(|^)\\d{6}")
    val matcher: Matcher = pattern.matcher(message)
    val subStringFromMessage = message.replace("[^0-9]".toRegex(), "")
    if (subStringFromMessage.length >= 4) {
      val digitString = subStringFromMessage.subSequence(0, 4)
      if (Utils.isNumberString(digitString.toString())) {
        binding.pinView.setText(digitString)
      }
    }
  }

  private fun startCounter() {
    object : CountDownTimer(30000, 1000) {
      override fun onTick(millisUntilFinished: Long) {

        binding.tvCountDown.text = "in 00:" + millisUntilFinished / 1000 + ""
        binding.tvSendAgain.isEnabled = false
      }

      override fun onFinish() {
        try {
          binding.tvSendAgain.isEnabled = true
        } catch (e: java.lang.Exception) {

        }

      }
    }.start()
  }

  override fun onChange(location: Location?) {

  }

  override fun address(address: Address?) {
    appManager.storageManagers.saveLatLng(
      LatLng(
        address?.latitude ?: 0.0,
        address?.longitude ?: 0.0
      )
    )
  }
}
