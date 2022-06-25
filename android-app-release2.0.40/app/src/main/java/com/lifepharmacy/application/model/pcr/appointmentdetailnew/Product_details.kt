package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName
import com.lifepharmacy.application.model.pcr.pcrlist.Prices

data class Product_details(

//  @SerializedName("id") val id: String,
//  @SerializedName("sku") val sku: Int,
//  @SerializedName("slug") val slug: String,
  @SerializedName("title") val title: String,
//  @SerializedName("active") val active: Boolean,
  @SerializedName("images") val images: Images,
//	@SerializedName("prices") val prices : String,
//  @SerializedName("prices") val prices: ArrayList<Prices> = ArrayList(),
//  @SerializedName("inventory") val inventory: Inventory,
//  @SerializedName("is_taxable") val is_taxable: Boolean,
//  @SerializedName("vat_percentage") val vat_percentage: Double,
//  @SerializedName("maximum_salable_qty") val maximum_salable_qty: Int
)