package com.lifepharmacy.application.ui.dashboard.adapter

import com.lifepharmacy.application.model.product.ProductDetails

/**
 * Created by Zahid Ali
 */
interface ClickHomeProduct {
  fun onProductClicked(productDetails: ProductDetails, position: Int)
  fun onClickAddProduct(productDetails: ProductDetails, position: Int)
  fun onClickMinus(productDetails: ProductDetails, position: Int)
  fun onClickPlus(productDetails: ProductDetails, position: Int)
  fun onClickWishList(productDetails: ProductDetails, position: Int)
  fun onClickNotify(productDetails: ProductDetails, position: Int)
  fun onClickOrderOutOfStock(productDetails: ProductDetails, position: Int)
}