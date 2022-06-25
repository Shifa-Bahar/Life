package com.lifepharmacy.application.model.payment


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class OrderPayment(
  @SerializedName("amount")
  var amount: String? = "",
  @SerializedName("card_info")
  var cardInfo: CardInfo? = CardInfo(),
  @SerializedName("id")
  var id: Int? = 0,
  @SerializedName("is_refunded")
  var isRefunded: Int? = 0,
  @SerializedName("method")
  var method: String? = "",
  @SerializedName("order_id")
  var orderId: Int? = 0,
//  @SerializedName("provider_id")
//  var providerId: Any? = Any(),
  @SerializedName("purpose")
  var purpose: String? = "",
  @SerializedName("status")
  var status: Int? = 0,
  @SerializedName("status_label")
  var statusLabel: String? = "",
  @SerializedName("type")
  var type: String? = "",
  @SerializedName("user_id")
  var userId: Int? = 0
) : Parcelable {
  fun getPaymentMethodTitle(): String {
    return method?.capitalize(Locale.ROOT) ?: "Cash"
  }
}