package com.lifepharmacy.application.model.product


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GalleryImages(
  @SerializedName("full")
  var full: String? = "",
  @SerializedName("image")
  var image: String? = "",
  @SerializedName("medium")
  var medium: String? = "",
  @SerializedName("thumbnail")
  var thumbnail: String? = ""
) : Parcelable