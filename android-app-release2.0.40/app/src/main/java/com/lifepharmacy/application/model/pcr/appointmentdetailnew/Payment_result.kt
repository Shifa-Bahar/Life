package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName

data class Payment_result (

	@SerializedName("avs_result") val avs_result : String,
	@SerializedName("cvv_result") val cvv_result : String,
	@SerializedName("response_code") val response_code : String,
	@SerializedName("response_status") val response_status : String,
	@SerializedName("response_message") val response_message : String,
	@SerializedName("transaction_time") val transaction_time : String
)