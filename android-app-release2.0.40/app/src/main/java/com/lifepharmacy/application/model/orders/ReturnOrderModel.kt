package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.product.ProductDetails

@SuppressLint("ParcelCreator")
@Parcelize
data class ReturnOrderModel(
  @SerializedName("created_at")
  var createdAt: String? = "",
  @SerializedName("id")
  var id: Int? = 0,
  @SerializedName("items")
  var items: ArrayList<OrderItem>? = ArrayList(),
  @SerializedName("reason")
  var reason: String? = "",
  @SerializedName("status")
  var status: Int? = 0,
  @SerializedName("status_label")
  var statusLabel: String? = "",
  @SerializedName("updated_at")
  var updatedAt: String? = "",
  @SerializedName("user_id")
  var userId: Int? = 0
) : Parcelable