package com.lifepharmacy.application.model.notifications


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InAppNotificationDataModel(
  @SerializedName("alignment")
  var alignment: String? = "",
//  @SerializedName("button")
//  var button: Button? = Button(),
//  @SerializedName("fullWithImage")
//  var fullWithImage: Boolean? = false,
//  @SerializedName("imageHeight")
//  var imageHeight: String? = "",
//  @SerializedName("imageWidth")
//  var imageWidth: String? = "",
  @SerializedName("type")
  var type: String? = "",
//  @SerializedName("value")
//  var value: String? = "",
  @SerializedName("content")
  var content: InAppNotificationDataContent? = InAppNotificationDataContent(),
  @SerializedName("action")
  var action: InAppNotificationDataAction? = InAppNotificationDataAction(),
) : Parcelable