package com.lifepharmacy.application.ui.orders.adapters

import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.orders.SubOrderItem
import com.lifepharmacy.application.model.orders.SubOrderProductItem

/**
 * Created by Zahid Ali
 */
interface ClickOrderShipmentItem {
  fun onClickSubOrderViewDetail(orderModel: SubOrderItem, orderId: String)
  fun onClickRating(orderModel: SubOrderItem, orderId: String, rating: Float)
}