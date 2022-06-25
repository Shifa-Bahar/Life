package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName
import com.lifepharmacy.application.model.payment.CardInfo


data class Payment_details (

  var amount : Double,
  var method : String,
  var purpose : String,
  var created_at : String,
  var remark : String,
  var type : String,
  var status_label : String,
  var is_refunded : Int,
  var updated_at : String,
  var user_id : Int,
  var provider_id : Int,
  var return_order_id : String,
//  @SerializedName("details") val details : Details,
//  var details : ArrayList<Details> = ArrayList(),
  var id : Int,
  var order_id : Int,
  var card_info : CardInfo,
  var status : Int

)