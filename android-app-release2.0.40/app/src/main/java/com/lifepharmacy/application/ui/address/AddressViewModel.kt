package com.lifepharmacy.application.ui.address

import android.app.Application
import android.location.Address
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.AddressChanged
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.managers.AddressManager
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.address.AddressTypeModel
import com.lifepharmacy.application.model.address.AddressDeleteRequest
import com.lifepharmacy.application.model.address.AddressMainModel
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.googleplaces.NearByPlacesRequestModel
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.ProfileRepository
import retrofit2.Response
import kotlin.collections.ArrayList
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.utils.*
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import com.lifepharmacy.application.utils.universal.GoogleUtils
import com.lifepharmacy.application.utils.universal.InputEditTextValidator
import com.lifepharmacy.application.utils.universal.Utils

/**
 * Created by Zahid Ali
 */
class AddressViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val profileRepository: ProfileRepository,
  val addressManager: AddressManager,
  application: Application
) : BaseViewModel(application) {

  var addressMainMut = MutableLiveData<Result<AddressMainModel>>()
  var addressType = MutableLiveData<AddressTypeModel>()
  var timeType = MutableLiveData<AddressTypeModel>()
  lateinit var phone: InputEditTextValidator
  var numberValid = ObservableField<Boolean>()
  var state: InputEditTextValidator
  var name: InputEditTextValidator
  var city: InputEditTextValidator
  var streetAddress: InputEditTextValidator
  var flatNumber: InputEditTextValidator
  var additionalInfo = ObservableField<String>()
  var building: InputEditTextValidator
  var currentAddress = ObservableField<Address>()
  var isSelecting = ObservableField<Boolean>()
  var isEdit: Boolean = false
  var isSetUserLocal: Boolean = true
  var isNearByClicked: Boolean = false

  var latLng: LatLng? = null
  var addressTypeModelOB = ObservableField<AddressTypeModel>()
  var timeTypeModelOB = ObservableField<AddressTypeModel>()
  var editAddress = AddressModel()
  var deliveredAddressMut = MutableLiveData<AddressModel?>()
  var isThereAnyAddress = MutableLiveData<Boolean>()
  var isThereLatLong = MutableLiveData<Boolean>()
  var isMapMoving = MutableLiveData<Boolean>()
  var addressConfirmed = MutableLiveData<Boolean>()
  var addressChanged = MutableLiveData<AddressChanged>()
  var addressSelection = AddressSelection.NEAREST_ADDRESS
  var userID: Int = 0

  var nearByPlaces =
    MutableLiveData<ArrayList<com.lifepharmacy.application.model.googleplaces.Result>>()

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
    state = InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      errorText
    )
    city = InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      errorText
    )
    streetAddress =
      InputEditTextValidator(
        InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
        true,
        null,
        errorText
      )
    flatNumber =
      InputEditTextValidator(
        InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
        true,
        null,
        errorText
      )
    building =
      InputEditTextValidator(
        InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
        true,
        null,
        errorText
      )
  }


  fun requestAddress() {
    if (appManager.persistenceManager.isLoggedIn()) {
      profileRepository.getAddress(object :
        HandleNetworkCallBack<GeneralResponseModel<AddressMainModel>> {
        override fun handleWebserviceCallBackSuccess(response: Response<GeneralResponseModel<AddressMainModel>>) {

          response.body()?.data?.let { data ->
            appManager.persistenceManager.saveAddressList(data)
          }
          if (addressSelection == AddressSelection.NEAREST_ADDRESS) {
            initNearestAddress()
          }
          if (addressSelection == AddressSelection.LATEST_ADDRESS) {
            response.body()?.data?.addresses?.last()?.let { setDeliveredAddress(it) }
            addressSelection = AddressSelection.NON
          }
          addressMainMut.value =
            Result(Result.Status.SUCCESS, response.body()?.data, response.body()?.message)

        }

        override fun handleWebserviceCallBackFailure(error: String?) {
          addressMainMut.value = Result(Result.Status.ERROR, null, error)
        }

      })
    }
  }

  private fun initNearestAddress() {
    appManager.persistenceManager.getLatLong()?.let {
      GoogleUtils.getLatLongFromString(it)?.let { latLong ->
        addressManager.getShortestAddress(latLong)?.let { address ->
          val gson = Gson()
          val jsonStr = gson.toJson(address)
//          Logger.d("ShortestAddress", jsonStr)
//          viewModel.selectedAddress.set(address)
          setDeliveredAddress(address)
          addressSelection = AddressSelection.NON
        }
      }
    }
  }

  fun getAddressTypes(): ArrayList<AddressTypeModel> {
    return profileRepository.getSpinnerItems()
  }

  fun getTimeTypes(): ArrayList<AddressTypeModel> {
    return profileRepository.getTimeItems()
  }

  fun setAddressType(addressTypeModel: AddressTypeModel) {
    addressType.value = addressTypeModel
  }

  fun setTimeType(addressTypeModel: AddressTypeModel) {
    timeType.value = addressTypeModel
  }

  fun makeAddressObject(): AddressModel {
    return AddressModel(
      id = editAddress.id,
      name = name.getValue(),
      phone = Utils.formatNumberToSimple(phone.getValue()),
      latitude = currentAddress.get()?.latitude.toString(),
      longitude = currentAddress.get()?.longitude.toString(),
      type = addressTypeModelOB.get()?.name ?: "Work",
      country = currentAddress.get()?.countryName ?: "",
      state = state.getValue(),
      city = city.getValue(),
      area = currentAddress.get()?.subLocality,
      streetAddress = streetAddress.getValue(),
      building = building.getValue(),
      flatNumber = flatNumber.getValue(),
      additionalInfo = additionalInfo.get() ?: "",
      suitableTiming = timeTypeModelOB.get()?.name ?: "0",
      googleAddress = currentAddress.get()?.getAddressLine(0) ?: "",
      addressId = editAddress.addressId
    )
  }


  fun saveAddress(addressModel: AddressModel) =
    performNwOperation { profileRepository.saveAddress(addressModel) }


  fun getNewByPlaces() =
    performNwOperation { profileRepository.getNearByPlaces(makeNearByPlacesRequestModel()) }


  fun makeDeleteAddressObject(int: Int): AddressDeleteRequest {
    return AddressDeleteRequest(int)
  }

  fun setAddress(addressModel: AddressModel) {
    isEdit = true
    currentAddress.get()?.latitude = addressModel.latitude?.stringToNullSafeDouble() ?: 0.0
    currentAddress.get()?.longitude = addressModel.longitude?.stringToNullSafeDouble() ?: 0.0
    currentAddress.get()?.countryName = addressModel.country
    currentAddress.get()?.subLocality = addressModel.area
    currentAddress.get()?.setAddressLine(0, addressModel.googleAddress)
    name.setValue(addressModel.name)
    phone.setValue(addressModel.phone)
    state.setValue(addressModel.state)
    city.setValue(addressModel.city)
    streetAddress.setValue(addressModel.streetAddress)
    building.setValue(addressModel.building)
    flatNumber.setValue(addressModel.flatNumber)
    additionalInfo.set(addressModel.additionalInfo)
//        var timeTypeModel = AddressTypeModel()
//        if (addressModel.suitableTiming.toString() == "0") {
//            timeTypeModel.name = "Morning"
//            timeTypeModel.addressId = addressModel.suitableTiming?.toInt() ?: 0
//        }
//        if (addressModel.suitableTiming.toString() == "1") {
//            timeTypeModel.name = "Afternoon"
//            timeTypeModel.addressId = addressModel.suitableTiming?.toInt() ?: 0
//        }
//        if (addressModel.suitableTiming.toString() == "2") {
//            timeTypeModel.name = "Evening"
//            timeTypeModel.addressId = addressModel.suitableTiming?.toInt() ?: 0
//        }
//        timeTypeModelOB.set(addressModel.suitableTiming?.toInt()?.let { MappingUtil.getTimeType(it) })

//        var addressTypeModel = AddressTypeModel()
//        if (addressModel.type.toString() == "Work") {
//            addressTypeModel.addressId = 1
//        }
//        if (addressModel.type.toString() == "Home") {
//            addressTypeModel.addressId = 2
//        }
//        if (addressModel.type.toString() == "Others") {
//            addressTypeModel.addressId = 3
//        }
//        addressTypeModel.name = addressModel.type.toString()
    addressTypeModelOB.set(MappingUtil.getAddressType(addressModel.type.toString()))
    editAddress = addressModel
    editAddress.addressId = addressModel.id
  }

  fun makeNearByPlacesRequestModel(): NearByPlacesRequestModel {
    return NearByPlacesRequestModel(
      key = viewModelContext.getString(R.string.google_Api_Key),
      location = "${currentAddress.get()?.latitude},${currentAddress.get()?.longitude}",
      radius = "100"
    )
  }

  fun clearFields() {
    state.setValue("")
    city.setValue("")
    streetAddress.setValue("")
    building.setValue("")
    flatNumber.setValue("")
    additionalInfo.set(null)
    addressTypeModelOB.set(null)
  }

  fun setDeliveredAddress(addressModel: AddressModel) {
    appManager.storageManagers.saveLatLng(
      LatLng(
        addressModel.latitude?.stringToNullSafeDouble() ?: 0.0,
        addressModel.longitude?.stringToNullSafeDouble() ?: 0.0
      )
    )
    deliveredAddressMut.value = addressModel
  }

  fun deleteAddress(deleteRequest: AddressDeleteRequest) =
    performNwOperation { profileRepository.deleteAddress(deleteRequest) }

  fun setNearByPlaceArray(places: ArrayList<com.lifepharmacy.application.model.googleplaces.Result>) {
    if (!places.isNullOrEmpty()) {
      places.removeFirst()
      if (places.size > 5) {
        val temArray = ArrayList<com.lifepharmacy.application.model.googleplaces.Result>()
        temArray.addAll(places.subList(0, 5))
        if (!temArray.isNullOrEmpty()) {
          nearByPlaces.value = temArray
        }
      } else {
        nearByPlaces.value = places
      }
    }
  }
}