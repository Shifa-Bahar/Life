package com.lifepharmacy.application.model.inappmsg

import com.google.gson.annotations.SerializedName

data class Json4Kotlin_Base (

	@SerializedName("success") val success : Boolean,
	@SerializedName("message") val message : String,
	@SerializedName("data") val data : Inappmsgrating
)