package com.lifepharmacy.application.model.payment


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Urls(
  @SerializedName("payment_fail_url")
  var failUrl: String? = "",
  @SerializedName("payment_url")
  var paymentUrl: String? = "",
  @SerializedName("payment_success_url")
  var successUrl: String? = ""
) : Parcelable