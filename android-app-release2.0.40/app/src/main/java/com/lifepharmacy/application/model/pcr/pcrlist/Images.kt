package com.lifepharmacy.application.model.pcr.pcrlist

import com.google.gson.annotations.SerializedName


data class Images (

  @SerializedName("featured_image") val featured_image : String,
  @SerializedName("other_images") val other_images : List<String>,
  @SerializedName("gallery_images") val gallery_images : List<String>
)

data class PcrImages (

  @SerializedName("logo") val logo : String,
  @SerializedName("banner") val banner : String,

)