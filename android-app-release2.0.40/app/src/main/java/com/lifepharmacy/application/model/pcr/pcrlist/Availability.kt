package com.lifepharmacy.application.model.pcr.pcrlist

import com.google.gson.annotations.SerializedName


data class Availability(

  @SerializedName("instant")
  val instant: Instant,
  @SerializedName("express")
  val express: Express,
  @SerializedName("standard")
  val standard: Standard
)