package com.lifepharmacy.application.ui.profile.viewmodel

import android.app.Application
import android.os.Build
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.UserSignUpState
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.managers.MediaManagerKT
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.profile.UpdateUserRequestModel
import com.lifepharmacy.application.model.request.NumberOTPRequest
import com.lifepharmacy.application.model.request.NumberOTPVerifyRequest
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.AuthRepository
import com.lifepharmacy.application.repository.CategoryRepository
import com.lifepharmacy.application.repository.FileRepository
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import com.lifepharmacy.application.utils.universal.InputEditTextValidator
import com.lifepharmacy.application.utils.universal.Utils
import com.onesignal.OneSignal
import retrofit2.Response
import java.io.File

/**
 * Created by Zahid Ali
 */
class ProfileViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: CategoryRepository,
  private val authRepository: AuthRepository,
  private val fileRepository: FileRepository,
  application: Application
) : BaseViewModel(application) {
  //  var collapesItems = ArrayList<Int>()
  lateinit var phone: InputEditTextValidator
  var numberValid = ObservableField<Boolean>()
  var isLoggedIn = ObservableField<Boolean>()
  var email: InputEditTextValidator
  var name: InputEditTextValidator
  var isPhoneVerified = ObservableField<Boolean>()
  var isCodeSend = ObservableField<Boolean>()
  var uploadedFile = ""
  var userMut = MutableLiveData<Result<VerifyOTPResponse>>()
  var userObjectMut = MutableLiveData<User>()
  var gender = MutableLiveData<String>()
  var dateOfBirth = MutableLiveData<String>()
  var isArabicEnabled = MutableLiveData<Boolean>()
  var isoutletEnabled: Boolean? = appManager.storageManagers.config.outletENABLED
  var userState = ObservableField<UserSignUpState>()

  init {
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
    isArabicEnabled.value = appManager.storageManagers.config.arabicEnabled
  }

  fun uploadFile(file: File) =
    performNwOperation {
      fileRepository.uploadImage(
        MediaManagerKT.makeFilePart(
          MediaManagerKT.getCompressedPhoto(
            file,
            viewModelContext
          )
        )
      )
    }

  fun isOTPValid(otp: String?): Boolean {
    otp?.let {
      if (Utils.isNumberString(otp) && otp.length >= 4) return true
    }
    return false
  }

  fun sendOTP() =
    performNwOperation { authRepository.sendOTP(makeNumberOtpRequestModel()) }

  fun updateInfoAndSendOTP() =
    performNwOperation { authRepository.updateAndSendOTP(makeMissingRequestModel()) }

  private fun makeNumberOtpRequestModel(): NumberOTPRequest {
    return NumberOTPRequest(
      phone = Utils.formatNumberToSimple(phone.getValue()),
      name = name.getValue(),
      email = email.getValue()
    )
  }

  private fun makeMissingRequestModel(): NumberOTPRequest {
    return NumberOTPRequest(
      phone = Utils.formatNumberToSimple(phone.getValue()),
      name = name.getValue()
    )
  }

  fun updateUser() =
    performNwOperation { authRepository.updateUser(makeUpdateUserModel()) }


  private fun makeUpdateUserModel(): UpdateUserRequestModel {
    return UpdateUserRequestModel(
      email = email.getValue(),
      name = name.getValue(),
      photo = uploadedFile,
      gender = Utils.getGenderKeyForSend(gender.value ?: "Male"),
      dob = dateOfBirth.value
    )
  }

  fun verifyOTP(otp: String) =
    performNwOperation { authRepository.verifyOTP(makeVerifyNumberOtpRequestModel(otp)) }

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
//
//  fun setFirstColapsedItems(item: Int) {
//    if (collapesItems.contains(item)) {
//      collapesItems.remove(item)
//    } else {
//      collapesItems.add(item)
//    }
//  }

//  fun setAllColasped(int: Int) {
//    for (i in 0..int) {
//      setFirstColapsedItems(i)
//    }
//  }

  fun updateCurrentUser() {
    if (appManager.persistenceManager.isLoggedIn()) {
      authRepository.updateUser(object :
        HandleNetworkCallBack<GeneralResponseModel<VerifyOTPResponse>> {
        override fun handleWebserviceCallBackSuccess(response: Response<GeneralResponseModel<VerifyOTPResponse>>) {
          response.body()?.data?.token?.let { token ->
            appManager.persistenceManager.saveToken(
              token
            )
          }
          response.body()?.data?.user?.let { user ->
            appManager.persistenceManager.saveLoggedInUser(user)
          }
          userMut.value =
            Result(Result.Status.SUCCESS, response.body()?.data, response.body()?.message)
          userObjectMut.value = response.body()?.data?.user
        }

        override fun handleWebserviceCallBackFailure(error: String?) {
          userMut.value = Result(Result.Status.ERROR, null, error)
        }

      })
    }
  }
}