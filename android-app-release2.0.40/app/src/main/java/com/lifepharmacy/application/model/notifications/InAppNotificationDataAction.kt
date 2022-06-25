package com.lifepharmacy.application.model.notifications


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InAppNotificationDataAction(
  @SerializedName("heading")
  var heading: String? = "",
  @SerializedName("key")
  var key: String? = "",
  @SerializedName("value")
  var value: String? = ""
) : Parcelable