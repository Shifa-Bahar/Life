package com.lifepharmacy.application.model.product


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlin.math.roundToInt

@Parcelize
data class Express(
    @SerializedName("is_available")
    var isAvailable: Boolean? = false,
    @SerializedName("qty")
    var qty: Double? = 0.0
): Parcelable{
  fun getQTY():Int?{
    return qty?.roundToInt()
  }
}