package com.lifepharmacy.application.model.cart


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AlertMessage(
  @SerializedName("icon")
  var icon: String? = "",
  @SerializedName("background")
  var background: String? = "",
  @SerializedName("title")
  var title: String? = "",
  @SerializedName("sub_title")
  var sub_title: String? = ""
) : Parcelable