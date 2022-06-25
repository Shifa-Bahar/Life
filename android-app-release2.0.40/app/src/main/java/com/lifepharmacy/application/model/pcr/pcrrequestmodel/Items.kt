package com.lifepharmacy.application.model.pcr.pcrrequestmodel
//import android.annotation.SuppressLint
//import android.os.Parcelable
//import com.google.gson.annotations.SerializedName
//import com.lifepharmacy.application.model.product.ProductDetails
//import kotlinx.android.parcel.Parcelize
//@SuppressLint("ParcelCreator")
//data class Items (
//
//  @SerializedName("id") val id : String,
//  @SerializedName("sku")  var sku: String = "",
//  @SerializedName("qty") val qty : Int,
//  @SerializedName("unit_price")  var unitPrice: Double = 0.0,
//  @SerializedName("gross_line_total") var grossLineTotal: Double = 0.0,
//  @SerializedName("discount") var discount: Double = 0.0,
//  @SerializedName("line_total") var lineTotal: Double = 0.0,
//  @SerializedName("net_line_total")var netLineTotal: Double = 0.0,
//  @SerializedName("vat") var vat: Double = 0.0,
//  @SerializedName("offers") val offers : String
//)



import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.pcr.pcrlist.Products
import com.lifepharmacy.application.model.product.ProductDetails
import kotlinx.android.parcel.RawValue

@SuppressLint("ParcelCreator")
@Parcelize
data class Items(

  var id: String = "",
  var sku: String = "",
  var qty: Int = 0,
  @SerializedName("unit_price")
  var unitPrice: Double = 0.0,
  @SerializedName("gross_line_total")
  var grossLineTotal: Double = 0.0,
  var discount: Double? = 0.0,
  @SerializedName("line_total")
  var lineTotal: Double = 0.0,
  var price: Double = 0.0,
  @SerializedName("net_line_total")
  var netLineTotal: Double = 0.0,
  @SerializedName("vat")
  var vat: Double = 0.0,
  @SerializedName("products")
  var products: @RawValue ArrayList<Products>? = ArrayList()
//  @SerializedName("product_details")
//  var productDetails: ProductDetails = ProductDetails(),
//  @SerializedName("product_name")
//  var productName: String = "",
//  var tax: Double = 0.0,
) : Parcelable