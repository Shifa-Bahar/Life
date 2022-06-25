package com.lifepharmacy.application.model.inappmsg

import com.google.gson.annotations.SerializedName

data class Inappmsgrating(

  @SerializedName("type") val type: String,
  @SerializedName("sub_order_id") val sub_order_id: Int,
  @SerializedName("sub_order_number") val sub_order_number: String,
  @SerializedName("inapp_data") val inapp_data: Inapp_data
)