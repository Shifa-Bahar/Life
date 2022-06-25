package com.lifepharmacy.application.model.pcr.pcrlist

import com.google.gson.annotations.SerializedName


data class Collections(

  @SerializedName("id")
  val id: String,
  @SerializedName("name")
  val name: String,
  @SerializedName("slug")
  val slug: String
)