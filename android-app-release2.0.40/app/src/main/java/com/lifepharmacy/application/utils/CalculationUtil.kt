package com.lifepharmacy.application.utils

import android.content.Context
import android.util.Log
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.pcr.pcrlist.Products
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import kotlin.time.times

/**
 * Created by Zahid Ali
 */
object CalculationUtil {
  fun getUnitPrice(product: ProductDetails, context: Context): Double {
    //    val regularPrice = PricesUtil.getRelativePrice(product.prices,context).price.regularPrice?:0.0
    //    return regularPrice.minus(getVATFromRegularPercentage(product,context))
    return PricesUtil.getRelativePrice(product.prices, context).price.regularPrice ?: 0.0
  }

  fun getUnitPcrPrice(product: Products, context: Context): Double {
    //    val regularPrice = PricesUtil.getRelativePrice(product.prices,context).price.regularPrice?:0.0
    //    return regularPrice.minus(getVATFromRegularPercentage(product,context))
    return PricesUtil.getRelativePcrPrice(product.prices, context).price.regularPrice ?: 0.0
  }

  fun getVATFromRegularPercentage(product: ProductDetails, context: Context): Double {
    val regularPrice =
      PricesUtil.getRelativePrice(product.prices, context).price.regularPrice ?: 0.0
    val a = 100
    //    val a = 100.plus(product.vatPercentage ?: 0.0)
    val b = product.vatPercentage?.div(a)
    return regularPrice.times(b ?: 0.0)
  }

  fun getVATFromUnitPriceWithGivenVATPercentage(
    unitPrice: Double,
    vatPer: Double,
  ): Double {
    val a = 100
    //    val a = 100.plus(product.vatPercentage ?: 0.0)
    val b = vatPer?.div(a)
    return unitPrice.times(b ?: 0.0)
  }

  fun getVATFromDiscountedAmount(
    product: ProductDetails,
    discountPercentage: Double,
    context: Context,
  ): Double {
    val amount = getAmountAfterDiscount(product, discountPercentage, context)
    val a = product.vatPercentage?.div(100)
    val vatOfDiscountAmount = amount.times(a ?: 1.0)
    return vatOfDiscountAmount
  }

  fun getGrossLineTotal(cartModel: CartModel, context: Context): Double {
    return cartModel.qty.times(getUnitPrice(cartModel.productDetails, context))
  }

  fun getGrossLineWithServerUnitPriceTotal(cartModel: CartModel): Double {
    return cartModel.qty.times(cartModel.productDetails.unitPrice ?: 1.0)
  }

  fun getDiscountAmount(
    product: ProductDetails,
    discountPercentage: Double,
    context: Context
  ): Double {
    val a = discountPercentage / 100
    val unitPrice = getUnitPrice(product, context)
    return unitPrice.times(a)
  }

  fun getAmountAfterDiscount(
    product: ProductDetails,
    discountPercentage: Double,
    context: Context
  ): Double {
    return getUnitPrice(product, context) - getDiscountAmount(product, discountPercentage, context)
  }

  fun getLineTotal(cartModel: CartModel, context: Context): Double {
    return cartModel.cartGrossTotal?.minus(cartModel.discountedAmount ?: 0.0) ?: 0.0
  }

  fun getVATOnLineTotal(cartModel: CartModel, context: Context): Double {
    val lineTotal = getLineTotal(cartModel, context)
    return lineTotal?.times(cartModel.productDetails.vatPercentage ?: 0.0).div(100) ?: 0.0
  }

  fun getVATOnGivenOutStockVatLineTotal(cartModel: CartModel, context: Context): Double {
    val lineTotal = getLineTotal(cartModel, context)
    return lineTotal?.times(cartModel.productDetails.vatPercentageOutOfStock ?: 0.0).div(100) ?: 0.0
  }

  fun getNetLineTotal(cartModel: CartModel, context: Context): Double {
    return getLineTotal(cartModel, context).plus(getVATOnLineTotal(cartModel, context) ?: 0.0)
      ?: 0.0
  }

  fun getNetLineOutOfStockTotal(cartModel: CartModel, context: Context): Double {
    return getLineTotal(cartModel, context).plus(
      getVATOnGivenOutStockVatLineTotal(
        cartModel,
        context
      ) ?: 0.0
    )
      ?: 0.0
  }

  fun getPriceAfterVATOGive(price: Double, product: ProductDetails): Double {
    val a = product.vatPercentage?.div(100)
    val vatOfDiscountAmount = price.times(a ?: 1.0)
    val amountAfterVat = price.plus(vatOfDiscountAmount)
    return amountAfterVat
  }
  fun getPcrPriceAfterVATOGive(price: Double, product: Products): Double {
    var a = product.vat_percentage?.div(100)
    var vatOfDiscountAmount = price.times(a ?: 1.0)
    val amountAfterVat = price.plus(vatOfDiscountAmount)
    return amountAfterVat
  }

}

