package com.lifepharmacy.application.model.product


import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import com.lifepharmacy.application.R
import com.lifepharmacy.application.enums.ShipmentType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Availability(
  @SerializedName("express")
  var express: Express? = Express(),
  @SerializedName("instant")
  var instant: Instant? = Instant(),
  @SerializedName("standard")
  var standard: Instant? = Instant()
) : Parcelable {
  fun getAvailability(quantity: Int = 0): ShipmentType {
    if (quantity == 0) {
      return if (instant != null && instant?.isAvailable == true) {
        ShipmentType.INSTANT
      } else if (express != null && express?.isAvailable == true) {
        ShipmentType.EXPRESS
      } else if (standard != null && standard?.isAvailable == true) {
        ShipmentType.STANDARD
      } else {
        ShipmentType.OUT_OF_STOCK
      }
    } else {
      if (instant?.isAvailable == true) {
        if (quantity <= instant?.getQTY() ?: 0) {
          return ShipmentType.INSTANT
        }
      }
      if (express?.isAvailable == true) {
        if (quantity <= express?.getQTY() ?: 0) {
          return ShipmentType.EXPRESS
        }
      }
      if (standard?.isAvailable == true) {
        if (quantity <= standard?.getQTY() ?: 0) {
          return ShipmentType.STANDARD
        }
      }
      return ShipmentType.OUT_OF_STOCK
    }
  }
}