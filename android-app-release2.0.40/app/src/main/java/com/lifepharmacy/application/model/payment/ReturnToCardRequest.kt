package com.lifepharmacy.application.model.payment


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReturnToCardRequest(
  @SerializedName("return_order_id")
  var returnOrderId: Double? = 0.0
) : Parcelable