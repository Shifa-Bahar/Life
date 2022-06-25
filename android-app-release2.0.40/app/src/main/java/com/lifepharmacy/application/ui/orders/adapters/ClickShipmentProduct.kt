package com.lifepharmacy.application.ui.orders.adapters

import com.lifepharmacy.application.model.orders.SubOrderProductItem

/**
 * Created by Zahid Ali
 */
interface ClickShipmentProduct {
  fun onClickProductRating(productId: String, ratingValue: Float, subOrderId: String,subOrderProductItem: SubOrderProductItem)
}