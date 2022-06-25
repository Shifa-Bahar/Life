package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class ReturnOrderRequestBody(
  @SerializedName("items_to_return")
  var itemsToReturn: ArrayList<ReturnOrderItem>? = ArrayList(),
  @SerializedName("reason")
  var reason: Reason? = Reason()
) : Parcelable