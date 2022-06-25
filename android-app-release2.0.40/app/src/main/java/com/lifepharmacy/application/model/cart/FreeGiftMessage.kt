package com.lifepharmacy.application.model.cart


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FreeGiftMessage(
  @SerializedName("message")
  var message: String? = "",
  @SerializedName("type")
  var type: String? = ""
) : Parcelable {
  fun getFreeGiftTypeEnum(): GiftMessageEnum {
    return when (type) {
      "pending" -> {
        GiftMessageEnum.PENDING
      }
      else -> {
        GiftMessageEnum.COMPLETE
      }

    }
  }
}