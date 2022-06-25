package com.lifepharmacy.application.ui.returned.adapters

import com.lifepharmacy.application.model.orders.ReturnOrderModel

/**
 * Created by Zahid Ali
 */
interface ClickReturnOrderItem {
  fun onClickReturnOrder(returnOrderItem: ReturnOrderModel)
}