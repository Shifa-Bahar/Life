package com.lifepharmacy.application.model.product


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReviewUserDetails(
  @SerializedName("name")
  var name: String? = "",
  @SerializedName("photo")
  var photo:  String? = "",
) : Parcelable