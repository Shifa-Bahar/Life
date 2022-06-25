package com.lifepharmacy.application.model.outlet

import com.google.gson.annotations.SerializedName

data class Outletdata(

  @SerializedName("is_available") val is_available: Boolean,
  @SerializedName("name") val name: String,
  @SerializedName("photo") val photo: String,
  @SerializedName("expiry_date") val expiry_date: String,
  @SerializedName("code") val code: String,
  @SerializedName("link_url") val link_url: String,
  @SerializedName("no_member_url") val no_member_url: String
)