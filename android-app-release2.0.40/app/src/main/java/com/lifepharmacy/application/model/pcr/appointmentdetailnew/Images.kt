package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName

data class Images (

	@SerializedName("other_images") val other_images : List<String>,
	@SerializedName("featured_image") val featured_image : String,
	@SerializedName("gallery_images") val gallery_images : List<String>
)