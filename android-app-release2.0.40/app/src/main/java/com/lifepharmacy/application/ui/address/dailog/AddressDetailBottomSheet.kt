package com.lifepharmacy.application.ui.address.dailog

import android.location.Address
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.gson.Gson
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseMapFragment
import com.lifepharmacy.application.databinding.BottomSheetAddressBinding
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.addressDetailScreenOpen
import com.lifepharmacy.application.model.address.AddressTypeModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.universal.EditTextCallBack
import com.lifepharmacy.application.utils.universal.EditTextWatcher
import com.lifepharmacy.application.utils.universal.Logger
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.PhoneNumberKit


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class AddressDetailBottomSheet : BaseMapFragment<BottomSheetAddressBinding>(), OnMapReadyCallback,
  ClickAddressBottomSheet, ClickTool {
  private val viewModel: AddressViewModel by activityViewModels()
  lateinit var phoneNumberKit: PhoneNumberKit

  //    lateinit var addressTypeSpinnerAdapter: AddressSpinnerAdapter
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
//        isCancelable = true
    binding.mapView.onCreate(savedInstanceState)

    viewModel.appManager.analyticsManagers.addressDetailScreenOpen()

    binding.mapView.onResume() // needed to get the map to display immediately
    try {
      MapsInitializer.initialize(requireActivity().applicationContext)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    binding.mapView.getMapAsync(this)
    initLayout()
    observers()
  }

  private fun initLayout() {
    seClicks()
    binding.address = viewModel.currentAddress
//        binding.lyAddressDetails.lyAddressType.click =this

    binding.tollBar.tvToolbarTitle.text = getString(R.string.add_a_new_address)

    binding.time = viewModel.timeTypeModelOB
    binding.addressType = viewModel.addressTypeModelOB

    binding.numberValid = viewModel.numberValid

    binding.phone = viewModel.phone
    viewModel.phone.setEditText(binding.edPhone)
    binding.name = viewModel.name
    viewModel.name.setEditText(binding.edFirstname)

    binding.state = viewModel.state
    viewModel.state.setEditText(binding.edState)


    binding.city = viewModel.city
    viewModel.city.setEditText(binding.edCity)


    binding.street = viewModel.streetAddress
    viewModel.streetAddress.setEditText(binding.edArea)
    binding.flat = viewModel.flatNumber
    viewModel.flatNumber.setEditText(binding.edFlat)

    binding.building = viewModel.building
    viewModel.building.setEditText(binding.edBuilding)
    setCurrentAddressValues()
    setCountryToEditText()

  }

  private fun observers() {

    binding.edPhone.addTextChangedListener(EditTextWatcher(object : EditTextCallBack {
      override fun onTextChange(text: String?) {
        viewModel.phone.setValue(text.toString())
        viewModel.numberValid.set(phoneNumberKit.isValid)
      }
    }))

    viewModel.addressType.observe(viewLifecycleOwner, Observer {
      it?.let {
        viewModel.addressTypeModelOB.set(it)
//                binding.lyAddressDetails.lyAddressType.addressType = it.name

        if (it.addressId == 1) {
          binding.lyType.isClickable = true
          binding.edType.isClickable = true
          binding.imgLocationType.setImageDrawable(
            ContextCompat.getDrawable(
              requireContext(),
              R.drawable.ic_home_location
            )
          )
        }
        if (it.addressId == 2) {
          binding.lyType.isClickable = true
          binding.edType.isClickable = true
          binding.imgLocationType.setImageDrawable(
            ContextCompat.getDrawable(
              requireContext(),
              R.drawable.ic_work_location
            )
          )
        }
        if (it.addressId == 3) {
          binding.lyType.isClickable = false
          binding.edType.isClickable = false
          binding.imgLocationType.setImageDrawable(
            ContextCompat.getDrawable(
              requireContext(),
              R.drawable.ic_fav_location
            )
          )
        }
      }
    })
    viewModel.timeType.observe(viewLifecycleOwner, Observer {
      it?.let {
        viewModel.timeTypeModelOB.set(it)

      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_address
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onConfirm() {
    if (viewModel.numberValid.get() == true
      && viewModel.currentAddress.get()?.latitude != null
      && viewModel.currentAddress.get()?.longitude != null
    ) {
      viewModel.additionalInfo.set(binding.edAdditional.text.toString())
      val gson = Gson()
      val jsonStr = gson.toJson(viewModel.makeAddressObject())
      Logger.d("SaveAddressModel", jsonStr)
      saveAddress()
    }
  }

  override fun onClickSelectType() {
    findNavController().navigate(R.id.addressTypeBottomSheet)
//        findNavController().navigate(R.id.addressTypeBottomSheet)
  }

  override fun onClickSelectTime() {
    findNavController().navigate(R.id.timeTypeBottomSheet)
  }

  override fun onBaseActivityCreated(savedInstanceState: Bundle?) {

  }

  override fun onMapMove(address: Address?) {

  }

  override fun onMapTouch() {

  }

  override fun onMapReady(map: GoogleMap?) {
    map?.let {
      baseMap = it
      initBaseMap(map)
      moveCameraToLocation(viewModel.latLng, false)
    }
  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

  private fun saveAddress() {
    if (viewModel.currentAddress.get()?.latitude != null && viewModel.currentAddress.get()?.latitude != 0.0
      && viewModel.currentAddress.get()?.longitude != null && viewModel.currentAddress.get()?.longitude != 0.0
    ) {
      viewModel.saveAddress(viewModel.makeAddressObject())
        .observe(viewLifecycleOwner, Observer {
          when (it.status) {
            Result.Status.SUCCESS -> {
              hideLoading()
              viewModel.addressSelection = AddressSelection.LATEST_ADDRESS
              findNavController().navigateUp()
              findNavController().navigateUp()
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

  }

  private fun seClicks() {
    binding.click = this
    binding.tollBar.click = this
  }

  private fun setUserValues() {
    val timeTypeModel =
      AddressTypeModel(0, "Morning")
    val addressTypeModel =
      AddressTypeModel(1, "Work")
    viewModel.timeTypeModelOB.set(timeTypeModel)
    if (viewModel.appManager.persistenceManager.getLoggedInUser()?.phone?.isNotBlank() == true) {
      viewModel.phone.setValue(viewModel.appManager.persistenceManager.getLoggedInUser()?.phone)
      viewModel.phone.setError(false)
    }
    viewModel.addressTypeModelOB.set(addressTypeModel)
    binding.edPhone.setText(viewModel.phone.getValue())
    viewModel.name.setValue(viewModel.appManager.persistenceManager.getLoggedInUser()?.name)
    viewModel.numberValid.set(phoneNumberKit.isValid)
  }

  private fun setCurrentAddressValues() {
    viewModel.state.setValue(viewModel.currentAddress.get()?.adminArea)
    viewModel.city.setValue(viewModel.currentAddress.get()?.locality)
    viewModel.streetAddress.setValue("${viewModel.currentAddress.get()?.thoroughfare ?: ""} ,${viewModel.currentAddress.get()?.subLocality ?: ""}")

  }

  private fun setCountryToEditText() {
    phoneNumberKit = PhoneNumberKit(requireContext()) // Requires context
    phoneNumberKit.attachToInput(binding.lyPhone, "AE")
    phoneNumberKit.setupCountryPicker(activity as AppCompatActivity)
    if (viewModel.isSetUserLocal) {
      setUserValues()
    } else {
      viewModel.phone.setError(false)
      binding.edPhone.setText(viewModel.phone.getValue())
      viewModel.numberValid.set(phoneNumberKit.isValid)
    }
  }
}