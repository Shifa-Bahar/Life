package com.lifepharmacy.application.model.rewards


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class ValidateRewardsRequestBody(
  @SerializedName("cart_id")
  var cartId: Int? = 0,
  @SerializedName("cart_total")
  var cartTotal: Double? = 0.0,
  @SerializedName("request_code")
  var rewardCode: String? = ""
) : Parcelable