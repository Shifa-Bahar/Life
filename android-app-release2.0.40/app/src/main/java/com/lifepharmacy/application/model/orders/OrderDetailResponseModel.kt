package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat

@SuppressLint("ParcelCreator")
@Parcelize
data class OrderDetailResponseModel(
  @SerializedName("address_id")
  var addressId: Int = 0,
  @SerializedName("created_at")
  var createdAt: String = "",
  var discount: Double? = 0.0,
  var id: Int = 0,
  @SerializedName("items")
  var items: ArrayList<OrderItem> = ArrayList(),
  @SerializedName("order_id")
  var orderId: String = "",
  var status: Int = 0,
  var tax: Double = 0.0,
  var total: Double = 0.0,
  var type: String = "",
  @SerializedName("updated_at")
  var updatedAt: String = "",
  @SerializedName("user_id")
  var userId: Int = 0,
  @SerializedName("address")
  var addresses: AddressModel? = AddressModel(),
  @SerializedName("address_details")
  var addressesDetails: AddressModel? = AddressModel(),
  @SerializedName("sub_total")
  var subTotal: Double = 0.0,
  @SerializedName("delivery_fees")
  var deliveryFee: Double = 0.0,
  @SerializedName("is_instant_requested")
  var isInstantRequested: Int = 0,
  @SerializedName("cod_charge")
  var codCharges: Double? = 0.0,
  @SerializedName("status_label")
  var statusLabel: String = "",
  @SerializedName("sub_orders")
  var subOrders: ArrayList<SubOrderItem> = ArrayList(),
  @SerializedName("transactions")
  var transactions: ArrayList<TransactionModel>? = ArrayList(),
  @SerializedName("is_order_returnable")
  var isOrderReturnable: Boolean = false,
) : Parcelable {
  fun getCodeInString(): String? {
    return codCharges?.currencyFormat()
  }

  fun getDiscountInString(): String? {
    return discount?.currencyFormat()
  }

  fun getAddressDetails(): AddressModel? {
    return if (addressesDetails != null) {
      addressesDetails
    } else {
      addresses
    }
  }
}