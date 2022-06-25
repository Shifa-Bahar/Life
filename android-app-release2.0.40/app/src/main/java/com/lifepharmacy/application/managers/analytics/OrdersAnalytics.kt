package com.lifepharmacy.application.managers.analytics

import android.os.Bundle
import com.facebook.appevents.AppEventsConstants
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.FaceBookAnalyticsUtilUtil

fun AnalyticsManagers.nonPrescriptionOrderScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "NonPrescriptionOrderFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "NonPrescriptionOrderFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.orderDetailScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "OrderDetailFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "OrderDetailFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.prescriptionOrderDetailScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "PrescriptionOrderDetailFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "PrescriptionOrderDetailFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.returningProductsScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ReturningProductsFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "ReturningProductsFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.returningRequestedScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ReturnedFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "ReturnedFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)

}