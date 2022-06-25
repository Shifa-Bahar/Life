package com.lifepharmacy.application.managers

import android.location.Address
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.lifepharmacy.application.model.address.AddressMainModel
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import com.lifepharmacy.application.utils.universal.GoogleUtils
import com.lifepharmacy.application.utils.universal.Logger
import javax.inject.Inject
import kotlin.collections.ArrayList

class AddressManager
@Inject constructor(var persistenceManager: PersistenceManager) {

  fun getShortestAddress(latLng: LatLng): AddressModel? {
    Logger.d("AddressManager", "Anything")
    val savedAddressList: AddressMainModel? = persistenceManager.getAddressList()
    val list: ArrayList<Double> = ArrayList()
    savedAddressList?.let {
      if (!it.addresses.isNullOrEmpty()) {
        for (item in it.addresses ?: return@let) {
          list.add(
            GoogleUtils.distanceBetweenTwoLatLongsValues(
              item.latitude?.stringToNullSafeDouble() ?: 0.0,
              item.longitude?.stringToNullSafeDouble() ?: 0.0,
              latLng.latitude,
              latLng.longitude
            )
          )
        }
      }
    }
    return if (list.isNotEmpty()) {
      if (savedAddressList != null) {
        savedAddressList.addresses?.get(minIndex(list))
      } else {
        null
      }
    } else {
      null
    }

  }

  private fun minIndex(list: ArrayList<Double>): Int {
    Logger.d("AddressManager", list.indexOf(list.minOrNull()).toString())
    return list.indexOf(list.minOrNull())
  }

  fun getAddressModelFromPlace(place: Place): AddressModel {
    return AddressModel(streetAddress = place.name)
  }

  fun getAddressModelFromAddress(address: Address): AddressModel {
    return AddressModel(
      googleAddress = address.getAddressLine(0),
      state = address.adminArea,
      city = address.subLocality,
      streetAddress = address.thoroughfare ?: "" + address.subLocality,
    )
  }
}