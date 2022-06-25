package com.lifepharmacy.application.ui.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentEnterEmailBinding
import com.lifepharmacy.application.interfaces.ClickToolBarTrans
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.emailScreenOpen
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.auth.viewmodel.AuthViewModel
import com.lifepharmacy.application.utils.universal.ConstantsUtil.PERMISSION_LOCATIONS_REQUEST_CODE
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class EmailFragment : BaseFragment<FragmentEnterEmailBinding>(), ClickEmailFragment,
  ClickToolBarTrans {
  private val viewModel: AuthViewModel by activityViewModels()
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.emailScreenOpen()
    mView = super.onCreateView(inflater, container, savedInstanceState)
    initUI()
    observers()
    return mView
  }

  fun initUI() {
    binding.click = this
    binding.lyTopBar.click = this
    binding.lyTopBar.title = "Enter the details"
    binding.email = viewModel.email
    viewModel.email.setEditText(binding.edEmail)

  }


  private fun observers() {

  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_enter_email
  }

  override fun permissionGranted(requestCode: Int) {
    if (requestCode == PERMISSION_LOCATIONS_REQUEST_CODE) {

    }
  }

  override fun onClickContinue() {
//        CoroutineScope(Dispatchers.Main.immediate).launch {
//            delay(1000)
    if (viewModel.isEmailValid(viewModel.email.getValue())) {
      viewModel.sendEmailOTP()
        .observe(viewLifecycleOwner, Observer {
          when (it.status) {
            Result.Status.SUCCESS -> {
              findNavController().navigate(
                R.id.toOTP,
                OTPFragment.makBundle(
                  viewModel.email.getValue(),
                  getString(R.string.verify_email),
                  false
                )
              )
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
        })
    }
//        }

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

}
