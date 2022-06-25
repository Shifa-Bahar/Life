package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class RateProductRequestBody(
  @SerializedName("rating")
  var rating: Float? = 0F,
  @SerializedName("review")
  var review: String? = "",
  @SerializedName("sub_order_id")
  var subOrderId: Int? = 0,
//  @SerializedName("order_id")
//  var orderID: Int? = 0,
  @SerializedName("response")
  var response: String? = "",
  @SerializedName("is_anonymous")
  var isAnonymous: Boolean? = false,
) : Parcelable