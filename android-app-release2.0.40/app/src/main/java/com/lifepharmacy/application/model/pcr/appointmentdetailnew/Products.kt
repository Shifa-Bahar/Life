package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Availability
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Prices
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Product_details
import com.lifepharmacy.application.model.pcr.pcrlist.Offers
import com.lifepharmacy.application.model.pcr.pcrlist.Products

data class Products(

  @SerializedName("id") val id: String,
  @SerializedName("qty") val qty: Int,
  @SerializedName("sku") val sku: Int,
  @SerializedName("tax") val tax: Double = 0.0,
  @SerializedName("vat") val vat: Double = 0.0,
  @SerializedName("unit") val unit: String,
  @SerializedName("price") val price: Double = 0.0,
//  @SerializedName("offers") val offers: Offers? = Offers(),

  @SerializedName("prices") val prices: ArrayList<Prices> = ArrayList(),
  @SerializedName("discount") val discount: Double = 0.0,
  @SerializedName("products") val products: ArrayList<String> = ArrayList(),
  @SerializedName("tax_rate") val tax_rate: String,
  @SerializedName("line_total") val line_total: Double = 0.0,
  @SerializedName("unit_price") val unit_price: Double = 0.0,
  @SerializedName("availability") val availability: Availability,
  @SerializedName("product_name") val product_name: String,
  @SerializedName("net_line_total") val net_line_total: Double = 0.0,
  @SerializedName("product_details") val product_details: Product_details,
  @SerializedName("gross_line_total") val gross_line_total:Double = 0.0,
  @SerializedName("ax_price_without_vat") val ax_price_without_vat: Double = 0.0
)