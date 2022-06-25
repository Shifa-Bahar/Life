package com.lifepharmacy.application.model.notifications


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StickyMessageData(
  @SerializedName("action_key")
  var actionKey: String? = "",
  @SerializedName("action_label")
  var actionLabel: String? = "",
  @SerializedName("action_value")
  var actionValue: String? = "",
  @SerializedName("icon")
  var icon: String? = "",
  @SerializedName("title")
  var title: String? = "",
  @SerializedName("message")
  var message: String? = ""
) : Parcelable