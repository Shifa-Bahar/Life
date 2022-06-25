package com.lifepharmacy.application.model.googleplaces


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class Result(
  @SerializedName("geometry")
  var geometry: Geometry? = Geometry(),
  @SerializedName("name")
  var name: String? = "",
  @SerializedName("place_id")
  var placeId: String? = "",
  @SerializedName("vicinity")
  var vicinity: String? = ""
) : Parcelable