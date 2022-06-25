package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat

@SuppressLint("ParcelCreator")
@Parcelize
data class OrderItem(
  var discount: Double = 0.0,
  var id: String = "",
  @SerializedName("line_total")
  var lineTotal: Double = 0.0,
  var price: Double = 0.0,
  @SerializedName("product_details")
  var productDetails: ProductDetails = ProductDetails(),
  @SerializedName("product_name")
  var productName: String = "",
  var qty: Int = 0,
  var sku: String = "",
  var tax: Double = 0.0,
  @SerializedName("gross_line_total")
  var grossLineTotal: Double = 0.0,
  @SerializedName("vat")
  var vat: Double = 0.0,
  @SerializedName("unit_price")
  var unitPrice: Double = 0.0,
  @SerializedName("net_line_total")
  var netLineTotal: Double = 0.0,
) : Parcelable {

  fun getShortJsonString(context: Context, position: Int = 1, qty: Int? = null): HashMap<String, Any> {
    val preference: SharedPreferences =
      context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val compainID = preference.getString(
      ConstantsUtil.SH_COMPAIN_ID,
      null
    )
    val hashMap = HashMap<String, Any>()
    hashMap["item_id"] = sku ?: ""
    hashMap["item_name"] = productName
    hashMap["currency"] = "AED"
    hashMap["discount"] = discount?: ""
    hashMap["price"] = lineTotal?: ""
    hashMap["quantity"] = qty.toString()
    return hashMap
  }

}