package com.lifepharmacy.application.ui.auth.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentNumberBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.numberScreenOpen
import com.lifepharmacy.application.managers.analytics.requestOtp
import com.lifepharmacy.application.managers.analytics.userSkipRegistration
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.auth.viewmodel.AuthViewModel
import com.lifepharmacy.application.ui.dashboard.DashboardActivity
import com.lifepharmacy.application.ui.pages.fragment.PageFragment
import com.lifepharmacy.application.ui.splash.SplashActivity
import com.lifepharmacy.application.utils.universal.*
import com.lifepharmacy.application.utils.universal.ConstantsUtil.PERMISSION_LOCATIONS_REQUEST_CODE
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.PhoneNumberKit


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class NumberFragment : BaseFragment<FragmentNumberBinding>(), ClickNumberFragment {
  private val viewModel: AuthViewModel by activityViewModels()
  lateinit var phoneNumberKit: PhoneNumberKit
  private val requestLocationPermissions =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
      var result = true
      granted.entries.forEach {
        if (!it.value) {
          result = false
          return@forEach
        }
      }

    }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.numberScreenOpen()
    mView = super.onCreateView(inflater, container, savedInstanceState)
    requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
    initUI()
    observers()
    return mView
  }

  @SuppressLint("ClickableViewAccessibility")
  fun initUI() {
    binding.click = this
    binding.phone = viewModel.phone
    viewModel.phone.setEditText(binding.edPhone)
    phoneNumberKit = PhoneNumberKit(requireContext()) // Requires context

    phoneNumberKit.attachToInput(binding.lyPhone, "AE")
    phoneNumberKit.setupCountryPicker(activity as AppCompatActivity, searchEnabled = true)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      binding.edPhone.setAutofillHints(View.AUTOFILL_HINT_PHONE)
    }
    binding.isArabicEnabled = viewModel.appManager.storageManagers.config.arabicEnabled
    viewModel.appManager.persistenceManager.clearUserData()

//    requestSpecificPermission(
//      Constants.PERMISSION_NUMBER_REQUEST_COD,
//      Constants.getReadPhoneNumber()
//    )


    hideLoading()


    binding.edPhone.setOnTouchListener { v, event ->
//      val DRAWABLE_LEFT = 0
//      val DRAWABLE_TOP = 1
      val drawableRight = 2
//      val DRAWABLE_BOTTOM = 3
      if (event.rawX >= (binding.edPhone.right - binding.edPhone.compoundDrawables[drawableRight].bounds.width())) {
        if (!phoneNumberKit.isValid) {
          viewModel.phone.setValue("")
        }
        true
      } else false
    }
    setRadioButtonFromPref()
  }

  private fun setRadioButtonFromPref() {
    when (appManager.persistenceManager.getLang()) {
      "ar" -> {
        binding.btnArabic.isChecked = true
      }
      "en" -> {
        binding.btnEnglish.isChecked = true
      }
    }
  }

  private fun observers() {
    binding.edPhone.addTextChangedListener(EditTextWatcher(object : EditTextCallBack {
      override fun onTextChange(text: String?) {
        val tem = text.toString().substringAfter(" ")
        Logger.d("SubString", tem)
        val temDigit = tem.firstOrNull()
        temDigit?.let {
          if (it.toString() == "0") {
            Logger.d("SubString", text.toString().substringAfter(" "))
            val tem2 = text.toString().substringBefore(" ")
            Logger.d("SubString", tem2)
            viewModel.phone.setValue(tem2.trim())
          } else {
            viewModel.phone.setValue((text.toString()))
            viewModel.numberValid = phoneNumberKit.isValid
          }
        }
      }

    }))
    binding.segmented2.setOnCheckedChangeListener { group, checkedId ->
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


  override fun getLayoutRes(): Int {
    return R.layout.fragment_number
  }

  @SuppressLint("MissingPermission", "HardwareIds")
  override fun permissionGranted(requestCode: Int) {
    if (requestCode == PERMISSION_LOCATIONS_REQUEST_CODE) {

    }
//    if (requestCode ==Constants.PERMISSION_NUMBER_REQUEST_COD){
//      val manager: TelephonyManager = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//      val mPhoneNumber: String = manager.line1Number
//      Toast.makeText(requireContext(),mPhoneNumber,Toast.LENGTH_SHORT).show()
//
//    }
  }

  override fun onClickSkip() {
    viewModel.appManager.analyticsManagers.userSkipRegistration()
    DashboardActivity.open(requireActivity())
  }

  override fun onClickContinue() {
    if (viewModel.numberValid) {
      viewModel.appManager.analyticsManagers.requestOtp()
      viewModel.sendPhoneOTP()
        .observe(viewLifecycleOwner, Observer {
          when (it.status) {
            Result.Status.SUCCESS -> {
              hideLoading()
              findNavController().navigate(
                R.id.toOTP,
                OTPFragment.makBundle(viewModel.phone.getValue(), getString(R.string.verify_phone))
              )
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
    }
  }

  override fun onClickTroubleSignIn() {
    binding.isShowBottom = true
  }

  override fun onClickLoginUsingEmail() {
    try {
      findNavController().navigate(R.id.toEmailFragment)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun onClickTerms() {
    findNavController().navigate(
      R.id.pageFragment,
      PageFragment.getPageFragmentBundle("terms-and-conditions")
    )
  }

  override fun onClickPrivacy() {
    findNavController().navigate(
      R.id.pageFragment,
      PageFragment.getPageFragmentBundle("privacy-policy")
    )
  }

  override fun onClickContactUS() {
//    IntentAction.openChatActivity(requireActivity(), viewModel.appManager/
  }

  override fun onClickParent() {
    KeyboardUtils.hideKeyboard(requireActivity(), binding.edPhone)
  }

}
