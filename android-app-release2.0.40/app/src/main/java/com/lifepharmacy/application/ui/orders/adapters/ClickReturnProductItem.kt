package com.lifepharmacy.application.ui.orders.adapters

import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.orders.SubOrderItem
import com.lifepharmacy.application.model.orders.SubOrderProductItem

/**
 * Created by Zahid Ali
 */
interface ClickReturnProductItem {
  fun onClickPlus(orderModel: SubOrderProductItem, qty: Int)
  fun onClickMinus(orderModel: SubOrderProductItem, qty: Int)
}