package com.lifepharmacy.application.model.googleplaces


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class GooglePlacesResponse(
  @SerializedName("error_message")
  var errorMessage: String? = "",
  @SerializedName("results")
  var results: ArrayList<Result>? = ArrayList(),
  @SerializedName("status")
  var status: String? = ""
) : Parcelable