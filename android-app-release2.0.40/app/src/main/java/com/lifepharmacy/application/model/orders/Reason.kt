package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Reason(
  @SerializedName("images")
  var images: List<String>? = listOf(),
  @SerializedName("reason")
  var reason: String? = ""
) : Parcelable