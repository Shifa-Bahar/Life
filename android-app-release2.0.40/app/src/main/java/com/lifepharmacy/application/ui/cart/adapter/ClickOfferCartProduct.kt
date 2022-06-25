package com.lifepharmacy.application.ui.cart.adapter

import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.cart.OffersCartModel
import com.lifepharmacy.application.model.product.ProductDetails

/**
 * Created by Zahid Ali
 */
interface ClickOfferCartProduct {
  fun onClickPlus(productDetails: ProductDetails, position: Int)
  fun onClickMinus(productDetails: ProductDetails, position: Int)
  fun onClickRemove(productDetails: ProductDetails, position: Int)
  fun onClickChecked(offerModel: OffersCartModel, position: Int)
  fun onClickProduct(productDetails: ProductDetails, position: Int)
  fun onClickAddMore(offerModel: OffersCartModel, position: Int)
}