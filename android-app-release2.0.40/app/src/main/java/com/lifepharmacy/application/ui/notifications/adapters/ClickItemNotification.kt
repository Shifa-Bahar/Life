
package com.lifepharmacy.application.ui.notifications.adapters

import com.lifepharmacy.application.model.notifications.NotificationModel


/**
 * Created by Zahid Ali
 */
interface ClickItemNotification {
    fun onClickNotification(notification:NotificationModel)
}