package com.lifepharmacy.application.managers.analytics

import android.os.Bundle
import com.facebook.appevents.AppEventsConstants
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.lifepharmacy.application.model.filters.FilterModel
import com.lifepharmacy.application.model.filters.FilterRequestModel
import com.lifepharmacy.application.model.product.DelvieryDetails
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.FaceBookAnalyticsUtilUtil
import com.rudderstack.android.sdk.core.RudderProperty
import org.json.JSONArray
import java.text.FieldPosition
import com.google.gson.Gson


fun AnalyticsManagers.buyItAgainScreenOpen() {
  FirebaseApp.initializeApp(context)
  val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "BuyItAgain")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "BuyItAgain"
//  AnalyticsUtil.segmentAnalytics?.track("Address", properties)
}

fun AnalyticsManagers.productListingScreenOpen() {
  FirebaseApp.initializeApp(context)
  val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ProductListFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "ProductListFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Address", properties)
}

fun AnalyticsManagers.productScreenOpen() {
  FirebaseApp.initializeApp(context)
  val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ProductDetailFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["product_id"] = "ProductDetailFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Product Viewed", properties)
}

fun AnalyticsManagers.productScreenOpen(id: String) {
  FirebaseApp.initializeApp(context)
  val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, id)
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["product_id"] = id
//  AnalyticsUtil.segmentAnalytics?.track("Product Viewed", properties)
}


fun AnalyticsManagers.productViewed(product: ProductDetails, position: Int) {

  val json = product.getShortJsonString(context, position)
//  val properties = Properties()
//  properties["products"] = json
  FirebaseApp.initializeApp(context)
  val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
  val bundle = Bundle()
  bundle.putString("product", json.toString())
  val hashMap = product.getShortHashMap(context, position)
  val properties = RudderProperty()
  properties.putValue(hashMap)
  firebaseAnalytics.logEvent("view_item", bundle)
  rudderClient.track("view_item", properties)
//  AnalyticsUtil.segmentAnalytics?.track("Product Viewed", properties)


  FaceBookAnalyticsUtilUtil.triggerEvent(
    context = context,
    appEventConstant = AppEventsConstants.EVENT_NAME_VIEWED_CONTENT,
    id = product.inventory?.sku ?: "",
    type = "Product",
    amount = product.getRegularPriceWithoutVAT(context)
      ?: 0.0
  )


}

fun AnalyticsManagers.productFetched(
  filterModel: FilterRequestModel,
  products: ArrayList<ProductDetails>,
  skip: Int = 0
) {

  val list = products.mapIndexed { index, productDetails ->
    productDetails.getShortJsonString(context, index + skip)
  }
  val jsonArray: ArrayList<HashMap<String, Any>> = list as ArrayList<HashMap<String, Any>>

  val properties = RudderProperty()
  properties.put("list_id", filterModel.value)
  properties.put("category", filterModel.key)
  properties.put("products", list)

//  AnalyticsUtil.segmentAnalytics?.track("Product List Viewed", properties)/
  val bundle = Bundle()
  bundle.putString("list_id", filterModel.value)
  bundle.putString("category", filterModel.key)
  bundle.putString("products", jsonArray.toString())
  firebaseAnalytics.logEvent("view_item_list", bundle)
  rudderClient.track("view_item_list", properties)
}

fun AnalyticsManagers.productClicked(product: ProductDetails, position: Int) {

  val json = product.getShortJsonString(context, position)
  val hashMap = product.getShortHashMap(context, position)
  val properties = RudderProperty()
  properties.putValue(hashMap)
  val bundle = Bundle()
  bundle.putString("product", json.toString())
  firebaseAnalytics.logEvent("select_item", bundle)
  rudderClient.track("select_item", properties)
//  AnalyticsUtil.segmentAnalytics?.track("Product Clicked", properties)


  FaceBookAnalyticsUtilUtil.triggerEvent(
    context = context,
    appEventConstant = AppEventsConstants.EVENT_NAME_VIEWED_CONTENT,
    id = product.inventory?.sku ?: "",
    type = "Product",
    amount = product.getRegularPriceWithoutVAT(context)
      ?: 0.0
  )
}

