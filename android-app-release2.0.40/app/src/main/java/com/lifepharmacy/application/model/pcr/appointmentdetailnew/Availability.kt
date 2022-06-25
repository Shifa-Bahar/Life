package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName
data class Availability (

	@SerializedName("express") val express : Express,
	@SerializedName("instant") val instant : Instant,
	@SerializedName("standard") val standard : Standard
)