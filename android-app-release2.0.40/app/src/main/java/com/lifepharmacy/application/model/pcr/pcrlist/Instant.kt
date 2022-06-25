package com.lifepharmacy.application.model.pcr.pcrlist

import com.google.gson.annotations.SerializedName


data class Instant(

  @SerializedName("is_available")
  val is_available: Boolean,
  @SerializedName("qty")
  val qty: Int,
  @SerializedName("store_code")
  val store_code: String
)