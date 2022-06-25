package com.lifepharmacy.application.model.payment


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class PaymentInfo(
  @SerializedName("card_scheme")
  var cardScheme: String? = "",
  @SerializedName("card_type")
  var cardType: String? = "",
  @SerializedName("expiryMonth")
  var expiryMonth: Int? = 0,
  @SerializedName("expiryYear")
  var expiryYear: Int? = 0,
  @SerializedName("payment_description")
  var paymentDescription: String? = ""
) : Parcelable