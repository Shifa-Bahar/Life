package com.lifepharmacy.application.ui.address.dailog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentAddressBinding
import com.lifepharmacy.application.enums.AddressChanged
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.addressListingScreenOpen
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.address.adapters.AddressAdapter
import com.lifepharmacy.application.ui.address.adapters.ClickItemAddress
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil.PERMISSION_LOCATIONS_REQUEST_CODE
import com.lifepharmacy.application.utils.universal.ConstantsUtil.RequiredPermissionsLocations
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class AddressFragment : BaseFragment<FragmentAddressBinding>(), ClickItemAddress,
  ClickAddressFragment, ClickTool {
  private val viewModel: AddressViewModel by activityViewModels()


  private val requestLocationPermissions =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
      var result = true
      granted.entries.forEach {
        if (!it.value) {
          result = false
          return@forEach
        }
      }
      if (result) {
        locationPermissionGranted()
      } else {
        AlertManager.permissionRequestPopup(requireActivity())
      }

    }

  /**
   * AddressAdapter from user address
   */
  lateinit var addressAdapter: AddressAdapter
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.addressListingScreenOpen()
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
    }
    viewModel.isEdit = false
    viewModel.addressChanged.value = AddressChanged.ADDRESS_UNCHANGED
    observers()
    viewModel.requestAddress()
    return mView
  }

  private fun initUI() {
    binding.tollBar.tvToolbarTitle.text = getString(R.string.address)
    binding.tollBar.click = this
    binding.click = this
    binding.isSelecting = viewModel.isSelecting
    initAddressRV()
  }

  private fun initAddressRV() {
    addressAdapter = AddressAdapter(requireActivity(), this, this)
    binding.rvAddress.adapter = addressAdapter
  }

  private fun observers() {
    viewModel.addressMainMut
      .observe(viewLifecycleOwner, {
        when (it.status) {
          Result.Status.SUCCESS -> {

            hideLoading()
            binding.showEmpty = it.data?.addresses.isNullOrEmpty()
            addressAdapter.setDataChanged(it.data?.addresses)
            it.data?.addresses?.let { it1 ->
              if (it1.isNotEmpty()) {
                val addressFromList =
                  it1.firstOrNull { address -> address.id == viewModel.deliveredAddressMut.value?.id }
                val indexAddress: Int = it1.indexOf(addressFromList)
                if (addressFromList != null && indexAddress >= 0) {
                  viewModel.setDeliveredAddress(
                    it1[indexAddress]
                  )
                  addressAdapter.selectedItem(indexAddress)
                  binding.rvAddress.smoothScrollToPosition(indexAddress + 1)
                } else {
                  viewModel.setDeliveredAddress(
                    it1[0]
                  )
                  addressAdapter.selectedItem(0)
                  binding.rvAddress.smoothScrollToPosition(1)
                }

              }
            }
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


  override fun getLayoutRes(): Int {
    return R.layout.fragment_address
  }

  override fun permissionGranted(requestCode: Int) {
  }

  private fun locationPermissionGranted() {
    try {
      findNavController().navigate(R.id.addNewAddressDialog)
    } catch (e: Exception) {
    }
  }

  override fun onClickAddress(address: AddressModel, position: Int?) {
    addressAdapter.selectedItem(position)
    viewModel.setDeliveredAddress(address)
  }

  override fun onClickDeleteAddress(address: AddressModel, position: Int?) {
    MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
      .setTitle(resources.getString(R.string.delete_address_title))
      .setMessage(resources.getString(R.string.delete_address_descr))
      .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
      }
      .setPositiveButton(resources.getString(R.string.delete_address)) { _, _ ->
        address.id?.let { it ->
          viewModel.deleteAddress(viewModel.makeDeleteAddressObject(it))
            .observe(viewLifecycleOwner, Observer { result ->
              when (result.status) {
                Result.Status.SUCCESS -> {
                  result.data?.message?.let { it1 ->
                    AlertManager.showSuccessMessage(
                      requireActivity(),
                      it1
                    )
                  }
                  if (position != null) {
                    refreshAddress(position)
                  }
                  hideLoading()
                }
                Result.Status.ERROR -> {
                  result.message?.let { it1 ->
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

      }
      .show()
  }

  override fun onClickEditAddress(address: AddressModel, position: Int?) {
    viewModel.setAddress(address)
    viewModel.isSetUserLocal = false
    requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
  }

  private fun refreshAddress(position: Int) {
//        addressAdapter.itemRemoved(position)
    viewModel.requestAddress()
  }

  override fun onConfirm() {
    viewModel.addressChanged.value = AddressChanged.ADDRESS_CHANGED
    viewModel.addressConfirmed.value = true
//    findNavController().navigateUp()
  }

  override fun onClickAddNew() {
    viewModel.isEdit = false
    viewModel.editAddress = AddressModel()
    viewModel.clearFields()
    requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

}
