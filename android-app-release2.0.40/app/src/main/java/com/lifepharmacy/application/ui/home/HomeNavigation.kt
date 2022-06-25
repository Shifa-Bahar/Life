package com.lifepharmacy.application.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.lifepharmacy.application.model.notifications.InAppNotificationMainModel
import com.lifepharmacy.application.model.notifications.NotificationAction
import com.lifepharmacy.application.model.notifications.NotificationPayLoad
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLDecoder

class HomeNavigation(var context: Context) {
  fun triggerNavigation(key: String, value: String, heading: String) {
    when {
      key == "order" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.ORDER
          )
        )
      }
      key == "sub_order" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.SUB_ORDER
          )
        )
      }
      key == "rate" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.RATE
          )
        )
      }
      NotificationPayLoad.stringProductListing.contains(key) -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = value,
            value = key,
            action = NotificationAction.PRODUCT_LISTING
          )
        )
      }
      key == "product" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.PRODUCT
          )
        )
      }
      key == "page" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.LANDING_PAGE
          )
        )
      }
      key == "page" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.LOGIN
          )
        )
      }
      key == "web" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.WEB
          )
        )
      }
      key == "screen" -> {
        moveToScreen(key, value, heading)
      }
    }
  }

  private fun moveToScreen(key: String, value: String, heading: String) {
    when (value) {
      "vouchers" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.VOUCHER
          )
        )
      }
      "prescription_request" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.PRESCRIPTION_REQUEST
          )
        )
      }
      "orders" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.ORDERS
          )
        )
      }
      "prescription_requests" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.PRESCRIPTION_REQUESTS
          )
        )
      }
      "wallet" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.WALLET
          )
        )
      }
      "account" -> {
        sendBroadcastToUI(
          NotificationPayLoad(
            title = heading,
            key = key,
            value = value,
            action = NotificationAction.ACCOUNT
          )
        )
//        ConstantsUtil.notificationPayLoad.value =
      }
    }
  }

  private fun sendBroadcastToUI(notificationGeneralModel: NotificationPayLoad?) {
    val i1 = Intent(ConstantsUtil.HOME_NAV)
    i1.setPackage(context.packageName)
    val bundle = Bundle()
    bundle.putParcelable("payload", notificationGeneralModel)
    i1.putExtras(bundle)
    context.sendBroadcast(i1)
  }

  private fun sendBroadcastToFragmentUI(notificationGeneralModel: NotificationPayLoad?) {
    val i1 = Intent(ConstantsUtil.HOME_FG_NAV)
    i1.setPackage(context.packageName)
    val bundle = Bundle()
    bundle.putParcelable("payload", notificationGeneralModel)
    i1.putExtras(bundle)
    GlobalScope.launch {
      delay(300)
      context.sendBroadcast(i1)
    }

  }

  fun decodeUrl(string: String) {
    try {
      val uri: Uri = Uri.parse(string)
      val path = uri.path
      val pathSagments = uri.pathSegments
      val args = uri.queryParameterNames
      var key: String? = ""
      var value: String? = ""
      if (!pathSagments.isNullOrEmpty() && pathSagments.size > 1) {
        key = pathSagments.first()
        value = pathSagments.get(1)
        triggerHomFGNavigation(key, value)
      } else {
        if (!args.isNullOrEmpty()) {
          key = args.first()
          value = uri.getQueryParameter(key)
          triggerHomFGNavigation(key, value)
        } else {
          key = path?.replace("/", "")
          triggerHomFGNavigation(key ?: "", value)
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

  }

  fun triggerHomFGNavigation(key: String, value: String?) {
    when {
      key == "product" -> {
        sendBroadcastToFragmentUI(
          NotificationPayLoad(
            title = "",
            key = key,
            value = value ?: "",
            action = NotificationAction.PRODUCT
          )
        )
      }
      NotificationPayLoad.stringProductListing.contains(key) -> {
        sendBroadcastToFragmentUI(
          NotificationPayLoad(
            title = "",
            key = value ?: "",
            value = key,
            action = NotificationAction.PRODUCT_LISTING
          )
        )
      }
      key == "login" -> {
        sendBroadcastToFragmentUI(
          NotificationPayLoad(
            title = "",
            key = value ?: "",
            value = key,
            action = NotificationAction.LOGIN
          )
        )
      }
    }

  }

}