package com.lifepharmacy.application.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.onesignal.OneSignal

object AnalyticsUtil {


//  var segmentAnalytics: Analytics? = null

//  fun setUpAnalytics(context: Context) {
//    // Create an analytics client with the given context and Segment write key.
//    segmentAnalytics = Analytics.Builder(context, ConstantsUtil.SEGMENT_KEY)
//      .trackApplicationLifecycleEvents() // Enable this to record certain application events automatically!
//      .recordScreenViews() // Enable this to record screen views automatically!
//      .build()
//
//// Set the initialized instance as a globally accessible instance.
//    Analytics.setSingletonInstance(segmentAnalytics)
//  }

//  fun setSegmentUser(verifyOTPResponse: VerifyOTPResponse, isPhone: Boolean = true) {
//    if (DateTimeUtil.getUtcTimeToDateObject(
//        verifyOTPResponse.user.createdAt
//      ) != null
//    ) {
//      if (DateTimeUtil.getTimeDifferenceBtwTwoDates(
//          DateTimeUtil.getUtcTimeToDateObject(verifyOTPResponse.user.createdAt)!!,
//          DateTimeUtil.getCurrentDateObject()
//        ) > 4
//      ) {
//        var properties = Properties().putValue(
//          "method", if (isPhone) {
//            "phone"
//          } else {
//            "email"
//          }
//        )
//        segmentAnalytics?.track("Sign In", properties)
//      } else {
//        var properties = Properties().putValue(
//          "method", if (isPhone) {
//            "phone"
//          } else {
//            "email"
//          }
//        )
//        segmentAnalytics?.track("Sign Up", properties)
//      }
//    }
//    setIdentify(verifyOTPResponse.user)
//
//  }

//  fun setIdentify(user: User) {
//    val device = OneSignal.getDeviceState()
//    val userId = device?.userId
//    val traits = Traits()
//    traits.putName(user.name)
//    traits.putEmail(user.email)
//    traits.putPhone(user.phone)
//    traits.putGender(user.gender)
//    traits.putValue("dob", user.dob)
//    traits.putValue("device_id", userId ?: user.deviceId)
//    segmentAnalytics?.identify(user.id.toString(), traits, null)
//  }
//
//
//  fun setSingleLog(context: Context, event: String) {
//    FirebaseApp.initializeApp(context);
//    val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
//    val bundle = Bundle()
//    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, event)
//    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//    segmentAnalytics?.track(event)
//  }

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
//    val properties = Properties()
//    properties["product_id"] = name
//    segmentAnalytics?.track(event, properties)

  }

  fun setEvenWithNamePropertyValue(
    context: Context,
    event: String,
    name: String,
    segmentNameEventName: String = event,
    propertyValue: String = "product_id"
  ) {
    var propertyValue = propertyValue
    if (event == "ScreenOpened") {
      propertyValue = "Screen"
    }
    FirebaseApp.initializeApp(context)
    val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, event)
    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, name)
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//    segmentAnalytics?.track(segmentNameEventName, properties)/

  }


  fun setSearchQuery(context: Context, event: String, name: String) {
    FirebaseApp.initializeApp(context)
    val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, event)
    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, name)
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//    segmentAnalytics?.track(event, properties)

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