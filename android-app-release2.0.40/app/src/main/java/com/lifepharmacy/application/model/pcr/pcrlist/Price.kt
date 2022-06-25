package com.lifepharmacy.application.model.pcr.pcrlist

import com.google.gson.annotations.SerializedName


data class Price(

  @SerializedName("offer_price")
  val offer_price: Int,
  @SerializedName("regular_price")
  val regular_price: Int
)