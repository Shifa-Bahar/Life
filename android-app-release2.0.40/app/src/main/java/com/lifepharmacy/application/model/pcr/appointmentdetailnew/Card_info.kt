package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName
data class Card_info (

	@SerializedName("card_type") val card_type : String,
	@SerializedName("expiryYear") val expiryYear : Int,
	@SerializedName("card_scheme") val card_scheme : String,
	@SerializedName("expiryMonth") val expiryMonth : Int,
	@SerializedName("payment_method") val payment_method : String,
	@SerializedName("payment_description") val payment_description : String
)