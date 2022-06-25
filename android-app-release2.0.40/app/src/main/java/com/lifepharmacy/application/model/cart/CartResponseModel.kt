package com.lifepharmacy.application.model.cart


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.config.DeliverySlot
import com.lifepharmacy.application.model.product.ProductDetails

@SuppressLint("ParcelCreator")
@Parcelize
data class CartResponseModel(
  @SerializedName("user_id")
  var userId: Int? = 0,
  @SerializedName("user_type")
  var userType: String? = "",
  @SerializedName("items")
  var items: ArrayList<CartModel> = ArrayList(),
  @SerializedName("id")
  var id: Int? = 0,
  @SerializedName("coupon")
  var couponModel: CouponModel? = CouponModel(),
  @SerializedName("out_of_stock")
  var outOfStock: ArrayList<CartModel> = ArrayList(),
  @SerializedName("gift_message")
  var giftMessage: FreeGiftMessage? = FreeGiftMessage(),
  @SerializedName("gift_items")
  var giftItems: ArrayList<CartModel> = ArrayList(),
  @SerializedName("delivery_instructions")
  var deliveryInstructions: ArrayList<DeliveryInstructions>? = ArrayList(),
  @SerializedName("alert_message")
  var alertmessage: AlertMessages? = AlertMessages(),
  @SerializedName("active_slots")
  var instantSlots: ArrayList<DeliverySlot>? = ArrayList(),
  @SerializedName("standard_slots")
  var standardSlots: ArrayList<DeliverySlot>? = ArrayList()
//  @SerializedName("active_slots")
//  var activeSlots: ArrayList<DeliverySlot>? = ArrayList(),
) : Parcelable