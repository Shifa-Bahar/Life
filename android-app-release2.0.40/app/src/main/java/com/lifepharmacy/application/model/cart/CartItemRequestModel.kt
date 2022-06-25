package com.lifepharmacy.application.model.cart


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class CartItemRequestModel(
  @SerializedName("id")
  var id: String? = "",
  @SerializedName("qty")
  var qty: Int? = 0,
  @SerializedName("offer_id")
  var offerId: String? = null,
) : Parcelable