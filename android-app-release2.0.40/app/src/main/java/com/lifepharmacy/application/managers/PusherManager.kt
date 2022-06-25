package com.lifepharmacy.application.managers

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.pusher.client.PusherOptions
import com.pusher.client.Pusher
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.connection.ConnectionState
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.Channel
import com.pusher.client.connection.ConnectionEventListener
import java.lang.Exception
import javax.inject.Inject
import android.os.Bundle

import android.content.Intent
import com.google.gson.Gson
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.notifications.InAppNotificationMainModel
import com.lifepharmacy.application.model.notifications.NotificationModel
import com.lifepharmacy.application.utils.universal.ConstantsUtil


/**
 * Created by Zahid Ali
 */
class PusherManager @Inject constructor(
  var context: Context,
  var persistenceManager: PersistenceManager
) {
  var options: PusherOptions? = null
  var pusher: Pusher? = null
  var isConnected = false
  var isSubribed = false
  var userChannel: Channel? = null
  var commonChannel: Channel? = null
  var inAppChannel: Channel? = null
  var privateChannel: Channel? = null
  var userOrderChannel: Channel? = null
  fun initPusher() {
// Create a new Pusher instance
    options = PusherOptions()
    options?.setCluster(ConstantsUtil.PUSHER_CLUSTER)
    pusher = Pusher(ConstantsUtil.PUSHER_KEY, options)
    pusher?.connect()
  }


  fun startChannelListener() {
    try {
      initPusher()
      connect()
    } catch (e: Exception) {
      e.printStackTrace()
    }

  }

  fun connect() {
    var userChannel = ""
    pusher?.connect()
    if (persistenceManager.getLoggedInUser()?.id != null) {
      userChannel = persistenceManager.getLoggedInUser()?.id.toString()
    }
    try {
      subscribeUserChannel(
        "${ConstantsUtil.USER_CHANNEL_KEY}${
          userChannel
        }", ConstantsUtil.IN_APP_MESSAGE_RECEIVED_EVENT
      )
      subscribeCommonChannel()
    } catch (e: Exception) {
      e.printStackTrace()
    }

//
//    pusher?.connect(object : ConnectionEventListener {
//      override fun onConnectionStateChange(change: ConnectionStateChange) {
//
//        Log.d("ConnectedPusher", "onEvent: ")
//        isConnected = true
//        try {
//          subscribeChannel(
//            "${ConstantsUtil.STORE_CHANNEL_KEY}${
//              storeCode
//            }", ConstantsUtil.STORE_EVENT
//          )
//        }catch (e:Exception){
//          e.printStackTrace()
//        }
//
//      }
//
//      override fun onError(message: String, code: String, e: Exception) {
//        Log.d("ConnectedPusher", message)
//        isConnected = false
//      }
//    }, ConnectionState.ALL)
  }


  @SuppressLint("LogConditional")
  private fun subscribeUserChannel(channelName: String?, event: String?) {
    Log.d("ConnectionStateChange", "" + channelName)
//    if (isConnected) {
    Log.d("ConnectionStateChange", "onEvent: ")
    pusher?.unsubscribe(channelName)
    userChannel = pusher?.subscribe(channelName)
    listenUserEvent(event)
  }

  @SuppressLint("LogConditional")
  private fun subscribeCommonChannel() {
    Log.d("ConnectionStateChange", "" + ConstantsUtil.COMMON_CHANNEL)
//    if (isConnected) {
    Log.d("ConnectionStateChange", "onEvent: ")
    pusher?.unsubscribe(ConstantsUtil.COMMON_CHANNEL)
    commonChannel = pusher?.subscribe(ConstantsUtil.COMMON_CHANNEL)
    listenCommonEvent()
  }


  private fun listenCommonEvent() {
    // Bind to listen for events called "my-event" sent to "my-channel"
    Log.d("PUSHERCOMMONEVENT", "${ConstantsUtil.IN_APP_MESSAGE_RECEIVED_EVENT}" + "")
    commonChannel?.bind(ConstantsUtil.IN_APP_MESSAGE_RECEIVED_EVENT) { event1: PusherEvent ->
      val gson = Gson()
      val notificationMainModel = gson.fromJson(event1.data.toString(), InAppNotificationMainModel::class.java)
      sendBroadcastToUI(notificationMainModel)
    }
  }

  private fun listenUserEvent(event: String?) {
    // Bind to listen for events called "my-event" sent to "my-channel"
    Log.d("PUSHERUSEREVENT", event + "")
    userChannel?.bind(event) { event1: PusherEvent ->
      event1.data
      Log.d("PUSHERUSEREVENT", event1.data.toString())
      val gson = Gson()
      val notificationMainModel =
        gson.fromJson(event1.data.toString(), InAppNotificationMainModel::class.java)
      sendBroadcastToUI(notificationMainModel)
    }
  }


  fun unSubscribeChannel(channelName: String?) {
    isConnected = false
    pusher?.unsubscribe(channelName)
    pusher?.disconnect()
  }

  fun disconnectSocket() {
    isConnected = false
    pusher?.disconnect()
  }

  private fun sendBroadcastToUI(notificationGeneralModel: InAppNotificationMainModel?) {
    val i1 = Intent(ConstantsUtil.IN_APP_POPUP)
    i1.setPackage(context.packageName)
    val bundle = Bundle()
    bundle.putParcelable("payload", notificationGeneralModel)
    i1.putExtras(bundle)
    context.sendBroadcast(i1)
  }

}