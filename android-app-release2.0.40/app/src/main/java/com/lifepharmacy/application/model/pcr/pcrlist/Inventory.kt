package com.lifepharmacy.application.model.pcr.pcrlist

import com.google.gson.annotations.SerializedName


data class Inventory(

  @SerializedName("sku")
  val sku: Int,
  @SerializedName("quantity")
  val quantity: String,
  @SerializedName("upc")
  val upc: String
)