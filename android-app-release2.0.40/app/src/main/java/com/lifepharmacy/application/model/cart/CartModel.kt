package com.lifepharmacy.application.model.cart

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.lifepharmacy.application.model.product.ProductDetails
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zahid Ali
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class CartModel(
  var qty: Int = 1,
  @SerializedName("product")
  var productDetails: ProductDetails = ProductDetails(),
  var isInstant: Boolean = false,
  @SerializedName("sku")
  var sku: String? = "",
  @SerializedName("rating")
  var rating: String? = "",
  var discountedAmount: Double? = 0.0,
  var cartGrossTotal: Double? = 0.0,
  var isFree: Boolean = false,
  var cartVAT: Double? = 0.0,
  var regularVAT: Double? = 0.0,
  @SerializedName("is_changed")
  var isChanged: Boolean = false,
  @SerializedName("only_instant")
  var onlyInstant: Boolean = false,
  @SerializedName("only_express")
  var onlyExpress: Boolean = false,
  @SerializedName("added_qty")
  var addedQTY: Int = 1,
) : Parcelable {

  fun getQtyInString(): String {
    return qty.toString()
  }
}