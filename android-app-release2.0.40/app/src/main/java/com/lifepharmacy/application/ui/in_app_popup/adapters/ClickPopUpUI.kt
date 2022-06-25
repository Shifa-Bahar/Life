package com.lifepharmacy.application.ui.in_app_popup.adapters

import com.lifepharmacy.application.model.notifications.InAppNotificationDataModel
import com.lifepharmacy.application.model.payment.CardMainModel

/**
 * Created by Zahid Ali
 */
interface ClickPopUpUI {
  fun onClickButton(item: InAppNotificationDataModel)
}