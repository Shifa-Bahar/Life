package com.lifepharmacy.application.managers.analytics

import android.content.Context
import android.os.Bundle
import com.algolia.instantsearch.insights.Insights
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.DateTimeUtil
import com.onesignal.OneSignal
import com.rudderstack.android.sdk.core.RudderClient
import com.rudderstack.android.sdk.core.RudderProperty
import com.rudderstack.android.sdk.core.RudderTraits

fun AnalyticsManagers.otpScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "OTPScreen")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "OTPScreen"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}

fun AnalyticsManagers.numberScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "NumberRequestOTP")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "NumberRequestOTP"
//  AnalyticsUtil.segmentAnalytics?.track("User", properties)
}

fun AnalyticsManagers.emailScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "EmailRequestOTP")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "EmailRequestOTP"
//  AnalyticsUtil.segmentAnalytics?.track("User", properties)
}

fun AnalyticsManagers.userSkipRegistration() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "UserSkippedRegistration")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  AnalyticsUtil.segmentAnalytics?.track("UserSkippedRegistration")
}

fun AnalyticsManagers.requestOtp() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "OtpRequested")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  AnalyticsUtil.segmentAnalytics?.track("OtpRequested")
}

fun AnalyticsManagers.verifyButtonClicked() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "UserRegistered")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  AnalyticsUtil.segmentAnalytics?.track("UserRegistered")
}

fun AnalyticsManagers.userVerified(verifyOTPResponse: VerifyOTPResponse, isPhone: Boolean = true) {
  setUserData(context, verifyOTPResponse.user, this.storageManagers.getLatLong())
  setUser(verifyOTPResponse, rudderClient, isPhone)
  setIdentify(verifyOTPResponse.user, rudderClient)
  this.pusherBeamManager.setUser()
}


fun AnalyticsManagers.addressListingScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "AddressListing")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "OTPScreen"
//  AnalyticsUtil.segmentAnalytics?.track("Address", properties)
}

fun AnalyticsManagers.newAddressScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "NewAddressMap")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "NewAddressMap"
//  AnalyticsUtil.segmentAnalytics?.track("Address", properties)
}

fun AnalyticsManagers.addressDetailScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "NewAddressDetail")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "NewAddressDetail"
//  AnalyticsUtil.segmentAnalytics?.track("Address", properties)
}


fun AnalyticsManagers.profileScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ProfileFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "ProfileFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}


fun AnalyticsManagers.userBasicScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "UserBasicInfoFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "UserBasicInfoFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}


fun AnalyticsManagers.wishListScreenOpen() {
  val bundle = Bundle()
  bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ScreenOpened")
  bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "WishListFragment")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//  val properties = Properties()
//  properties["Screen"] = "WishListFragment"
//  AnalyticsUtil.segmentAnalytics?.track("Screen", properties)
}

fun AnalyticsManagers.addToWishList(productDetails: ProductDetails) {

  val hashMap = productDetails.getShortHashMap(context)
  val json = productDetails.getShortJsonString(context)
  val properties = RudderProperty()
  properties.putValue(hashMap)
  rudderClient.track("add_to_wishlist", properties)
  val bundle = Bundle()
  bundle.putString("product", json.toString())
  firebaseAnalytics.logEvent("add_to_wishlist", bundle)
  val hashMapFlyer = productDetails.getShortHashForAppFlyMap(context)
  AppsFlyerLib.getInstance()
    .logEvent(this.context, AFInAppEventType.ADD_TO_WISH_LIST, hashMapFlyer);
  AnalyticsUtil.setEvenWithNamePropertyValue(
    persistenceManager.application,
    "AddedToWishList",
    productDetails.inventory?.sku.toString(),
    segmentNameEventName = "Product Added to Wishlist",
    propertyValue = "product_id"
  )
}

fun AnalyticsManagers.removeFromWishList(productDetails: ProductDetails) {
  val hashMap = productDetails.getShortHashMap(context)
  val json = productDetails.getShortJsonString(context)
  val properties = RudderProperty()
  properties.putValue(hashMap)
  rudderClient.track("removed_from_wishlist", properties)
  val bundle = Bundle()
  bundle.putString("product", json.toString())
  firebaseAnalytics.logEvent("removed_from_wishlist", bundle)
  AnalyticsUtil.setEvenWithNamePropertyValue(
    persistenceManager.application,
    "AddedToWishList",
    productDetails.inventory?.sku.toString(),
    segmentNameEventName = "Product Added to Wishlist",
    propertyValue = "product_id"
  )
}

/**
 * SetFaceBookUser
 */
fun setUserData(context: Context, user: User, latLong: LatLng) {
  FirebaseApp.initializeApp(context);
  val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
  val bundle = Bundle()
  firebaseAnalytics.setUserProperty("userId", user.id.toString())
  firebaseAnalytics.setUserProperty("userPhone", user.phone.toString())
  firebaseAnalytics.setUserProperty("userName", user.name.toString())
  firebaseAnalytics.setUserProperty("userLocation", "${latLong.latitude},${latLong.longitude}")
  firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
  AppsFlyerLib.getInstance().logEvent(context, AFInAppEventType.LOGIN, user.getHashMap());

}


/**
 * Set Segment User
 */
fun setUser(
  verifyOTPResponse: VerifyOTPResponse,
  rudderClient: RudderClient,
  isPhone: Boolean = true
) {
  if (DateTimeUtil.getUtcTimeToDateObject(
      verifyOTPResponse.user.createdAt
    ) != null
  ) {
    if (DateTimeUtil.getTimeDifferenceBtwTwoDates(
        DateTimeUtil.getUtcTimeToDateObject(verifyOTPResponse.user.createdAt) ?: return,
        DateTimeUtil.getCurrentDateObject()
      ) > 4
    ) {
      val properties = RudderProperty().putValue(
        "method", if (isPhone) {
          "phone"
        } else {
          "email"
        }
      )
      rudderClient.track("Sign In", properties)
//      AnalyticsUtil.segmentAnalytics?.track("Sign In", properties)
    } else {
      val properties = RudderProperty().putValue(
        "method", if (isPhone) {
          "phone"
        } else {
          "email"
        }
      )

      rudderClient.track("Sign Up", properties)
    }
  }

  Insights.shared?.userToken = verifyOTPResponse.user.id.toString()

}

fun setIdentify(user: User, rudderClient: RudderClient) {
  val device = OneSignal.getDeviceState()
  val userId = device?.userId
  var rudderTraits = RudderTraits()
  rudderTraits.putName(user.name)
  rudderTraits.putEmail(user.email)
  rudderTraits.putPhone(user.phone)
  rudderTraits.putGender(user.gender)
  rudderTraits.putId(user.id.toString())
  rudderTraits.put("dob", user.dob)
  rudderTraits.put("device_id", userId ?: user.deviceId)
  rudderClient.identify(rudderTraits)
//  val traits = Traits()
//  traits.putName(user.name)
//  traits.putEmail(user.email)
//  traits.putPhone(user.phone)
//  traits.putGender(user.gender)
//  traits.putValue("dob", user.dob)
//  traits.putValue("device_id", userId ?: user.deviceId)
//  AnalyticsUtil.segmentAnalytics?.identify(user.id.toString(), traits, null)
}