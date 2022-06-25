package com.lifepharmacy.application.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.utils.universal.ConstantsUtil


object FaceBookAnalyticsUtilUtil {
  fun triggerEvent(
    context: Context,
    appEventConstant: String,
    id: String,
    type: String,
    amount: Double
  ) {

    val preference: SharedPreferences =
      context.applicationContext.getSharedPreferences(
        context.packageName,
        Context.MODE_PRIVATE
      )
    val currency = preference.getString(
      ConstantsUtil.SH_CURRENCY,
      "AED"
    )
    val logger = AppEventsLogger.newLogger(context)
    val params = Bundle()
    params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency)
    params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, type)
    params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, id)


    logger.logEvent(
      appEventConstant,
      amount,
      params
    )
  }

  fun setUserPlayerId(context: Context) {
    FirebaseApp.initializeApp(context)
    val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    val preference: SharedPreferences =
      context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "AppLaunched")
    firebaseAnalytics.setUserProperty(
      "userPlayerId",
      preference.getString(ConstantsUtil.SH_FCM_TOKEN, null)
    )
  }

  fun setEvenWithName(context: Context, event: String, name: String) {
    FirebaseApp.initializeApp(context)
    val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, event)
    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, name)
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
  }

  fun setUserData(context: Context, user: User, latLong: LatLng) {
    FirebaseApp.initializeApp(context);
    val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    val bundle = Bundle()
    firebaseAnalytics.setUserProperty("userId", user.id.toString())
    firebaseAnalytics.setUserProperty("userPhone", user.phone.toString())
    firebaseAnalytics.setUserProperty("userName", user.name.toString())
    firebaseAnalytics.setUserProperty("userLocation", "${latLong.latitude},${latLong.longitude}")
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
  }
}