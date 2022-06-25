package com.lifepharmacy.application.model.orders.shipment


import com.google.gson.annotations.SerializedName

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeLine(
  @SerializedName("description")
  var description: String? = "",
  @SerializedName("event_title")
  var eventTitle: String? = "",
  @SerializedName("happened")
  var happened: Boolean? = false,
  @SerializedName("status")
  var status: Int? = 0,
  @SerializedName("timestamp")
  var timestamp: String? = "",
  @SerializedName("data")
  var data: ArrayList<KeyValueModel>? = ArrayList()
) : Parcelable