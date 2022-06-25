package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName
data class Order (

	@SerializedName("id") var id : Int,
	@SerializedName("order_id") var order_id : String,
	@SerializedName("sub_total") var sub_total : Double? = 0.0,
	@SerializedName("discount") var discount : Double? = 0.0,
	@SerializedName("tax") var tax: Double? = 0.0,
	@SerializedName("total") var total : Double? = 0.0,
)