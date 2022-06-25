package com.lifepharmacy.application.managers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lifepharmacy.application.R
import com.lifepharmacy.application.model.notifications.NotificationPayLoad
import com.lifepharmacy.application.ui.dashboard.DashboardWithNativeBottomActivity
import com.livechatinc.inappchat.models.NewMessageModel

/**
 * Created by Zahid Ali
 */
class NotificationHelpManager {


  companion object {

    val NOTIFICATION_ID = 101
    val CHANNEL_ID = "personal_notification"
    fun createNotification(
      context: Context,
      title: String,
      description: String,
      notificationModel: NotificationPayLoad? = null
    ) {
      if (notificationModel == null) {
        notificationWithOutAction(context, title, description)
      } else {
        val intent = Intent(context, DashboardWithNativeBottomActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val bundle = Bundle()
        bundle.putParcelable("payload", notificationModel)
        intent.putExtras(bundle)
        val pendingIntent =
          PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        notificationWithAction(context, title, description, pendingIntent)
      }

    }

    fun notificationSimpleText(context: Context, message: NewMessageModel) {
      createNotificationChannel(context)
      val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_life_logo)
        .setContentTitle("Live Chat")
        .setContentText(message.author.name)
        .setStyle(
          NotificationCompat.BigTextStyle()
            .bigText(message.text)
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        .setShowWhen(true)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) //Important for heads-up notification
        .setAutoCancel(true)
      with(NotificationManagerCompat.from(context)) {
        notify(NOTIFICATION_ID, builder.build())
      }
    }

    private fun createNotificationChannel(context: Context) {
      // Create the NotificationChannel, but only on API 26+ because
      // the NotificationChannel class is new and not in the support library
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Personal Notifications"
        val descriptionText = "Include ll personal Notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
          description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
      }
    }

    fun notificationWithAction(
      context: Context,
      title: String,
      description: String,
      pendingIntent: PendingIntent
    ) {
      createNotificationChannel(context)
      val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_life_logo)
        .setContentTitle(title)
        .setContentText(description)
        .setPriority(NotificationCompat.FLAG_INSISTENT)
        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        .setShowWhen(true)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) //Important for heads-up notification
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
      with(NotificationManagerCompat.from(context)) {
        val notification = builder.build()
//        notification.flags = Notification.FLAG_INSISTENT;
        // notificationId is a unique int for each notification that you must define
        notify(NOTIFICATION_ID, notification)
      }
    }

    fun notificationWithOutAction(
      context: Context,
      title: String,
      description: String,
    ) {
      createNotificationChannel(context)
      val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_life_logo)
        .setContentTitle(title)
        .setContentText(description)
        .setPriority(NotificationCompat.FLAG_INSISTENT)
        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        .setShowWhen(true)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) //Important for heads-up notification
        .setAutoCancel(true)
      with(NotificationManagerCompat.from(context)) {
        val notification = builder.build()
//        notification.flags = Notification.FLAG_INSISTENT;
        // notificationId is a unique int for each notification that you must define
        notify(NOTIFICATION_ID, notification)
      }
    }
  }


//  companion object {
//
//    val NOTIFICATION_ID = 101
//    val CHANNEL_ID = "personal_notification"
//
////    fun notificationWithAction(
////      context: Context,
////      message: NewMessageModel,
////      pendingIntent: PendingIntent
////    ) {
////      createNotificationChannel(context)
////      var builder = NotificationCompat.Builder(context, CHANNEL_ID)
////        .setSmallIcon(R.drawable.life_circle_icon)
////        .setContentTitle("Live Chat")
////        .setContentText(message.author.name)
////        .setStyle(
////          NotificationCompat.BigTextStyle()
////            .bigText(message.text)
////        )
////        .setPriority(NotificationCompat.PRIORITY_HIGH)
////        .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
////        .setShowWhen(true)
////        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) //Important for heads-up notification
////        // Set the intent that will fire when the user taps the notification
////        .setContentIntent(pendingIntent)
////        .setAutoCancel(true)
//////            val notification = builder.build()
//////        notificationManager!!.notify(NOTIFICATION_ID, notification)/
////      with(NotificationManagerCompat.from(context)) {
////        // notificationId is a unique int for each notification that you must define
////        notify(NOTIFICATION_ID, builder.build())
////      }
////    }
//
//    fun notificationSimpleText(context: Context, message: NewMessageModel) {
//      createNotificationChannel(context)
//      val builder = NotificationCompat.Builder(context, CHANNEL_ID)
//        .setSmallIcon(R.drawable.life_circle_icon)
//        .setContentTitle("Live Chat")
//        .setContentText(message.author.name)
//        .setStyle(
//          NotificationCompat.BigTextStyle()
//            .bigText(message.text)
//        )
//        .setPriority(NotificationCompat.PRIORITY_HIGH)
//        .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
//        .setShowWhen(true)
//        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) //Important for heads-up notification
//        .setAutoCancel(true)
//      with(NotificationManagerCompat.from(context)) {
//        notify(NOTIFICATION_ID, builder.build())
//      }
//    }
//
//    private fun createNotificationChannel(context: Context) {
//      // Create the NotificationChannel, but only on API 26+ because
//      // the NotificationChannel class is new and not in the support library
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val name = "Personal Notifications"
//        val descriptionText = "Include ll personal Notifications"
//        val importance = NotificationManager.IMPORTANCE_HIGH
//        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//          description = descriptionText
//        }
//        // Register the channel with the system
//        val notificationManager: NotificationManager =
//          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//      }
//    }
//
////        fun notificationWithAction(context: Context,notificationModel: NotificationModel, pendingIntent: PendingIntent){
////            createNotificationChannel(context)
////            var builder = NotificationCompat.Builder(context, CHANNEL_ID)
////                .setSmallIcon(R.drawable.app_logo_round)
////                .setContentTitle(context.getString(R.string.app_name))
////                .setContentText(notificationModel.title)
////                .setStyle(NotificationCompat.BigTextStyle()
////                    .bigText(notificationModel.body))
//////                .setContentText(notificationModel.body)
////                .setPriority(NotificationCompat.PRIORITY_HIGH)
////                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
////                .setShowWhen(true)
////                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) //Important for heads-up notification
////                // Set the intent that will fire when the user taps the notification
////                .setContentIntent(pendingIntent)
////                .setAutoCancel(true)
//////            val notification = builder.build()
//////        notificationManager!!.notify(NOTIFICATION_ID, notification)/
////            with(NotificationManagerCompat.from(context)) {
////                // notificationId is a unique int for each notification that you must define
////                notify(NOTIFICATION_ID, builder.build())
////            }
////        }
//  }
}