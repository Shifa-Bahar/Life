package com.lifepharmacy.application.model.cart


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.utils.universal.Extensions.doubleDigitDouble

@SuppressLint("ParcelCreator")
@Parcelize
data class CouponModel(
    @SerializedName("coupon_code")
    var couponCode: String? = "",
    @SerializedName("coupon_expiry")
    var couponExpiry: String? = "",
    @SerializedName("coupon_value")
    var couponValue: Double? = 0.0
) : Parcelable{
  fun getCouponValueFormat():Double?{
    return couponValue?.doubleDigitDouble()
  }
}