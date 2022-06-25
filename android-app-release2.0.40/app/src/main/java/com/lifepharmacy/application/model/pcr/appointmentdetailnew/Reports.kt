package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName
data class Reports (

	@SerializedName("url") val url : String,
	@SerializedName("name") val name : String,
	@SerializedName("upload_by") val upload_by : Int,
	@SerializedName("uploaded_time") val uploaded_time : String
)