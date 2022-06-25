package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName

data class Price (

	@SerializedName("offer_price") val offer_price : Double,
	@SerializedName("regular_price") val regular_price : Double
)