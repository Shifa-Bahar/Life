package com.lifepharmacy.application.model.notifications


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InAppNotificationDataContent(
  @SerializedName("button_background")
  var buttonBackground: String? = "",
  @SerializedName("full_width")
  var fullWidth: Boolean? = false,
  @SerializedName("image_url")
  var imageUrl: String? = "",
  @SerializedName("text")
  var text: String? = "",
  @SerializedName("title")
  var title: String? = "",
  @SerializedName("text_color")
  var textColor: String? = "",
  @SerializedName("height")
  var imageHeight: Int? = 1,
  @SerializedName("width")
  var imageWidth: Int? = 1,
) : Parcelable