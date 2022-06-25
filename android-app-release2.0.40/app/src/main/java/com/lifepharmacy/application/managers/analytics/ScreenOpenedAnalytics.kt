package com.lifepharmacy.application.managers.analytics

import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.lifepharmacy.application.utils.AnalyticsUtil

fun AnalyticsManagers.categoryScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Categories")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "Categories"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.documentScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "DocumentsListing")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "DocumentsListing"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.homeScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "HomeScreen")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "HomeScreen"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.landingScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "LandingPage")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "LandingPage"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}

fun AnalyticsManagers.notificationDetailScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "NotificationDetailFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "NotificationDetailFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}

fun AnalyticsManagers.notificationListScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "NotificationsFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "NotificationsFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}

fun AnalyticsManagers.offersListScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "OffersListing")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "OffersListing"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.offersBottomSheetScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "OffersBottomSheet")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "OffersBottomSheet"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.transactionScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TransactionDetailFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "TransactionDetailFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.transactionListScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TransactionListFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "TransactionListFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}


fun AnalyticsManagers.voucherDetailScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "VoucherDetailFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "VoucherDetailFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.voucherListScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TransactionListFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "TransactionListFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}

fun AnalyticsManagers.RewardsListScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "RewardsFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "TransactionListFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}

fun AnalyticsManagers.PcrListScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "PcrListFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "TransactionListFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.PcrDateScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "PcrListFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "TransactionListFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}

