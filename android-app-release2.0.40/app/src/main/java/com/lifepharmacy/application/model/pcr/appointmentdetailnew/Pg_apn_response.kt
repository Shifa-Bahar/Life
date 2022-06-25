package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName
data class Pg_apn_response (

	@SerializedName("token") val token : String,
	@SerializedName("cart_id") val cart_id : Int,
	@SerializedName("tran_ref") val tran_ref : String,
	@SerializedName("ipn_trace") val ipn_trace : String,
	@SerializedName("tran_type") val tran_type : String,
	@SerializedName("profile_id") val profile_id : Int,
	@SerializedName("tran_class") val tran_class : String,
	@SerializedName("tran_total") val tran_total : Double,
	@SerializedName("cart_amount") val cart_amount : Double,
	@SerializedName("merchant_id") val merchant_id : Int,
	@SerializedName("payment_info") val payment_info : Payment_info,
	@SerializedName("cart_currency") val cart_currency : String,
	@SerializedName("tran_currency") val tran_currency : String,
	@SerializedName("payment_result") val payment_result : Payment_result,
	@SerializedName("cart_description") val cart_description : String,
	@SerializedName("customer_details") val customer_details : Customer_details,
	@SerializedName("previous_tran_ref") val previous_tran_ref : String
)