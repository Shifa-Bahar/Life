package com.lifepharmacy.application.model.googleplaces


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NearByPlacesRequestModel(
  @SerializedName("key")
  var key: String? = "",
  @SerializedName("location")
  var location: String? = "",
  @SerializedName("radius")
  var radius: String? = ""
) : Parcelable