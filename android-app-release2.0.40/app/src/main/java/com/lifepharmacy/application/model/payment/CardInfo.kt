package com.lifepharmacy.application.model.payment


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardInfo(
  @SerializedName("card_scheme")
  var cardScheme: String? = "",
  @SerializedName("card_type")
  var cardType: String? = "",
  @SerializedName("payment_description")
  var paymentDescription: String? = ""
) : Parcelable {
  fun getLastForDigitsOfCard(): String {
    paymentDescription?.let {
      return if (it.length > 4) {
        it.substring(it.length - 4);
      } else {
        paymentDescription ?: ""
      }
    }
    return paymentDescription ?: ""

  }
}