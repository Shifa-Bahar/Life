package com.lifepharmacy.application.managers.analytics

import android.os.Bundle
import com.appsflyer.AppsFlyerLib
import com.facebook.appevents.AppEventsConstants
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.cart.CartResponseModel
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.FaceBookAnalyticsUtilUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Extensions.doubleDigitDouble
import com.rudderstack.android.sdk.core.RudderProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.getApplicationContext
import com.lifepharmacy.application.utils.CalculationUtil


fun AnalyticsManagers.cartScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Cart")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "Cart"
//  AnalyticsUtil.segmentAnalytics?.track("Cart Viewed", properties)
  rudderClient.screen("Cart Viewed")
}

fun AnalyticsManagers.cartCheckOutStartedButtonClicked() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "CheckoutInitiated")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  AnalyticsUtil.segmentAnalytics?.track("CheckoutInitiated")
}

fun AnalyticsManagers.couponEntered(value: String) {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Coupon Entered")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value)
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Coupon Entered"] = value
//  AnalyticsUtil.segmentAnalytics?.track("coupon", properties)
}

fun AnalyticsManagers.rewardEntered(value: String) {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Reward Entered")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value)
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Coupon Entered"] = value
//  AnalyticsUtil.segmentAnalytics?.track("coupon", properties)
}

fun AnalyticsManagers.couponApplied(value: String) {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Coupon Applied")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value)
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Coupon Applied"] = value
//  AnalyticsUtil.segmentAnalytics?.track("coupon", properties)
}

fun AnalyticsManagers.couponDenied(value: String) {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Coupon Denied")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value)
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Coupon Denied"] = value
//  AnalyticsUtil.segmentAnalytics?.track("coupon", properties)
}

fun AnalyticsManagers.paymentScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Payment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "Payment"
//  AnalyticsUtil.segmentAnalytics?.track("Checkout Started", properties)
}

fun AnalyticsManagers.initiateCheckOutForListProducts(items: ArrayList<CartModel>) {
  CoroutineScope(Dispatchers.IO).launch {
    var price: Double? = 0.0
    var qty: Int? = 1

    var productIdArray = items.map {
      it.productDetails.id
    }


    var categoryNames = items.map {
      it.productDetails.categories?.firstOrNull()?.name
    }
    val priceArray = items.map { item ->
      (CalculationUtil.getUnitPrice(
        item.productDetails,
        this@initiateCheckOutForListProducts.context
      )
        .times(item.qty))
    }

    val qtyArray = items.map { item ->
      item.qty
    }
    if (!priceArray.isNullOrEmpty()) {
      price = priceArray.reduce { acc, d -> acc + d }
    }
    if (!qtyArray.isNullOrEmpty()) {
      qty = qtyArray.reduce { acc, d -> acc + d }
    }
    val eventValue: MutableMap<String, Any> = HashMap()
    eventValue[AFInAppEventParameterName.PRICE] = price ?: 0.0
    eventValue[AFInAppEventParameterName.CONTENT_ID] = productIdArray
    eventValue[AFInAppEventParameterName.CONTENT_TYPE] = categoryNames
    eventValue[AFInAppEventParameterName.QUANTITY] = qtyArray
    AppsFlyerLib.getInstance()
      .logEvent(getApplicationContext(), AFInAppEventType.INITIATED_CHECKOUT, eventValue)

    for (item in items) {
      this@initiateCheckOutForListProducts.initiateCheckout(item.productDetails)
    }
  }
}

fun AnalyticsManagers.orderCreated(createOrderResponseModel: OrderResponseModel) {
  val list = createOrderResponseModel.items?.mapIndexed { index, item ->
    item.productDetails.getShortJsonString(context, position = 0, qty = item.qty)
  }
  val properties = RudderProperty()
  properties.putValue("products", list)
  properties.putValue("order_id", createOrderResponseModel.id.toString())
  properties.putValue("total", createOrderResponseModel.total?.currencyFormat())
  rudderClient.track("begin_checkout", properties)
  val bundle = Bundle()
  bundle.putString("products", list.toString())
  bundle.putString("order_id", createOrderResponseModel.id.toString())
  bundle.putString("total", createOrderResponseModel.total?.currencyFormat())
  firebaseAnalytics.logEvent("begin_checkout", bundle)
}

fun AnalyticsManagers.orderCompleted(
  transactionModel: TransactionModel,
  OrderResponseModel: OrderResponseModel
) {
  val productIdArray = OrderResponseModel.items?.map {
    it.productDetails.id
  }
  var categoryNames = OrderResponseModel.items?.map {
    it.productDetails.categories?.firstOrNull()?.name
  }
  val qtyArray = OrderResponseModel.items?.map { item ->
    item.qty
  }
  val eventValue: MutableMap<String, Any> = HashMap()
  eventValue[AFInAppEventParameterName.PRICE] = OrderResponseModel.total ?: 0.0
  eventValue[AFInAppEventParameterName.PROJECTED_REVENUE] = OrderResponseModel.total ?: 0.0
  eventValue[AFInAppEventParameterName.CONTENT_ID] = productIdArray ?: ""
  eventValue[AFInAppEventParameterName.CONTENT_TYPE] = categoryNames?:""
  eventValue[AFInAppEventParameterName.QUANTITY] = qtyArray?:""
  eventValue[AFInAppEventParameterName.CURRENCY] = "AED"
  eventValue[AFInAppEventParameterName.ORDER_ID] = transactionModel.orderId
  eventValue[AFInAppEventParameterName.RECEIPT_ID] = transactionModel.id
  AppsFlyerLib.getInstance()
    .logEvent(getApplicationContext(), AFInAppEventType.PURCHASE, eventValue)


  val list = OrderResponseModel.items?.mapIndexed { index, item ->
    item.productDetails.getShortJsonString(context, position = 0, qty = item.qty)
  }

  val itemlist = OrderResponseModel.items?.mapIndexed { index, item ->
    item.getShortJsonString(context, position = 0, qty = item.qty)
  }

//  val properties = RudderProperty()
//  properties.putValue("products", list)
//  properties.putValue("order_id", transactionModel.orderId)
//  properties.putValue("value", OrderResponseModel.total?.currencyFormat())
//  rudderClient.track("purchase", properties)
//  val bundle = Bundle()
//  bundle.putString("products", list.toString())
//  bundle.putString("order_id", transactionModel.orderId.toString())
//  bundle.putString("value", OrderResponseModel.total?.currencyFormat())
//  firebaseAnalytics.logEvent("purchase", bundle)

  val properties = RudderProperty()
  properties.putValue("currency", "AED")
  properties.putValue("transaction_id", transactionModel.id)
  properties.putValue("value", OrderResponseModel.total?.doubleDigitDouble())
  properties.putValue("tax", OrderResponseModel.tax?.doubleDigitDouble())
  properties.putValue("shipping", OrderResponseModel.deliveryFee?.doubleDigitDouble())
  properties.putValue("items", itemlist)
  rudderClient.track("purchase", properties)

  //for google analytics
  val bundle = Bundle()
  bundle.putString("currency", "AED")
  bundle.putString("transaction_id", transactionModel.id.toString())
  bundle.putString("value", OrderResponseModel.total?.currencyFormat())
  bundle.putString("tax", OrderResponseModel.tax?.currencyFormat())
  bundle.putString("shipping", OrderResponseModel.deliveryFee?.currencyFormat())
  bundle.putString("items", itemlist.toString())
  firebaseAnalytics.logEvent("purchase", bundle)
}

fun AnalyticsManagers.initiateCheckout(productDetails: ProductDetails) {
  FaceBookAnalyticsUtilUtil.triggerEvent(
    context = context,
    appEventConstant = AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT,
    id = productDetails?.id ?: "",
    type = "Product",
    amount = productDetails?.getRegularPriceWithoutVAT(context)
      ?: 0.0
  )
}

fun AnalyticsManagers.completedCheckOutForListProducts(items: ArrayList<CartModel>) {
  CoroutineScope(Dispatchers.IO).launch {
    for (item in items) {
      this@completedCheckOutForListProducts.completedCheckout(item.productDetails)
    }
  }
}

fun AnalyticsManagers.completedCheckout(productDetails: ProductDetails) {
  FaceBookAnalyticsUtilUtil.triggerEvent(
    context = context,
    appEventConstant = AppEventsConstants.EVENT_NAME_PURCHASED,
    id = productDetails?.id ?: "",
    type = "Product",
    amount = productDetails?.getRegularPriceWithoutVAT(context)
      ?: 0.0
  )
}

fun AnalyticsManagers.checkOutCompleted() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "CheckoutCompleted")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  AnalyticsUtil.segmentAnalytics?.track("CheckoutCompleted")
}

fun AnalyticsManagers.checkOutFailed() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "CheckoutFailed")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  AnalyticsUtil.segmentAnalytics?.track("CheckoutFailed")
}

fun AnalyticsManagers.transactionFailed() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "TransactionFailed")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  AnalyticsUtil.segmentAnalytics?.track("TransactionFailed")
}
fun AnalyticsManagers.splashScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SplashScreen")
//  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
  firebaseAnalytics.logEvent("android_app_launch", bundle)
//  val properties = Properties()
//  properties["Screen"] = "Cart"
//  AnalyticsUtil.segmentAnalytics?.track("Cart Viewed", properties)
  rudderClient.screen("Splash Viewed")
}
fun AnalyticsManagers.addToCart(productDetails: ProductDetails, position: Int, qty: Int = 1) {
  val json = productDetails.getShortJsonString(context = context, position = position, qty = qty)
//  val properties = Properties()
//  properties["products"] = json

  val bundle = Bundle()
  bundle.putString("product", json.toString())
  firebaseAnalytics.logEvent("add_to_cart", bundle)
  val hashMap = productDetails.getShortHashMap(context, position)

  val properties = RudderProperty()
  properties.putValue(hashMap)
  rudderClient.track("add_to_cart", properties)
//  AnalyticsUtil.segmentAnalytics?.track("Product Added", properties)
  val hashMapFlyer = productDetails.getShortHashForAppFlyMap(context, position)
  AppsFlyerLib.getInstance().logEvent(this.context, AFInAppEventType.ADD_TO_CART, hashMapFlyer);

  FaceBookAnalyticsUtilUtil.triggerEvent(
    context = context,
    appEventConstant = AppEventsConstants.EVENT_NAME_ADDED_TO_CART,
    id = productDetails?.id ?: "",
    type = "Product",
    amount = productDetails?.getRegularPriceWithoutVAT(context)
      ?: 0.0
  )
}

fun AnalyticsManagers.removeFromCart(productDetails: ProductDetails, position: Int, qty: Int = 1) {
  val json = productDetails.getShortJsonString(context = context, position = position, qty = qty)
//  val properties = Properties()
//  properties["products"] = json
  FirebaseApp.initializeApp(context)
  val bundle = Bundle()
  bundle.putString("product", json.toString())
  firebaseAnalytics.logEvent("remove_from_cart", bundle)
  val hashMap = productDetails.getShortHashMap(context, position)
  val properties = RudderProperty()
  properties.putValue(hashMap)
  rudderClient.track("remove_from_cart", properties)
//  AnalyticsUtil.segmentAnalytics?.track("Product Removed", properties)
  FaceBookAnalyticsUtilUtil.triggerEvent(
    context = context,
    appEventConstant = AppEventsConstants.EVENT_NAME_ADDED_TO_CART,
    id = productDetails?.id ?: "",
    type = "Product",
    amount = productDetails?.getRegularPriceWithoutVAT(context)
      ?: 0.0
  )
}

fun AnalyticsManagers.cartViewed(cartResponseModel: CartResponseModel) {
  val list = cartResponseModel.items.mapIndexed { index, item ->
    item.productDetails.getShortJsonString(context, position = 0, qty = item.qty)
  }
  val jsonArray = JSONArray(list)
//  val properties = Properties()
//  properties["cart_id"] = cartResponseModel.id
//  properties["products"] = jsonArray.toString()
  FirebaseApp.initializeApp(context)
  val bundle = Bundle()
  bundle.putString("cart_id", cartResponseModel.id.toString())
  bundle.putString("products", jsonArray.toString())
  firebaseAnalytics.logEvent("view_cart", bundle)
  val properties = RudderProperty()
  properties.putValue("cart_id", cartResponseModel.id)
  properties.putValue("products", list)
  rudderClient.track("view_cart", properties)
//  AnalyticsUtil.segmentAnalytics?.track("Cart Viewed", properties)

}