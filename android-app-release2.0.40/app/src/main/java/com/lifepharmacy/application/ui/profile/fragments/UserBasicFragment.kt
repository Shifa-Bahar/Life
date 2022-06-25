package com.lifepharmacy.application.ui.profile.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentUserBasicBinding
import com.lifepharmacy.application.enums.UserSignUpState
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.userBasicScreenOpen
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.utils.DateTimeUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.IntentHandler
import com.lifepharmacy.application.utils.IntentStarter
import com.lifepharmacy.application.utils.universal.EditTextCallBack
import com.lifepharmacy.application.utils.universal.EditTextWatcher
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.PhoneNumberKit
import java.io.File
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class UserBasicFragment : BaseFragment<FragmentUserBasicBinding>(),
  ClickBasicProfile {
  private val viewModel: ProfileViewModel by activityViewModels()
  lateinit var phoneNumberKit: PhoneNumberKit
  private var mYear = 0
  private var mMonth: Int = 0
  private var mDay: Int = 0
  var fromDateMilles: Long = 0
  private var mHour: Int = 0
  private var mMinute: Int = 0
  lateinit var intentHandler: IntentHandler
  lateinit var intentStarter: IntentStarter


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

  var userGlobal: User? = null
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    intentHandler = IntentHandler(requireContext(), this, viewModel.appManager.mediaManager)
    intentStarter = IntentStarter(requireContext(), this)
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
    }
    viewModel.appManager.analyticsManagers.userBasicScreenOpen()
    return mView
  }

  private fun initUI() {


    binding.click = this
    binding.mVieModel = viewModel
    binding.lifecycleOwner = this
    binding.numberValid = viewModel.numberValid
    binding.phone = viewModel.phone
    viewModel.phone.setEditText(binding.edPhone)
    binding.isPhoneVerified = viewModel.isPhoneVerified
    binding.name = viewModel.name
    viewModel.isCodeSend.set(false)
    viewModel.name.setEditText(binding.edName)
    viewModel.isCodeSend = viewModel.isCodeSend
    binding.email = viewModel.email
    viewModel.email.setEditText(binding.edEmail)

    phoneNumberKit = PhoneNumberKit(requireContext()) // Requires context


    phoneNumberKit.attachToInput(
      binding.lyPhone, viewModel.appManager.persistenceManager.getCountry()
        .capitalize(Locale.ROOT)
    )
    phoneNumberKit.setupCountryPicker(activity as AppCompatActivity)
    val user: User? = viewModel.appManager.persistenceManager.getLoggedInUser()
    user?.let {
      if (!it.phoneVerifiedAt.isNullOrBlank()) {
        viewModel.isPhoneVerified.set(true)
        viewModel.phone.setValue(it.phone)
      }
      if (!it.email.isNullOrBlank()) {
        viewModel.email.setValue(it.email)
      }
      if (!it.name.isNullOrBlank()) {
        viewModel.name.setValue(it.name)
      }
    }
  }


  private fun observers() {
    binding.edPhone.addTextChangedListener(
      EditTextWatcher(object : EditTextCallBack {
        override fun onTextChange(text: String?) {
          viewModel.phone.setValue(text.toString())
          viewModel.numberValid.set(phoneNumberKit.isValid)
        }
      })
    )
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_user_basic
  }

  override fun permissionGranted(requestCode: Int) {

  }

  fun handlePicturePermissions() {
    intentStarter.openImageSelectionBottomSheet()
  }

  override fun onClickUploadImage() {

    requestPhotoPermissions.launch(ConstantsUtil.RequiredPermissionsPicture)

  }

  override fun onClickDOB() {
    openDatePicker()
  }

  override fun onClickGender() {
    findNavController().navigate(R.id.genderSelectionBottomSheet)
  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

  override fun onClickSave() {
    updateUser()
//    viewModel.userState.get()?.let {
//      when (it) {
//        UserSignUpState.SKIP_OR_EMAIL -> {
//          findNavController().navigate(R.id.userDetailsBottomSheet)
//        }
//        UserSignUpState.UPDATE_PROFILE -> {
//          updateUser()
//        }
//        UserSignUpState.OTP -> {
////          verifyLogin()
//        }
//
//      }
//    }
  }

  override fun onClickDetailsProceed() {

  }

  override fun onClickOTPProceed() {
//    verifyLogin()
  }

  override fun onClickEditDetails() {
    viewModel.isCodeSend.set(false)

  }

  override fun onClickResend() {
//    viewModel.sendOTP()
//    object : CountDownTimer(60000, 1000) {
//      override fun onTick(millisUntilFinished: Long) {
//        binding.llOtp.tvCountDown.text = "in 00:" + millisUntilFinished / 1000 + ""
//        binding.llOtp.tvSendAgain.isEnabled = false
//      }
//
//      override fun onFinish() {
//        try {
//          binding.llOtp.tvSendAgain.isEnabled = true
//        } catch (e: java.lang.Exception) {
//
//        }
//
//      }
//    }.start()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
//
//    if (data != null) {
    if (resultCode == Activity.RESULT_OK) {
      when (requestCode) {
        ConstantsUtil.GALLERY_PIC -> {
          val file = intentHandler.handleGalleryIntent(data)
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
//
//          Constants.GALLERY_PIC -> {
//            intentHandler.handleGalleryIntent(data)
//
//          }
//          Constants.CAMERA_PIC -> {
//            intentStarter.photoFile?.let {
//              MediaManagerKT.startCropImage(
//                it,
//                requireContext(),
//                this@UserBasicFragment
//              )
//            }
//          }
        ConstantsUtil.CROP_IMAGE -> {
          val resultUri = UCrop.getOutput(data!!)
          if (resultUri != null) {
            uploadFile(resultUri.toFile())
          }
        }
      }
    }
//    }

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

//  private fun sendOTP() {
//    if (viewModel.numberValid.get() == true) {
//      viewModel.sendOTP().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//        it?.let {
//          when (it.status) {
//            Result.Status.SUCCESS -> {
//              hideLoading()
//              binding.llOtp.value =
//                Utils.formatNumberToSimple(viewModel.phone.getValue())
//              viewModel.userState.set(UserCurrentState.OTP)
//            }
//            Result.Status.ERROR -> {
//              it.message?.let { it1 ->
//                AlertManager.showErrorMessage(
//                  requireActivity(),
//                  it1
//                )
//              }
//              hideLoading()
//            }
//            Result.Status.LOADING -> {
//              showLoading()
//            }
//          }
//        }
//
//      })
//    }
//  }

//  private fun verifyLogin() {
//    if (viewModel.isOTPValid(binding.llOtp.pinView.text.toString())) {
//      viewModel.verifyOTP(binding.llOtp.pinView.text.toString()).observe(viewLifecycleOwner,
//        androidx.lifecycle.Observer {
//          it?.let {
//            when (it.status) {
//              Result.Status.SUCCESS -> {
//                it.data?.data?.let { it1 -> updateLocalUser(it1) }
//                hideLoading()
//              }
//              Result.Status.ERROR -> {
//                hideLoading()
//                it.message?.let { it1 ->
//                  AlertManager.showErrorMessage(
//                    requireActivity(),
//                    it1
//                  )
//                }
//              }
//              Result.Status.LOADING -> {
//                showLoading()
//              }
//            }
//          }
//
//        })
//
//    } else {
//      Toast.makeText(requireContext(), getString(R.string.invalid_otp), Toast.LENGTH_SHORT)
//        .show()
//    }
//  }

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

  private fun uploadFile(file: File) {
    if (file != null && file.exists()) {
      file?.let { fileInternal ->
        viewModel.uploadFile(fileInternal).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
          it?.let {
            when (it.status) {
              Result.Status.SUCCESS -> {
                hideLoading()
                viewModel.uploadedFile = it.data?.data?.file.toString()
                Glide.with(requireContext())
                  .load(viewModel.uploadedFile)
                  .into(binding.ivProfile)
                updateUser()
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

  private fun updateLocalUser(responseData: VerifyOTPResponse) {
//    responseData.token?.let { token ->
//      viewModel.appManager.persistenceManager.saveToken(
//        token
//      )
//    }
    responseData.user.let { user ->
      viewModel.appManager.persistenceManager.saveLoggedInUser(user)
      userGlobal = viewModel.appManager.persistenceManager.getLoggedInUser()
      viewModel.userObjectMut.value = userGlobal
    }
    viewModel.isLoggedIn.set(true)
    setUserState()
  }

  private fun setUserState() {
    if (userGlobal != null) {
      if (!userGlobal!!.phoneVerifiedAt.isNullOrBlank() && (userGlobal!!.name.isNullOrEmpty() || userGlobal!!.email.isNullOrEmpty())) {
        viewModel.userState.set(UserSignUpState.UPDATE_PROFILE)
      } else if (!userGlobal!!.email.isNullOrBlank()) {
        viewModel.userState.set(UserSignUpState.SKIP_OR_EMAIL)
      } else {
        findNavController().navigateUp()
        findNavController().navigate(R.id.nav_address)
      }
    } else {
      viewModel.userState.set(UserSignUpState.SKIP_OR_EMAIL)
    }
  }

  private fun openDatePicker() {
    val c = Calendar.getInstance()
    mYear = c[Calendar.YEAR]
    mMonth = c[Calendar.MONTH]
    mDay = c[Calendar.DAY_OF_MONTH]
    fromDateMilles = DateTimeUtil.getTimesInMilles(DateTimeUtil.getFirstDateOfCurrentMonth())
    val datePickerDialog = DatePickerDialog(
      requireContext(),
      { view, year, monthOfYear, dayOfMonth ->
        val date =
          year.toString() + "-" + (monthOfYear + 1).toString() + "-" + dayOfMonth.toString()

        viewModel.dateOfBirth.value = date


      }, mYear, mMonth, mDay
    )
    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    datePickerDialog.show()
  }
}



