package com.lifepharmacy.application.model.pcr.pcrlist

import com.google.gson.annotations.SerializedName

data class Label(

  @SerializedName("_id")
  val _id: String,
  @SerializedName("id")
  val id: String,
  @SerializedName("label_text")
  val label_text: String,
  @SerializedName("icon_type")
  val icon_type: Int,
  @SerializedName("color_code")
  val color_code: String,
  @SerializedName("active")
  val active: Boolean,
  @SerializedName("seo")
  val seo: Seo,
  @SerializedName("updated_at")
  val updated_at: String,
  @SerializedName("created_at")
  val created_at: String
)