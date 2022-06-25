package com.lifepharmacy.application.model.notifications


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChangeStatusRequestBody(
  @SerializedName("action")
  var action: String? = "",
  @SerializedName("message_id")
  var messageId: String? = ""
) : Parcelable