package com.lifepharmacy.application.model.config


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeliverySlot(
  @SerializedName("fees")
  var fees: Double? = 0.0,
  @SerializedName("title")
  var title: String? = "",
  @SerializedName("slot_id")
  var slotId: Int? = 0,
  @SerializedName("subtitle")
  var subtitle: String? = "",
  @SerializedName("label")
  var label: String? = "",
  @SerializedName("time")
  var time: String? = "",
  @SerializedName("active")
  var isActive: Boolean? = false,
  @SerializedName("selected")
  var selected: Boolean? = false,
) : Parcelable