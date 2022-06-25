package com.lifepharmacy.application.ui.cart.fragmets

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomWithLoading
import com.lifepharmacy.application.databinding.BottomSheetUserDetailsBinding
import com.lifepharmacy.application.enums.UserSignUpState
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.cart.viewmodel.UserDetailBottomViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.broadCastReciever.ReadSms
import com.lifepharmacy.application.broadCastReciever.SmsBroadcastReceiver
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.ui.address.AddressSelectionActivity
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.utils.universal.*
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.PhoneNumberKit
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class UserDetailsBottomSheet : BaseBottomWithLoading<BottomSheetUserDetailsBinding>(),
  ClickUserDetailBottom {
  private val viewModel: UserDetailBottomViewModel by activityViewModels()
  private val viewModelProfile: ProfileViewModel by activityViewModels()
  private val viewModelAddress: AddressViewModel by activityViewModels()
  lateinit var phoneNumberKit: PhoneNumberKit
  private var readSms: ReadSms? = null
  private val REQ_USER_CONSENT = 200
  var smsBroadcastReceiver: SmsBroadcastReceiver? = null
  var userGlobal: User? = null
  private val addressContract =
    registerForActivityResult(AddressSelectionActivity.Contract()) { result ->
      result?.address?.let {
        viewModelAddress.deliveredAddressMut.value = it
        viewModelAddress.addressSelection = AddressSelection.NON
      }
    }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCancelable = true
    initLayout()
    observers()

  }

  private fun initLayout() {
    binding.state = viewModel.userState
    binding.llPersonalDetails.state = viewModel.userState
    binding.llPersonalDetails.lifecycleOwner = this
    binding.llPersonalDetails.click = this
    binding.llOtp.click = this
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.llPersonalDetails.numberValid = viewModel.numberValid
    binding.llPersonalDetails.isFirstTime = viewModel.isFirstTime

    viewModel.isFirstTime.set(true)

    binding.llPersonalDetails.phone = viewModel.phone
    viewModel.phone.setEditText(binding.llPersonalDetails.edPhone)

    binding.llPersonalDetails.name = viewModel.name
    viewModel.name.setEditText(binding.llPersonalDetails.edName)


    binding.llPersonalDetails.email = viewModel.email
    viewModel.email.setEditText(binding.llPersonalDetails.edEmail)

    phoneNumberKit = PhoneNumberKit(requireContext()) // Requires context


    phoneNumberKit.attachToInput(
      binding.llPersonalDetails.lyPhone, viewModel.appManager.persistenceManager.getCountry()
        .capitalize(Locale.ROOT)
    )
    phoneNumberKit.setupCountryPicker(activity as AppCompatActivity, searchEnabled = true)
    userGlobal = viewModel.appManager.persistenceManager.getLoggedInUser()
    if (userGlobal?.email?.isNotBlank() == true) {
      viewModel.email.setValue(userGlobal!!.email)
    }
    setUserState()

  }

  private fun observers() {
    binding.llPersonalDetails.edPhone.addTextChangedListener(
      EditTextWatcher(object : EditTextCallBack {
        override fun onTextChange(text: String?) {
          if (viewModel.userState.get() == UserSignUpState.SKIP_OR_EMAIL) {

            var tem = text.toString().substringAfter(" ")
            Logger.d("SubString", tem)
            var temDigit = tem.firstOrNull()
            temDigit?.let {
              if (it.toString() == "0") {
                Logger.d("SubString", text.toString().substringAfter(" "))
                val tem2 = text.toString().substringBefore(" ")
                Logger.d("SubString", tem2)
                viewModel.phone.setValue(tem2.trim())
              } else {
                viewModel.phone.setValue(text.toString())
                viewModel.numberValid.set(phoneNumberKit.isValid)
              }
            }


          }
        }
      })
    )
    binding.llOtp.pinView.addTextChangedListener(EditTextWatcher(object : EditTextCallBack {
      override fun onTextChange(text: String?) {
        text?.let {
          if (text.length == 4) {
            KeyboardUtils.hideKeyboard(requireActivity(), binding.llOtp.pinView)
            verifyLogin()
          }
        }
      }

    }))
  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_user_details
  }

  override fun permissionGranted(requestCode: Int) {
    if (requestCode == ConstantsUtil.PERMISSION_READ_SMS) {
//      readSMS()
      startSmsUserConsent()
    }
  }


  override fun onClickDetailsProceed() {
    viewModel.userState.get()?.let {
      when (it) {
        UserSignUpState.SKIP_OR_EMAIL -> {
          sendOTP()
        }
        UserSignUpState.UPDATE_PROFILE -> {
          updateUser()
        }
        UserSignUpState.OTP -> {
          verifyLogin()
        }

      }
    }
  }

  override fun onClickOTPProceed() {

  }

  override fun onClickEditDetails() {
    setUserState()
  }

  override fun onClickResend() {
    viewModel.sendOTP()
    startCounter()

  }

  override fun onStart() {
    super.onStart()
    registerBroadcastReceiver()
  }

  override fun onStop() {
    super.onStop()
    requireActivity().unregisterReceiver(smsBroadcastReceiver)
  }

  private fun updateUser() {
    viewModel.updateUser().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.data?.data?.let { it1 -> updateLocalUser(it1) }
            hideLoading()
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

  private fun sendOTP() {
    if (viewModel.numberValid.get() == true) {
      viewModel.sendOTP().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
        it?.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
              hideLoading()
              binding.llOtp.value =
                Utils.formatNumberToSimple(viewModel.phone.getValue())
              viewModel.userState.set(UserSignUpState.OTP)
              startCounter()
              startSmsUserConsent()
//              requestSpecificPermission(
//                ConstantsUtil.PERMISSION_READ_SMS,
//                ConstantsUtil.requestPermissionReadSMS
//              )
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

  private fun verifyLogin() {
    if (viewModel.isOTPValid(binding.llOtp.pinView.text.toString())) {
      viewModel.verifyOTP(binding.llOtp.pinView.text.toString()).observe(viewLifecycleOwner,
        androidx.lifecycle.Observer {
          it?.let {
            when (it.status) {
              Result.Status.SUCCESS -> {
                it.data?.data?.let { it1 ->
                  updateToken(it1)
                  updateLocalUser(it1)
                }
                hideLoading()
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
          }

        })

    } else {
      Toast.makeText(requireContext(), getString(R.string.invalid_otp), Toast.LENGTH_SHORT)
        .show()
    }
  }
//
//  private fun updateInfoAndOTP() {
//    viewModel.updateInfoAndSendOTP().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//      it?.let {
//        when (it.status) {
//          Result.Status.SUCCESS -> {
//            hideLoading()
//            binding.llOtp.value =
//              Utils.formatNumberToSimple(viewModel.phone.getValue())
//            viewModel.userState.set(UserCurrentState.OTP)
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
//
//    })
//  }

  private fun updateLocalUser(responseData: VerifyOTPResponse) {

    responseData.user.let { user ->
      viewModel.appManager.persistenceManager.saveLoggedInUser(user)
      userGlobal = viewModel.appManager.persistenceManager.getLoggedInUser()
    }
    viewModelProfile.isLoggedIn.set(true)
    setUserState()
  }

  private fun updateToken(responseData: VerifyOTPResponse) {
    responseData.token?.let { token ->
      viewModel.appManager.persistenceManager.saveToken(
        token
      )
    }
  }

  private fun setUserState() {
    if (userGlobal != null) {
      if (!userGlobal!!.phoneVerifiedAt.isNullOrBlank() && (userGlobal!!.name.isNullOrEmpty() || userGlobal!!.email.isNullOrEmpty())) {
        viewModel.phone.setError(false)
        viewModel.numberValid.set(true)
        viewModel.userState.set(UserSignUpState.UPDATE_PROFILE)
        viewModel.numberValid.set(true)
      } else {
        viewModelAddress.isSelecting.set(true)
        findNavController().navigateUp()
        addressContract.launch(true)
      }
    } else {
      viewModel.email.setError(false)
      viewModel.name.setError(false)
      viewModel.userState.set(UserSignUpState.SKIP_OR_EMAIL)
    }
  }

  override fun getLoadingLayout(): ConstraintLayout {
    return binding.llLoading.clLoading
  }

  private fun readSMS() {
    try {
      // read the sms, on  sms receive. then split message and set otp to the edit test.and check if it is 5 digit then calling verification service

      readSms = object : ReadSms() {
        override fun onSmsReceived(s: String?) {
          if (s != null) {
            val subStringFromMessage = s.replace("[^0-9]".toRegex(), "")
            if (s.length >= 4) {
              val digitString = subStringFromMessage.subSequence(0, 4)
              if (Utils.isNumberString(digitString.toString())) {
                binding.llOtp.pinView.setText(s)
              }
            }
          }
        }

      }

      val intentFilter =
        IntentFilter("android.provider.Telephony.SMS_RECEIVED")
      intentFilter.priority = 1000
      requireActivity().registerReceiver(readSms, intentFilter)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun startCounter() {
    object : CountDownTimer(60000, 1000) {
      override fun onTick(millisUntilFinished: Long) {
        binding.llOtp.tvCountDown.visibility = View.VISIBLE
        binding.llOtp.tvCountDown.text = "in 00:" + millisUntilFinished / 1000 + ""
        binding.llOtp.tvSendAgain.isEnabled = false
      }

      override fun onFinish() {
        try {
          binding.llOtp.tvCountDown.visibility = View.GONE
          binding.llOtp.tvSendAgain.isEnabled = true
        } catch (e: java.lang.Exception) {

        }

      }
    }.start()
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
    smsBroadcastReceiver!!.smsBroadcastReceiverListener = object :
      SmsBroadcastReceiver.SmsBroadcastReceiverListener {
      override fun onSuccess(intent: Intent) {
        startActivityForResult(intent, REQ_USER_CONSENT)
      }

      override fun onFailure() {}
    }
    val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    requireActivity().registerReceiver(smsBroadcastReceiver, intentFilter)
  }

  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    @Nullable data: Intent?
  ) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQ_USER_CONSENT) {
      if (resultCode == Activity.RESULT_OK && data != null) {
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
        binding.llOtp.pinView.setText(digitString)
      }
    }
  }
}