package com.lifepharmacy.application.model.config


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SlotsMainModel(
  @SerializedName("instant_slots")
  var instantSlots: ArrayList<DeliverySlot>? = ArrayList(),
  @SerializedName("standard_slots")
  var standardSlots: ArrayList<DeliverySlot>? = ArrayList()
) : Parcelable