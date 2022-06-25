package com.lifepharmacy.application.model.cart


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.utils.universal.Extensions.doubleDigitDouble

@SuppressLint("ParcelCreator")
@Parcelize
data class AlertMessages(
  @SerializedName("icon")
  var icon: String? = "",
  @SerializedName("background")
  var background: String? = "",
  @SerializedName("title")
  var title: String? = "",
  @SerializedName("sub_title")
  var subtitle: String? = ""
) : Parcelable {

}