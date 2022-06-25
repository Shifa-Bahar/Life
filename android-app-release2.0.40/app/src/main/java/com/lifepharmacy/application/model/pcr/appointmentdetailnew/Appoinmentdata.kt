package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName
import com.lifepharmacy.application.model.product.ProductDetails

//data class Appoinmentdata(
//
//  @SerializedName("status") val status: String,
//  @SerializedName("appointment_details") val appointment_details: Appointment_details,
////  @SerializedName("address") val address: Ad,
////  @SerializedName("products") val products: List<Products>,
////  @SerializedName("reports") val reports: List<String>,
////  @SerializedName("payment_details") val payment_details: Payment_details,
////  @SerializedName("order") val order: Order
//)


data class Appoinmentdata(

  val appointment_details: Appointment_details,
  val address: Address,
  val payment_details: Payment_details,
  val status: String,
  val products: ArrayList<Products> = ArrayList(),
  @SerializedName("reports") val reports : ArrayList<Reports> = ArrayList(),
  val order: Order
)