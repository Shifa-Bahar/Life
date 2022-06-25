package com.lifepharmacy.application.utils

import android.content.Context
import android.content.SharedPreferences
import com.lifepharmacy.application.model.home.Price
import com.lifepharmacy.application.model.pcr.pcrlist.Prices
import com.lifepharmacy.application.utils.universal.ConstantsUtil

/**
 * Created by Zahid Ali
 */
object PricesUtil {
  fun getUnitPrice(prices: ArrayList<Price>?, context: Context): Double {
    val preference: SharedPreferences =
      context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val code = preference.getString(
      ConstantsUtil.SH_Country,
      null
    )
    val price = prices?.firstOrNull { price -> price.countryCode == code }
    var returnDouble = 0.0
    price?.price.let {
      if (price?.price?.offerPrice == null || price.price.offerPrice == 0.0) {
        it?.regularPrice?.let { regularPrice ->
          returnDouble = regularPrice
        }

      } else {
        returnDouble = price.price.offerPrice
      }
    }
    return returnDouble
  }
  fun getUnitPcrPrice(prices: ArrayList<Prices>?, context: Context): Double {
    val preference: SharedPreferences =
      context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val code = preference.getString(
      ConstantsUtil.SH_Country,
      null
    )
    val price = prices?.firstOrNull { price -> price.countryCode == code }
    var returnDouble = 0.0
    price?.price.let {
      if (price?.price?.offerPrice == null || price.price.offerPrice == 0.0) {
        it?.regularPrice?.let { regularPrice ->
          returnDouble = regularPrice
        }

      } else {
        returnDouble = price.price.offerPrice
      }
    }
    return returnDouble
  }
  fun getRelativePcrPrice(pricez: ArrayList<Prices>?, context: Context): Prices {
    var price: Prices? = Prices()
    if (!pricez.isNullOrEmpty()) {
      val preference: SharedPreferences =
        context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
      val code = preference.getString(
        ConstantsUtil.SH_Country,
        null
      )
      price = pricez.firstOrNull { item -> item.countryCode == code }

      if (price == null) {
        if (pricez.isNotEmpty()) {
          price = pricez.first()
        }
      }
      price?.let {
        preference.edit().putString(ConstantsUtil.SH_CURRENCY, price.currency).apply()
      }
    } else {
      price?.price?.regularPrice = 0.0

    }

    return price!!
  }
  fun getRelativePrice(prices: ArrayList<Price>?, context: Context): Price {
    var price: Price? = Price()
    if (!prices.isNullOrEmpty()) {
      val preference: SharedPreferences =
        context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
      val code = preference.getString(
        ConstantsUtil.SH_Country,
        null
      )
      price = prices.firstOrNull { item -> item.countryCode == code }
      if (price == null) {
        if (prices.isNotEmpty()) {
          price = prices.first()
        }
      }
      price?.let {
        preference.edit().putString(ConstantsUtil.SH_CURRENCY, price.currency).apply()
      }
    } else {
      price?.countryCode = "ae"
      price?.price?.regularPrice = 0.0

    }

    return price!!
  }

}