package com.lifepharmacy.application.model.home


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class PriceX(
  @SerializedName("offer_price")
  var offerPrice: Double = 0.0,
  @SerializedName("regular_price")
  var regularPrice: Double = 0.0
) : Parcelable