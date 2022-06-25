package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName
data class Instant (

	@SerializedName("qty") val qty : Int,
	@SerializedName("store_code") val store_code : String,
	@SerializedName("is_available") val is_available : Boolean
)