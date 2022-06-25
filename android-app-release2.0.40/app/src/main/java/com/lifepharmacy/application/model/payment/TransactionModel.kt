package com.lifepharmacy.application.model.payment


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.payment.Urls
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
data class TransactionModel(
//    var details: List<Any> = listOf(),
  var method: String = "",
  @SerializedName("order_id")
  var orderId: Int = 0,
  var status: Int = 0,
  var id: Int = 0,
  var updated_at: String = "",
  var created_at: String = "",
  @SerializedName("payment_status")
  var paymentStatus: String? = "",
  @SerializedName("transaction_id")
  var transactionId: String? = "",
  @SerializedName("type")
  var type: String? = "charge",
  @SerializedName("card_type")
  var CardType: String? = "",
  @SerializedName("token")
  var token: String? = "",
  @SerializedName("save_card")
  var save_card: Boolean = true,
  @SerializedName("redirect")
  var redirect: Boolean = false,
  @SerializedName("redirect_url")
  var redirect_url: String = "",
  @SerializedName("card_id")
  var card_id: Int? = null,
  @SerializedName("pg")
  var pg: Int? = null,
  @SerializedName("payment_fail_url")
  var failUrl: String? = "",
  @SerializedName("payment_url")
  var paymentUrl: String? = "",
  @SerializedName("payment_success_url")
  var successUrl: String? = "",
//  @SerializedName("details")
//  var urls: Urls? = Urls(),
  @SerializedName("amount")
  var amount: Double? = 0.0,
  @SerializedName("purpose")
  var purpose: String? = "",

  @SerializedName("order")
  var order: OrderResponseModel? = OrderResponseModel(),
  @SerializedName("card_info")
  var cardInfo: CardInfo? = CardInfo(),
) : Parcelable {
  fun getPaymentMethodTitle(): String {
    return method.capitalize(Locale.ROOT)
  }
}