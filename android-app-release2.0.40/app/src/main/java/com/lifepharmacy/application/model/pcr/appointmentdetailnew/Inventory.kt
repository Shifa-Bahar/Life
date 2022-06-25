package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName

data class Inventory (

	@SerializedName("sku") val sku : Int,
	@SerializedName("upc") val upc : String,
	@SerializedName("quantity") val quantity : String
)