package com.lifepharmacy.application.ui.orders.adapters

import com.lifepharmacy.application.model.orders.OrderResponseModel

/**
 * Created by Zahid Ali
 */
interface ClickOrder {
  fun onClickOrder(orderModel: OrderResponseModel)
  fun onClickViewOrder(orderModel: OrderResponseModel)
}