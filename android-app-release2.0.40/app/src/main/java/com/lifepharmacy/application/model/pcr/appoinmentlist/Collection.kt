package com.lifepharmacy.application.model.pcr.appoinmentlist

import com.google.gson.annotations.SerializedName


data class Collection (

  @SerializedName("_id") val _id : String,
  @SerializedName("id") val id : String,
  @SerializedName("name") val name : String,
  @SerializedName("slug") val slug : String,
  @SerializedName("images") val images : Images
)