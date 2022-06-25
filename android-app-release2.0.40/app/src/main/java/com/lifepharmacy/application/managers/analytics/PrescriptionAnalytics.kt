package com.lifepharmacy.application.managers.analytics

import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.lifepharmacy.application.utils.AnalyticsUtil


fun AnalyticsManagers.choosePrescriptionScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ChoosePrescriptionMainFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "ChoosePrescriptionMainFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)/
}

fun AnalyticsManagers.prescriptionScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "PrescriptionFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "PrescriptionFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}
fun AnalyticsManagers.withOutPrescriptionScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "WithOutPrescriptionFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "WithOutPrescriptionFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}

