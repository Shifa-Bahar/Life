package com.lifepharmacy.application.model.review


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllRatingRequestBody(
  @SerializedName("driver")
  var driver: Driver? = Driver(),
  @SerializedName("is_anonymous")
  var isAnonymous: Boolean? = false,
  @SerializedName("over_all")
  var overAll: OverAll? = OverAll(),
  @SerializedName("products")
  var products: List<Product>? = listOf(),
  @SerializedName("sub_order_id")
  var subOrderId: Int? = 0
) : Parcelable