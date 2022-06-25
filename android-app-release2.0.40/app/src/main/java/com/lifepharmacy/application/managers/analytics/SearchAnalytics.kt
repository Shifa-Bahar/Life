package com.lifepharmacy.application.managers.analytics

import android.Manifest
import android.os.Bundle
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.event.EventObjects
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.google.firebase.analytics.FirebaseAnalytics
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.search.agolia.Hits
import java.text.FieldPosition


fun AnalyticsManagers.searchScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SearchFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "SearchFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}

fun AnalyticsManagers.searchedTerm(term: String) {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ProductsSearched")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, term)
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["ProductsSearched"] = term
//  AnalyticsUtil.segmentAnalytics?.track("Products Searched", properties)
}

fun AnalyticsManagers.clickAfterSearched(
  queryID: String,
  hits: Hits
) {
  val position = hits.position ?: 0 + 1
  Insights.shared?.clickedAfterSearch(
    eventName = hits.index ?: "",
    queryId = hits.queryID ?: "",
    objectIDs = EventObjects.IDs(hits.objectID.toString()),
    positions = listOf(
      position
    ),

    )
}

fun AnalyticsManagers.productsAfterSearched(query: String, list: ArrayList<ProductDetails>) {
  val listOFIds = list.map {
    it.id
  }

  val afHashMap = HashMap<String, Any>()
  afHashMap[AFInAppEventParameterName.CONTENT_LIST] = listOFIds
  afHashMap[AFInAppEventParameterName.SEARCH_STRING] = query

  AppsFlyerLib.getInstance().logEvent(this.context, AFInAppEventType.SEARCH, afHashMap)
}

fun AnalyticsManagers.searchedProductClicked(queryID: String) {

}