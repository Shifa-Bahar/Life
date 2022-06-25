package com.lifepharmacy.application.model.pcr.pcrdatetime
import com.google.gson.annotations.SerializedName

data class Slots (

  @SerializedName("time") val time : String,
  @SerializedName("available_capacity") val available_capacity : Int,
  @SerializedName("active") val active : Boolean,
  @SerializedName("additional_charges") val additional_charges : Boolean,
  @SerializedName("additional_charges_amount") val additional_charges_amount : Int
)