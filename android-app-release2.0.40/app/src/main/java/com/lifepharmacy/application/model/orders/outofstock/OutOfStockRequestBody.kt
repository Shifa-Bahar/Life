package com.lifepharmacy.application.model.orders.outofstock


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OutOfStockRequestBody(
  @SerializedName("address_id")
  var addressId: Int? = 0,
  @SerializedName("channel")
  var channel: String? = "",
  @SerializedName("discount")
  var discount: Int? = 0,
  @SerializedName("items")
  var items: ArrayList<Item>? = ArrayList(),
  @SerializedName("net_vat")
  var netVat: Double? = 0.0,
  @SerializedName("sub_total")
  var subTotal: Double? = 0.0,
  @SerializedName("total")
  var total: Double? = 0.0
) : Parcelable