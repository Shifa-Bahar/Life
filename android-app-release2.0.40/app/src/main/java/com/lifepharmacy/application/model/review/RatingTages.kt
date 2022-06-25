package com.lifepharmacy.application.model.review


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RatingTages(
  @SerializedName("remarks")
  var remarks: String? = "",
  @SerializedName("tags")
  var tags: List<String>? = listOf(),
  @SerializedName("value")
  var value: Int? = 0
) : Parcelable