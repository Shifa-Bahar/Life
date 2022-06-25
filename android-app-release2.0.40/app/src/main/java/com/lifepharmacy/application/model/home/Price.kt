package com.lifepharmacy.application.model.home


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Price(
  @SerializedName("country_code")
  var countryCode: String = "",
  var currency: String = "",
  var price: PriceX = PriceX()
) : Parcelable {
}