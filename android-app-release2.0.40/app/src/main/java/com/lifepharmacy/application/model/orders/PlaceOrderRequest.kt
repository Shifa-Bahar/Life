package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.orders.OrderItem

@SuppressLint("ParcelCreator")
@Parcelize
data class PlaceOrderRequest(
  @SerializedName("address_id")
  var addressId: Int = 0,
  var discount: Double = 0.0,
  var items: ArrayList<OrderItem> = ArrayList(),
  var total: Double = 0.0,
  @SerializedName("is_instant_requested")
  var isInstantRequested: Boolean = false,
  @SerializedName("instant_not_requested")
  var instantNotRequested: Boolean = false,
  @SerializedName("delivery_instructions")
  var deliveryinstructions: ArrayList<String>? = ArrayList(),
  @SerializedName("is_leave_at_door_requested")
  var isLeaveAtMyDoor: Boolean = false,
  @SerializedName("delivery_fees")
  var deliveryFees: Double = 0.0,
  @SerializedName("cod_charge")
  var codCharge: Double = 0.0,
  @SerializedName("sub_total")
  var subTotal: Double = 0.0,
  @SerializedName("coupon")
  var coupon: String? = "",
  @SerializedName("channel")
  var channel: String? = "android",
  @SerializedName("cart_id")
  var cartId: String? = "",
  @SerializedName("net_vat")
  var vat: Double? = 0.0,
  @SerializedName("notes")
  var notes: String? = "",
  @SerializedName("instant_slot_id")
  var instantSlotId: Int? = 0,
  @SerializedName("standard_slot_id")
  var standardSlotID: Int? = 0,
) : Parcelable