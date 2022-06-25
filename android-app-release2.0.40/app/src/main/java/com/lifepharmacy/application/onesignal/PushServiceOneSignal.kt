package com.lifepharmacy.application.onesignal

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.lifepharmacy.application.R
import com.lifepharmacy.application.utils.universal.Logger
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal


/**
 * Created by Zahid Ali
 */
class PushServiceOneSignal : OneSignal.OSRemoteNotificationReceivedHandler {
  override fun remoteNotificationReceived(
    context: Context?,
    notificationReceivedEvent: OSNotificationReceivedEvent?
  ) {
    val notification = notificationReceivedEvent!!.notification
    Logger.d("NotificationComingFromServer", notification.additionalData.toString())

    // Example of modifying the notification's accent color

    // Example of modifying the notification's accent color
    val mutableNotification = notification.mutableCopy()
    context?.let {
      mutableNotification.setExtender { builder: NotificationCompat.Builder ->
        builder.setColor(
          ContextCompat.getColor(it, R.color.accent_blue)
        )
      }
    }


    // If complete isn't call within a time period of 25 seconds, OneSignal internal logic will show the original notification
    // If null is passed to complete

    // If complete isn't call within a time period of 25 seconds, OneSignal internal logic will show the original notification
    // If null is passed to complete
    notificationReceivedEvent.complete(mutableNotification)
  }
}