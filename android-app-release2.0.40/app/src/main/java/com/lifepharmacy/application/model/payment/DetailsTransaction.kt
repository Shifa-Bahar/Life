package com.lifepharmacy.application.model.payment


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class DetailsTransaction(
  @SerializedName("return_order_id")
  var returnOrderId: Double? = 0.0,
) : Parcelable