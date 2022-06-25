package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Rating(
  @SerializedName("created_at")
  var createdAt: String? = "",
  @SerializedName("entity")
  var entity: String? = "",
  @SerializedName("entity_id")
  var entityId: String? = "",
  @SerializedName("review")
  var review: String? = "",
  @SerializedName("sub_order_id")
  var subOrderId: Int? = 0,
//  @SerializedName("tags")
//  var tags: String? = "",
  @SerializedName("updated_at")
  var updatedAt: String? = "",
  @SerializedName("user_id")
  var userId: Int? = 0,
  @SerializedName("value")
  var value: Float? = 0F
) : Parcelable