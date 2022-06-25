package com.lifepharmacy.application.model.pcr.pcrlist

import com.google.gson.annotations.SerializedName

data class Options(

  @SerializedName("key")
  val key: String,
  @SerializedName("value")
  val value: String,
  @SerializedName("order")
  val order: Int
)