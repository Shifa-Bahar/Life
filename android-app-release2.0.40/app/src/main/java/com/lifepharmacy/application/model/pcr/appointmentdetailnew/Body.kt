package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName

data class Body (

	@SerializedName("payment_url") val payment_url : String,
	@SerializedName("payment_fail_url") val payment_fail_url : String,
	@SerializedName("payment_success_url") val payment_success_url : String
)