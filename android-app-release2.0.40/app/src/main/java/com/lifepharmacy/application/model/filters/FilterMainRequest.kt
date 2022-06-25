package com.lifepharmacy.application.model.filters


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@SuppressLint("ParcelCreator")
@Parcelize
data class FilterMainRequest(
  var filters: ArrayList<FilterRequestModel>? = ArrayList(),
  @SerializedName("search")
  var search: String? = "",
  @SerializedName("instant_only")
  var instantOnly: Boolean? = false
) : Parcelable