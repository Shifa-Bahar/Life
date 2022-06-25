package com.lifepharmacy.application.model.cart

import com.google.gson.annotations.SerializedName
import com.lifepharmacy.application.model.product.ProductDetails

/**
 * Created by Zahid Ali
 */
data class CreateCartRequest(
  @SerializedName("items")
  var items: ArrayList<CartItemRequestModel> = ArrayList(),
  @SerializedName("user_id")
  var userID: String? = "",
  @SerializedName("device_id")
  var deviceID: String? = "",
  @SerializedName("id")
  var cartID: String? = "",
  @SerializedName("coupon_code")
  var couponCode: String? = "",
  @SerializedName("cart_total")
  var cartTotal: Double? = 0.0
)