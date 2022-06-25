package com.lifepharmacy.application.model.pcr.pcrlist

import com.google.gson.annotations.SerializedName

data class Seo(

  @SerializedName("seo_title") val seo_title: String,
  @SerializedName("seo_description") val seo_description: String,
  @SerializedName("seo_title_ar") val seo_title_ar: String,
  @SerializedName("seo_description_ar") val seo_description_ar: String
)