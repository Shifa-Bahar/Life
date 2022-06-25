package com.lifepharmacy.application.ui.rating.adapters

import com.lifepharmacy.application.model.orders.SubOrderProductItem

/**
 * Created by Zahid Ali
 */
interface ClickProductRating {
  fun onClickProductRating(productId: String, ratingValue: Float, subOrderId: String)
  fun onClickWriteReview(
    position: Int,
    subOrderProductItem: SubOrderProductItem,
    productId: String,
    ratingValue: Float?,
    subOrderId: String
  )
}