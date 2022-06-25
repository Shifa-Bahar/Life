package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.payment.OrderPayment
import com.lifepharmacy.application.model.payment.TransactionModel

@SuppressLint("ParcelCreator")
@Parcelize
data class OrderResponseModel(
  @SerializedName("address_id")
  var addressId: Int? = 0,
  @SerializedName("created_at")
  var createdAt: String? = "",
  var discount: Double? = 0.0,
  var id: Int? = 0,
  var items: ArrayList<OrderItem>? = ArrayList(),
  @SerializedName("order_id")
  var orderId: String? = "",
  var status: Int? = 0,
  var tax: Double? = 0.0,
  var total: Double? = 0.0,
  var deliveryFee: Double? = 0.0,
  var type: String? = "",
  @SerializedName("updated_at")
  var updatedAt: String? = "",
  @SerializedName("user_id")
  var userId: Int? = 0,
  var addresses: AddressModel? = AddressModel(),
  @SerializedName("status_label")
  var statusLabel: String? = "",
  @SerializedName("sub_orders")
  var subOrders: ArrayList<SubOrderItem>? = ArrayList(),
  @SerializedName("transactions")
  var transactions: ArrayList<TransactionModel>? = ArrayList(),
  @SerializedName("payment")
  var payment: OrderPayment? = OrderPayment(),
) : Parcelable