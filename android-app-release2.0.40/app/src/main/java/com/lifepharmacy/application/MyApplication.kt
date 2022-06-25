package com.lifepharmacy.application

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.bugsnag.android.Bugsnag
import com.google.firebase.FirebaseApp
import com.lifepharmacy.application.managers.PusherBeamManager
import com.lifepharmacy.application.model.notifications.NotificationAction
import com.lifepharmacy.application.model.notifications.NotificationPayLoad
import com.lifepharmacy.application.ui.cart.fragmets.AnimationOfferCompleteCouponDialog.Companion.TAG
import com.lifepharmacy.application.utils.AlgoliaInsightsUtil
import com.lifepharmacy.application.utils.PushNavigation
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Logger
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal
import com.pusher.pushnotifications.PushNotifications
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


//import zendesk.answerbot.AnswerBot
//import zendesk.chat.Chat
//import zendesk.core.Zendesk
//import zendesk.support.Support

@HiltAndroidApp
class MyApplication : Application() {
  @Inject
  lateinit var pusherBeamManager: PusherBeamManager

  override fun onCreate() {
    super.onCreate()
    FirebaseApp.initializeApp(applicationContext)
    Bugsnag.start(this)
//    AnalyticsUtil.setSingleLog(applicationContext, "AppLaunched")
//    AnalyticsUtil.setUpAnalytics(context = applicationContext)
    AlgoliaInsightsUtil.setUpInsights(context = applicationContext)
    AppsFlyerLib.getInstance().init(ConstantsUtil.APP_FLYER_KEY, null, this)
    AppsFlyerLib.getInstance().start(this)
    AppsFlyerLib.getInstance().setDebugLog(true)

//    pusherBeamManager = PusherBeamManager(applicationContext)
    pusherBeamManager.initBeam()
//    //ZenDeskInit
//    Zendesk.INSTANCE.init(
//      applicationContext,
//      Constants.ZEN_URL,
//      Constants.ZEN_DESK_APP_ID,
//      Constants.ZEN_DESK_CLIENT_ID
//    )
//    Support.INSTANCE.init(Zendesk.INSTANCE)
//    AnswerBot.INSTANCE.init(Zendesk.INSTANCE, Support.INSTANCE);
//    Chat.INSTANCE.init(applicationContext, Constants.ZEN_DESK_CHAT_ACCOUNT_KEY)

    // Logging set to help debug issues, remove before releasing your app.
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
//
//        // OneSignal Initialization
    OneSignal.initWithContext(this)
    OneSignal.setAppId(ConstantsUtil.ONE_SIGNAL_KEY)
    val device = OneSignal.getDeviceState()

    val email = (device ?: return).emailAddress
    val emailId = device.emailUserId
    val pushToken = device.pushToken
    val userId = device.userId

    val enabled = device!!.areNotificationsEnabled()
    val subscribed = device!!.isSubscribed
    if (userId != null) {
      savePlayerId(userId)
    }
    OneSignal.addSubscriptionObserver {
      val playerID = it?.to?.userId
      if (playerID != null) {
        savePlayerId(playerID)
      }
    }
    OneSignal.setNotificationOpenedHandler(object :
      OneSignal.OSNotificationWillShowInForegroundHandler,
      OneSignal.OSNotificationOpenedHandler {
      override fun notificationWillShowInForeground(notificationReceivedEvent: OSNotificationReceivedEvent?) {
//        val data = notificationReceivedEvent!!.notification.additionalData

        try {
          notificationReceivedEvent?.complete(notificationReceivedEvent.notification);
        } catch (e: Exception) {
          e.printStackTrace()
        }
//        if (/* some condition */) {
//          // Complete with a notification means it will show

//        } else {
//          // Complete with null means don't show a notification.
//          notificationReceivedEvent.complete(null);
//        }
      }

      override fun notificationOpened(result: OSNotificationOpenedResult?) {
        try {
          val data = result?.notification?.additionalData
          var key = ""
          var value = ""
          var heading = ""
          if (data?.has("key") == true) {
            key = data.getString("key") ?: ""
          }
          if (data?.has("value") == true) {
            value = data.getString("value") ?: ""
          }
          if (data?.has("heading") == true) {
            heading = data.getString("heading") ?: ""
          }
          if (data?.has("campaignId") == true) {
            val preference: SharedPreferences =
              applicationContext.getSharedPreferences(
                applicationContext.packageName,
                Context.MODE_PRIVATE
              )
            preference.edit()
              .putString(ConstantsUtil.SH_COMPAIN_ID, data.getString("campaignId") ?: "").apply()
          }
          PushNavigation.setNavigation(key = key, value = value, heading = heading)

        } catch (e: Exception) {
          e.printStackTrace()
        }

      }
    })


//    OneSignal.setNotificationOpenedHandler(PushServiceOneSignal())
  }


  fun savePlayerId(player: String) {
    val preference: SharedPreferences =
      applicationContext.getSharedPreferences(
        applicationContext.packageName,
        Context.MODE_PRIVATE
      )
    if (player != null) {
      Logger.d("PlayerID", player)
    }
    preference.edit().putString(ConstantsUtil.SH_FCM_TOKEN, player).commit()
  }

//  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//  private fun onAppKilled() {
//  }
}