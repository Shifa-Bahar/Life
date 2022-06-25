package com.lifepharmacy.application.utils

import com.lifepharmacy.application.model.notifications.NotificationAction
import com.lifepharmacy.application.model.notifications.NotificationPayLoad
import com.lifepharmacy.application.utils.universal.ConstantsUtil

object PushNavigation {

  fun getNotificationPayLoadFromData(
    key: String,
    value: String,
    heading: String
  ): NotificationPayLoad? {
    return when {
      key == "order" -> {
        NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.ORDER
        )
      }
      key == "sub_order" -> {
        NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.SUB_ORDER
        )
      }
      key == "rate_order" -> {
        NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.RATE
        )
      }
      NotificationPayLoad.stringProductListing.contains(key) -> {
        NotificationPayLoad(
          title = heading,
          key = value,
          value = key,
          action = NotificationAction.PRODUCT_LISTING
        )
      }
      key == "product" -> {
        NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.PRODUCT
        )
      }
      key == "page" -> {
        NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.LANDING_PAGE
        )
      }
      key == "web" -> {
        NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.WEB
        )
      }
      key == "link" -> {
        NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.LINK
        )
      }
      key == "screen" -> {
        when (value) {
          "vouchers" -> {
            NotificationPayLoad(
              title = heading,
              key = key,
              value = value,
              action = NotificationAction.VOUCHER
            )
          }
          "prescription_request" -> {
            NotificationPayLoad(
              title = heading,
              key = key,
              value = value,
              action = NotificationAction.PRESCRIPTION_REQUEST
            )
          }
          "orders" -> {
            NotificationPayLoad(
              title = heading,
              key = key,
              value = value,
              action = NotificationAction.ORDERS
            )
          }
          "prescription_requests" -> {
            NotificationPayLoad(
              title = heading,
              key = key,
              value = value,
              action = NotificationAction.PRESCRIPTION_REQUESTS
            )
          }
          "cart" -> {
            NotificationPayLoad(
              title = heading,
              key = key,
              value = value,
              action = NotificationAction.CART
            )
          }
          "wallet" -> {
            NotificationPayLoad(
              title = heading,
              key = key,
              value = value,
              action = NotificationAction.WALLET
            )
          }
          "account" -> {
            NotificationPayLoad(
              title = heading,
              key = key,
              value = value,
              action = NotificationAction.ACCOUNT
            )
          }
          else -> {
            null
          }
        }
      }
      else -> {
        null
      }
    }
  }

  fun setNavigation(key: String, value: String, heading: String) {
    when {
      key == "order" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.ORDER
        )
      }
      key == "sub_order" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.SUB_ORDER
        )
      }
      key == "rate_order" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.RATE
        )
      }
      NotificationPayLoad.stringProductListing.contains(key) -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = value,
          value = key,
          action = NotificationAction.PRODUCT_LISTING
        )
      }
      key == "product" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.PRODUCT
        )
      }
      key == "page" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.LANDING_PAGE
        )
      }
      key == "web" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.WEB
        )
      }
      key == "link" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.LINK
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
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.VOUCHER
        )
      }
      "prescription_request" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.PRESCRIPTION_REQUEST
        )
      }
      "orders" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.ORDERS
        )
      }
      "prescription_requests" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.PRESCRIPTION_REQUESTS
        )
      }
      "cart" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.CART
        )
      }
      "wallet" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.WALLET
        )
      }
      "account" -> {
        ConstantsUtil.notificationPayLoad.value = NotificationPayLoad(
          title = heading,
          key = key,
          value = value,
          action = NotificationAction.ACCOUNT
        )
      }
    }
  }
}