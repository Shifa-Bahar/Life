package com.lifepharmacy.application.ui.orders.adapters

import com.lifepharmacy.application.model.orders.SubOrderItem

/**
 * Created by Zahid Ali
 */
interface ClickShipmentItem {
  fun onClickShipmentRating(ratingValue: Float, subOrderId: Int, shipmentId: Int)
  fun onClickInvoice(subOrderId: Int)
  fun onClickViewStatusDetails(subOrderItem: SubOrderItem)
}