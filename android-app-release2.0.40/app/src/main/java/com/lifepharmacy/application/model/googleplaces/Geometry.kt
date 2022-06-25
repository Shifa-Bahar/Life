package com.lifepharmacy.application.model.googleplaces


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Geometry(
  @SerializedName("location")
  var location: Location? = Location()
) : Parcelable {
  fun getLatLng(): LatLng {
    return LatLng(
      location?.lat ?: 0.0, location?.lng ?: 0.0
    )
  }
}