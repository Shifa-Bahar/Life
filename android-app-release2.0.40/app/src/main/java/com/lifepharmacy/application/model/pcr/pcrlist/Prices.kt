package com.lifepharmacy.application.model.pcr.pcrlist

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class Prices(
  @SerializedName("country_code")
  var countryCode: String = "",
  var currency: String = "",
  var price: PriceX = PriceX()
) : Parcelable {
}