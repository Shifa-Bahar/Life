package com.lifepharmacy.application.model.review


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Driver(
  @SerializedName("feedback")
  var feedback: String? = "",
  @SerializedName("rating")
  var rating: Float? = 0F,
  @SerializedName("shipment_id")
  var shipmentId: Int? = 0
) : Parcelable