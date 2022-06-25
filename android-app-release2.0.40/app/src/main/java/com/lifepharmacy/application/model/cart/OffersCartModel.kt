package com.lifepharmacy.application.model.cart

import android.app.Activity
import com.google.gson.Gson
import com.lifepharmacy.application.enums.ShipmentType
import com.lifepharmacy.application.model.product.Offers
import com.lifepharmacy.application.model.product.OffersType
import com.lifepharmacy.application.utils.CalculationUtil
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDouble
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDoubleByDefault1
import com.lifepharmacy.application.utils.universal.Logger
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Created by Zahid Ali
 */
class OffersCartModel(
  var groupAmountWithoutDiscount: Double? = 0.0,
  var groupAmountWithDiscount: Double? = 0.0,
  var groupVAT: Double? = 0.0,
  var groupDiscountedAmount: Double? = 0.0,
  var offers: Offers? = Offers(),
  var products: ArrayList<CartModel> = ArrayList(),
  var productsWithQTY: ArrayList<CartModel> = ArrayList(),
  var freeIndexes: ArrayList<Int> = ArrayList(),
  var isInstant: Boolean = true,
  var isValid: Boolean = true
) {
  fun getTypeEnum(): OffersType {
    return when (offers?.type) {
      "bxgy" -> {
        OffersType.BXGY
      }
      "flat_percentage_discount" -> {
        OffersType.FLAT
      }
      "free_gift" -> {
        OffersType.FREE_GIFT
      }
      else -> {
        OffersType.NON
      }
    }
  }

  fun getSlotsCount(): Int {
    val validCount = (offers?.xValue ?: 0) + (offers?.yValue ?: 0)
    return if (validCount >= products.size) {
      validCount
    } else {
      val decimal =
        (products.size.intToNullSafeDoubleByDefault1() / validCount.intToNullSafeDoubleByDefault1())
      val df = DecimalFormat("#")
      df.roundingMode = RoundingMode.UP
      val multiplier = df.format(decimal)
      (validCount) * multiplier.toInt()

    }
  }

  fun savedAmount(): Double {
    return groupDiscountedAmount ?: 0.0
  }

  fun calculate(context: Activity) {
    products.sortByDescending {
      it.productDetails.getRelativePrice(context)?.price?.regularPrice
    }
    groupVAT = 0.0
    groupAmountWithoutDiscount = 0.0
    freeIndexes.clear()
    when (getTypeEnum()) {
      OffersType.BXGY, OffersType.FREE_GIFT -> {
        isValid = getSlotsCount() == products.size
        calculationForFreeGiftAndBXGY(products, context)

      }
      OffersType.NON, OffersType.FLAT -> {
        val item = products.first()
        calculationForFlatAndNonOfferProduct(item, context)
      }
    }
    createSingleProductWithQTYArray()
  }

  private fun calculationForFlatAndNonOfferProduct(item: CartModel, context: Activity) {
    item.cartVAT = CalculationUtil.getVATFromDiscountedAmount(
      item.productDetails,
      item.productDetails.getDiscountPercentageValue(),
      context
    ).times(item.qty)
    groupVAT = item.cartVAT
    item.discountedAmount = CalculationUtil.getDiscountAmount(
      item.productDetails,
      item.productDetails.getDiscountPercentageValue(),
      context
    ).times(item.qty)
    item.cartGrossTotal = CalculationUtil.getGrossLineTotal(item, context)
    groupDiscountedAmount = item.discountedAmount
    groupAmountWithoutDiscount =
      CalculationUtil.getUnitPrice(item.productDetails, context).times(item.qty)
    Logger.d(
      "groupAmountWithoutDiscount",
      "${CalculationUtil.getUnitPrice(item.productDetails, context)}"
    )
    Logger.d(
      "groupAmountWithDiscount",
      "${
        CalculationUtil.getDiscountAmount(
          item.productDetails,
          item.productDetails.getDiscountPercentageValue(),
          context
        ).times(item.qty)
      }"
    )
    Logger.d(
      "groupAmountWithoutDiscount1",
      "${
        CalculationUtil.getDiscountAmount(
          item.productDetails,
          item.productDetails.getDiscountPercentageValue(),
          context
        )
      }"
    )
    groupAmountWithDiscount = CalculationUtil.getAmountAfterDiscount(
      item.productDetails,
      item.productDetails.getDiscountPercentageValue(),
      context
    ).times(item.qty)
  }

  private fun calculationForFreeGiftAndBXGY(list: ArrayList<CartModel>, context: Activity) {
    var totalAmountDiscount = 0.0
    var totalAmountWithDiscount = 0.0
    var discount = 0.0
    var singleItemVat = 0.0
    var vatOldSub = 0.0
    var indexX = 0
    var indexY = 0
    for (index in 0 until list.size) {
      val item = list[index]
      if (indexX < offers?.xValue ?: 0) {
        //PaidProduct
        totalAmountWithDiscount += CalculationUtil.getUnitPrice(item.productDetails, context)
        item.cartVAT = CalculationUtil.getVATFromRegularPercentage(item.productDetails, context)
        singleItemVat += item.cartVAT ?: 0.0
        indexX += 1
        vatOldSub += item.productDetails.getRegularAmountVAT(context)?.times(item.qty) ?: 0.0
        item.discountedAmount = CalculationUtil.getDiscountAmount(item.productDetails, 0.0, context)
      } else {
        //Free Product
        indexY += 1
//        freeIndexes.add(index)
        if (indexY == offers?.yValue ?: 0) {
          indexX = 0
          indexY = 0
        }
        item.cartVAT = 0.0
        singleItemVat += item.cartVAT ?: 0.0
        vatOldSub += item.productDetails.getRegularAmountVAT(context)?.times(item.qty) ?: 0.0
        item.discountedAmount =
          CalculationUtil.getDiscountAmount(item.productDetails, 100.0, context)
        item.isFree = true
      }
      totalAmountDiscount += CalculationUtil.getUnitPrice(item.productDetails, context)
      discount = discount.plus(item.discountedAmount ?: 0.0)
      Logger.d("CartQTY", "${item.qty}")
      item.cartGrossTotal = CalculationUtil.getGrossLineTotal(item, context)
    }
    groupAmountWithoutDiscount = totalAmountDiscount
    groupAmountWithDiscount = totalAmountWithDiscount
    groupVAT = singleItemVat
    groupDiscountedAmount = discount
    calculateFreeIndexes(list)
  }

  private fun calculateFreeIndexes(list: ArrayList<CartModel>) {
    var indexX = 0
    var indexY = 0

    for (index in 0 until (getSlotsCount())) {
      if (indexX < offers?.xValue ?: 0) {
        //PaidProduct
        indexX += 1
      } else {
        //Free Product
        indexY += 1
        freeIndexes.add(index)
        if (indexY == offers?.yValue ?: 0) {
          indexX = 0
          indexY = 0
        }
      }
    }
  }

  private fun createSingleProductWithQTYArray() {
    productsWithQTY.clear()
    val tempStringArray = ArrayList<String>()
    when (getTypeEnum()) {
      OffersType.FREE_GIFT, OffersType.BXGY -> {
        for (i in 0 until products.size) {
          var count = 0
          var disocunt = 0.0
          var grossTotal = 0.0
          var cartVat = 0.0
          var instantOnly = false
          var expressOnly = false
          if (!tempStringArray.contains(products[i].productDetails.id)) {
            for (j in i until products.size) {
              if (products[i].productDetails.id == products[j].productDetails.id) {
                count += 1
                Logger.d("ProductCount", "$count")
                disocunt = products[j].discountedAmount?.let { disocunt.plus(it) } ?: 0.0
                Logger.d("grossTotal", "${products[j].cartGrossTotal}")
                grossTotal = products[j].cartGrossTotal?.let { grossTotal.plus(it) } ?: 0.0
                cartVat = products[j].cartVAT?.let { cartVat.plus(it) } ?: 0.0
                if (products[i].onlyExpress) {
                  expressOnly = true
                }
              }
            }
            val cartModel = CartModel()
            cartModel.productDetails = products[i].productDetails
            cartModel.qty = count
            cartModel.discountedAmount = disocunt
            cartModel.cartGrossTotal = grossTotal
            cartModel.cartVAT = cartVat
            cartModel.onlyInstant = products[i].onlyInstant
            cartModel.onlyExpress = products[i].onlyExpress
            productsWithQTY.add(cartModel)
            tempStringArray.add(products[i].productDetails.id)
          }
        }
      }
      OffersType.FLAT, OffersType.NON -> {
        productsWithQTY.add(products.first())
      }
    }

//    products
    val gson = Gson()
    val jsonStr = gson.toJson(productsWithQTY)
    Logger.d("productsWithQTY", jsonStr)
    checkIsInstant()
  }

  private fun checkIsInstant() {
    for (item in productsWithQTY) {
      try {
        when (item.productDetails.stockAvailability(item.qty)) {
          ShipmentType.INSTANT -> {
            isInstant = true
          }
          ShipmentType.EXPRESS -> {
            isInstant = false
            break
          }
          ShipmentType.STANDARD -> {
            isInstant = false
            break
          }
          ShipmentType.OUT_OF_STOCK -> {
            isInstant = false
            break
          }
        }
      } catch (e: Exception) {
        isInstant = false
      }
    }
    Logger.d("instantValue", isInstant.toString())
  }

}