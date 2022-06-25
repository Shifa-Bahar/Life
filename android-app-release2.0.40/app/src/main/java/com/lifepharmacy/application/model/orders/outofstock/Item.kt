package com.lifepharmacy.application.model.orders.outofstock


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
  @SerializedName("gross_line_total")
  var grossLineTotal: Double? = 0.0,
  @SerializedName("id")
  var id: String? = "",
  @SerializedName("line_total")
  var lineTotal: Double? = 0.0,
  @SerializedName("net_line_total")
  var netLineTotal: Double? = 0.0,
  @SerializedName("qty")
  var qty: Int? = 0,
  @SerializedName("unit_price")
  var unitPrice: Double? = 0.0,
  @SerializedName("vat")
  var vat: Double? = 0.0
) : Parcelable