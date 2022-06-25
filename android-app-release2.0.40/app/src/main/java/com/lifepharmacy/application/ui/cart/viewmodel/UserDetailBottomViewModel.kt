package com.lifepharmacy.application.ui.cart.viewmodel

import android.app.Application
import android.os.Build
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.UserSignUpState
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.profile.UpdateUserRequestModel
import com.lifepharmacy.application.model.request.NumberOTPRequest
import com.lifepharmacy.application.model.request.NumberOTPVerifyRequest
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.AuthRepository
import com.lifepharmacy.application.utils.universal.InputEditTextValidator
import com.lifepharmacy.application.utils.universal.Utils
import com.onesignal.OneSignal

/**
 * Created by Zahid Ali
 */
class UserDetailBottomViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: AuthRepository,
  application: Application
) : BaseViewModel(application) {
  lateinit var phone: InputEditTextValidator
  var numberValid = ObservableField<Boolean>()
  var email: InputEditTextValidator
  var name: InputEditTextValidator
  var isFirstTime = ObservableField<Boolean>()
  var userState = ObservableField<UserSignUpState>()

  init {
    userState.set(UserSignUpState.SKIP_OR_EMAIL)
    var errorText = viewModelContext.getString(R.string.phoneErrorMessage)
    phone = InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.NON,
      true,
      object :
        InputEditTextValidator.InputEditTextValidatorCallBack {
        override fun onValueChange(validator: InputEditTextValidator?) {
          if (numberValid.get() != true) {
            phone.setError(errorText)
          }
        }
      },
      errorText
    )
    errorText = viewModelContext.getString(R.string.fieldError)
    name = InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      errorText
    )
    errorText = viewModelContext.getString(R.string.email_error)
    email = InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.EMAIL,
      true,
      null,
      errorText
    )
  }

  fun isOTPValid(otp: String?): Boolean {
    otp?.let {
      if (Utils.isNumberString(otp) && otp.length >= 4) return true
    }
    return false
  }

  fun sendOTP() =
    performNwOperation { repository.sendOTP(makeNumberOtpRequestModel()) }

  fun updateInfoAndSendOTP() =
    performNwOperation { repository.updateAndSendOTP(makeMissingRequestModel()) }

  private fun makeNumberOtpRequestModel(): NumberOTPRequest {
    return NumberOTPRequest(
      phone = Utils.formatNumberToSimple(phone.getValue())
    )
  }

  //  private fun makeNumberOtpRequestModel(): NumberOTPRequest {
//    return NumberOTPRequest(
//      phone = Utils.formatNumberToSimple(phone.getValue()),
//      name = name.getValue(),
//      email = email.getValue()
//    )
//  }
  private fun makeMissingRequestModel(): NumberOTPRequest {
    return NumberOTPRequest(
      phone = Utils.formatNumberToSimple(phone.getValue()),
      name = name.getValue()
    )
  }

  fun updateUser() =
    performNwOperation { repository.updateUser(makeUpdateUserModel()) }


  private fun makeUpdateUserModel(): UpdateUserRequestModel {
    return UpdateUserRequestModel(
      email = email.getValue(),
      name = name.getValue()
    )
  }

  fun verifyOTP(otp: String) =
    performNwOperation { repository.verifyOTP(makeVerifyNumberOtpRequestModel(otp)) }

  private fun makeVerifyNumberOtpRequestModel(otp: String): NumberOTPVerifyRequest {
    val version = Build.MODEL

    val device = OneSignal.getDeviceState()
    val userId = device?.userId
    return NumberOTPVerifyRequest(
      code = otp.toInt(),
      deviceName = version,
      deviceId = userId ?: "",
      phone = Utils.formatNumberToSimple(phone.getValue())
    )
  }
}