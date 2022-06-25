package com.lifepharmacy.application.model.pcr.pcrdatetime
import com.google.gson.annotations.SerializedName


data class Booking_slots (

  @SerializedName("date") val date : String,
  @SerializedName("slots") val slots : ArrayList<Slots>
)