package com.lifepharmacy.application.model.cart


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class ValidateCouponRequestBody(
  @SerializedName("cart_id")
  var cartId: Int? = 0,
  @SerializedName("cart_total")
  var cartTotal: Double? = 0.0,
  @SerializedName("coupon_code")
  var couponCode: String? = ""
) : Parcelable