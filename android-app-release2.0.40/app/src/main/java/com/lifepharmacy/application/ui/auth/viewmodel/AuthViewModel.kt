package com.lifepharmacy.application.ui.auth.viewmodel

import android.app.Application
import android.os.Build
import androidx.hilt.lifecycle.ViewModelInject
import com.lifepharmacy.application.R
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.request.NumberOTPRequest
import com.lifepharmacy.application.model.request.NumberOTPVerifyRequest
import com.lifepharmacy.application.repository.AuthRepository
import com.lifepharmacy.application.utils.universal.InputEditTextValidator
import com.lifepharmacy.application.utils.universal.Utils
import com.lifepharmacy.application.utils.universal.ValidationUtils
import com.onesignal.OneSignal

/**
 * Created by Zahid Ali
 */
class AuthViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val authRepository: AuthRepository,
  application: Application
) : BaseViewModel(application) {

  //    private var userNumber = ""
  lateinit var phone: InputEditTextValidator
  var email: InputEditTextValidator
  var numberValid = false

  init {
    var errorText = viewModelContext.getString(R.string.phoneErrorMessage)
    phone =
      InputEditTextValidator(
        InputEditTextValidator.InputEditTextValidationsEnum.NON,
        true,
        object :
          InputEditTextValidator.InputEditTextValidatorCallBack {
          override fun onValueChange(validator: InputEditTextValidator?) {
            if (!numberValid) {
              phone.setError(errorText)
            }
          }
        },
        errorText
      )
    email =
      InputEditTextValidator(
        InputEditTextValidator.InputEditTextValidationsEnum.EMAIL,
        true,
        null,
        null
      )
  }

  //    fun setNumber(number: String){
//        userNumber = number
//    }
//    fun getNumber():String{
//        return userNumber
//    }
  fun isOTPValid(otp: String?): Boolean {
    otp?.let {
      if (Utils.isNumberString(otp) && otp.length >= 4) return true
    }
    return false
  }

  fun isEmailValid(email: String?): Boolean {
    email?.let {
      if (ValidationUtils.validateEmailAddress(it)) return true
    }
    return false
  }
//    fun getProducts() =
//        performNwOperation { authRepository.getProducts() }


  fun sendPhoneOTP() =
    performNwOperation { authRepository.sendOTP(makeNumberOtpRequestModel()) }

  fun resendOTP() =
    performNwOperation { authRepository.sendOTP(makeNumberOtpResendRequestModel()) }

  private fun makeNumberOtpRequestModel(): NumberOTPRequest {
    return NumberOTPRequest(phone = Utils.formatNumberToSimple(phone.getValue()))
  }

  private fun makeNumberOtpResendRequestModel(): NumberOTPRequest {
    return NumberOTPRequest(
      phone = Utils.formatNumberToSimple(phone.getValue()),
      smsChannel = "twilio"
    )
  }

  fun verifyOTP(otp: String) =
    performNwOperation { authRepository.verifyOTP(makeVerifyNumberOtpRequestModel(otp)) }


  fun sendEmailOTP() =
    performNwOperation { authRepository.sendOTP(makeNumberOtpRequestEmailModel()) }

  private fun makeNumberOtpRequestEmailModel(): NumberOTPRequest {
    return NumberOTPRequest(email = email.getValue())
  }

  private fun makeVerifyNumberOtpRequestModel(otp: String): NumberOTPVerifyRequest {
    val version = Build.MODEL
    val device = OneSignal.getDeviceState()
    val userId = device?.userId

    return NumberOTPVerifyRequest(
      code = otp.toInt(),
      deviceName = version,
      deviceId = userId ?: "",
      email = email.getValue(),
      phone = Utils.formatNumberToSimple(phone.getValue())
    )
  }

}