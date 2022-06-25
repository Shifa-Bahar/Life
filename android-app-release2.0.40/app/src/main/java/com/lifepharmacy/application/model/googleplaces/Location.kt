package com.lifepharmacy.application.model.googleplaces


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class Location(
  @SerializedName("lat")
  var lat: Double? = 0.0,
  @SerializedName("lng")
  var lng: Double? = 0.0
) : Parcelable