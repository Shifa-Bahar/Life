package com.lifepharmacy.application.managers.analytics

import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.lifepharmacy.application.model.filters.FilterMainRequest
import com.lifepharmacy.application.utils.AnalyticsUtil

fun AnalyticsManagers.filterScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "FilterSheet")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "FilterSheet"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}

fun AnalyticsManagers.filterApplied(filters: FilterMainRequest) {
  val gson = Gson()
  var stringFilter = gson.toJson(filters)
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Filter")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, stringFilter)
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Filter"] = stringFilter
//  AnalyticsUtil.segmentAnalytics?.track("Product List Filtered", properties)
}