package com.lifepharmacy.application.ui.cart.adapter

import com.lifepharmacy.application.model.LatestOfferModel
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.cart.OffersCartModel
import com.lifepharmacy.application.model.product.ProductDetails

/**
 * Created by Zahid Ali
 */
interface ClickCartProduct {
  fun onClickPlus(productDetails: ProductDetails, position: Int)
  fun onClickMinus(productDetails: ProductDetails, position: Int)
  fun onClickRemove(productDetails: ProductDetails, position: Int)
  fun onClickProduct(productDetails: ProductDetails, position: Int)
  fun onClickChecked(cartModel: CartModel, position: Int)
  fun onClickEmptyClicked(offerModel: OffersCartModel, position: Int)
  fun onClickNotify(productDetails: ProductDetails, position: Int)
}