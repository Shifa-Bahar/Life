package com.lifepharmacy.application.services

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lifepharmacy.application.managers.NotificationHelpManager
import com.lifepharmacy.application.utils.PushNavigation
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Logger
import com.pusher.pushnotifications.fcm.MessagingService
import org.json.JSONObject

/**
 *
 */
class MyCustomMessagingService : MessagingService() {

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    try {
      Logger.d("NotificationReceived", remoteMessage.data["data"].toString())
      val title: String? = remoteMessage.data["title"]
      val body: String? = remoteMessage.data["message"]
      val data: String? = remoteMessage.data["data"];
      val push = JSONObject(data)
      var key = ""
      var value = ""
      var heading = ""
      if (push.has("key")) {
        key = push.getString("key") ?: ""
      }
      if (push.has("value")) {
        value = push.getString("value") ?: ""
      }
      if (push.has("heading")) {
        heading = push.getString("heading") ?: ""
      }
      if (push.has("campaignId")) {
        val preference: SharedPreferences =
          applicationContext.getSharedPreferences(
            applicationContext.packageName,
            Context.MODE_PRIVATE
          )
        preference.edit()
          .putString(ConstantsUtil.SH_COMPAIN_ID, push.getString("campaignId") ?: "").apply()
      }
      NotificationHelpManager.createNotification(
        context = this,
        title = title ?: "",
        description = body ?: "",
        notificationModel = PushNavigation.getNotificationPayLoadFromData(key, value, heading)
      )

    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}