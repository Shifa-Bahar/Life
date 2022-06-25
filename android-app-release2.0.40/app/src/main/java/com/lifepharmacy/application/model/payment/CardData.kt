package com.lifepharmacy.application.model.payment


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class CardData(
  @SerializedName("payment_info")
  var paymentInfo: PaymentInfo? = PaymentInfo()
) : Parcelable