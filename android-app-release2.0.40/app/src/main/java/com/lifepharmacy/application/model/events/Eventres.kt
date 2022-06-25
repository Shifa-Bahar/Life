package com.lifepharmacy.application.model.events


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

data class Eventres(

  @SerializedName("user_properties") val user_properties: List<String>,
  @SerializedName("event_type") val event_type: List<String>,
  @SerializedName("event_data") val event_data: List<String>,
  @SerializedName("source") val source: List<String>,
  @SerializedName("channel") val channel: List<String>
)