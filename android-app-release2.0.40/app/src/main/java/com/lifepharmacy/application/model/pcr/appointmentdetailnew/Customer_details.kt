package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName
data class Customer_details (

	@SerializedName("city") val city : String,
	@SerializedName("name") val name : String,
	@SerializedName("email") val email : String,
	@SerializedName("phone") val phone : String,
	@SerializedName("state") val state : String,
	@SerializedName("country") val country : String,
	@SerializedName("street1") val street1 : String
)