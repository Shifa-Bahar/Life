package com.lifepharmacy.application.model.orders.suborder


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.orders.SubOrderShipment
import com.lifepharmacy.application.model.orders.shipment.TimeLine
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat

@SuppressLint("ParcelCreator")
@Parcelize
data class SubOrderItemWithJustItemOfRating(
  @SerializedName("address_id")
  var addressId: Int? = 0,
  @SerializedName("cod_charge")
  var codCharge: Double? = 0.0,
  @SerializedName("created_at")
  var createdAt: String? = "",
  @SerializedName("delivery_fees")
  var deliveryFee: Double? = 0.0,
  @SerializedName("discount")
  var discount: Double? = 0.0,
  @SerializedName("fulfilment_type")
  var fulfilmentType: String? = "",
  @SerializedName("id")
  var id: Int? = 0,
  @SerializedName("parent_order_id")
  var parentOrderId: Int? = 0,
  @SerializedName("shipment")
  var shipment: SubOrderShipment? = SubOrderShipment(),
  @SerializedName("items")
  var items: ArrayList<SubOrderProductItemForRating>? = ArrayList(),
  @SerializedName("status")
  var status: Int? = 0,
  @SerializedName("status_label")
  var statusLabel: String? = "",
  @SerializedName("store_code")
  var storeCode: String? = "",
  @SerializedName("sub_order_id")
  var subOrderId: String? = "",
  @SerializedName("tax")
  var tax: Double? = 0.0,
  @SerializedName("total")
  var total: Double? = 0.0,
  @SerializedName("updated_at")
  var updatedAt: String? = "",
  @SerializedName("user_id")
  var userId: Int? = 0,
  @SerializedName("expected_delivery_time")
  var expectedDate: String? = "",
  @SerializedName("time_lines")
  var timeLines: ArrayList<TimeLine>? = ArrayList(),
  @SerializedName("payment")
  var transactions: TransactionModel? = TransactionModel(),
  @SerializedName("is_order_returnable")
  var isOrderReturnable: Boolean = false,
  @SerializedName("address_details")
  var addressesDetails: AddressModel? = AddressModel(),
  @SerializedName("address")
  var addresses: AddressModel? = AddressModel(),
  @SerializedName("order_id")
  var orderId: String = "",
  @SerializedName("sub_total")
  var subTotal: Double = 0.0,
//  @SerializedName("sub_orders")
//  var subOrders: ArrayList<SubOrderItem>? = ArrayList(),
  @SerializedName("rating")
  var rating: Float? = 0F,
) : Parcelable {
//  fun getCodeInString(): String? {
//    return codCharge?.currencyFormat()
//  }
//
//  fun getDiscountInString(): String? {
//    return discount?.currencyFormat()
//  }
//
//  fun getAddressDetails(): AddressModel? {
//    return if (addressesDetails != null) {
//      addressesDetails
//    } else {
//      addresses
//    }
//  }
}
