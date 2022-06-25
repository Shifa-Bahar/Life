package com.lifepharmacy.application.model.pcr.pcrlist

import com.google.gson.annotations.SerializedName


data class Pcrbase(

  @SerializedName("success")
  val success: Boolean,
  @SerializedName("message")
  val message: String,
  @SerializedName("data")
  val data: PcrData
)