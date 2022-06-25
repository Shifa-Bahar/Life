package com.lifepharmacy.application.ui.cart.adapter

import com.lifepharmacy.application.model.LatestOfferModel
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.cart.OffersCartModel
import com.lifepharmacy.application.model.config.DeliverySlot
import com.lifepharmacy.application.model.product.ProductDetails

/**
 * Created by Zahid Ali
 */
interface ClickDeliverySlot {
  fun onClickSlot(slot: DeliverySlot, position: Int)
}