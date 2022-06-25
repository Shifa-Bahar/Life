package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class ReturnOrderItem(
  @SerializedName("id")
  var id: String? = "",
  @SerializedName("qty")
  var qty: Int? = 0,
  @SerializedName("sub_order_id")
  var subOrderId: Int? = 0
) : Parcelable