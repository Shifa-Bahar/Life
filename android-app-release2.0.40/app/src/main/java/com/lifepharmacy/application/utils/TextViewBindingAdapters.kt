package com.lifepharmacy.application.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.google.gson.Gson
import com.lifepharmacy.application.R
import com.lifepharmacy.application.enums.ShipmentType
import com.lifepharmacy.application.enums.VoucherStatus
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.config.Config
import com.lifepharmacy.application.model.config.DeliverySlot
import com.lifepharmacy.application.model.home.Price
import com.lifepharmacy.application.model.home.PriceX
import com.lifepharmacy.application.model.orders.SubOrderItem
import com.lifepharmacy.application.model.payment.TransactionMainModel
import com.lifepharmacy.application.model.pcr.pcrlist.Products
import com.lifepharmacy.application.model.product.*
import com.lifepharmacy.application.model.vouchers.VoucherModel
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import com.lifepharmacy.application.utils.universal.GoogleUtils
import com.lifepharmacy.application.utils.universal.Logger
import java.util.*
import kotlin.math.roundToInt


object TextViewBindingAdapters {
  @JvmStatic
  @BindingAdapter("setQty")
  fun setQTy(
    view: TextView,
    qty: Double?
  ) {
    try {
      qty?.let {
        view.text = qty.roundToInt().toString()
      }

    } catch (e: Exception) {

    }

  }

  @JvmStatic
  @BindingAdapter(value = ["setFormatePcrPrice", "offers", "product"], requireAll = false)
  fun setFormatePcrPrice(
    view: TextView,
    prices: Double?,
    offers: com.lifepharmacy.application.model.pcr.pcrlist.Offers?,
    product: Products?
  ) {
    try {
      prices?.let {
        if (offers != null && offers.type == "flat_percentage_discount") {
          val percentagePrice = (prices * offers.value!!) / 100
          val discountedPrice = prices - percentagePrice

          if (product != null) {
            val amountAfterVat = CalculationUtil.getPcrPriceAfterVATOGive(discountedPrice, product)
            view.text = amountAfterVat.currencyFormat()
          } else {
            view.text = discountedPrice.currencyFormat()
          }


        } else {
          if (product != null) {
            val amountAfterVat = CalculationUtil.getPcrPriceAfterVATOGive(prices, product)
            view.text = amountAfterVat.currencyFormat()
          } else {
            view.text = prices.currencyFormat()
          }

        }
      }

    } catch (e: Exception) {

    }

  }

  @JvmStatic
  @BindingAdapter(value = ["setFormatePrice", "offers", "product"], requireAll = false)
  fun setFormatePrice(
    view: TextView,
    prices: Double?,
    offers: Offers?,
    product: ProductDetails?
  ) {
    try {
      prices?.let {
        if (offers != null && offers.type == "flat_percentage_discount") {
          val percentagePrice = (prices * offers.value!!) / 100
          val discountedPrice = prices - percentagePrice

          if (product != null) {
            val amountAfterVat = CalculationUtil.getPriceAfterVATOGive(discountedPrice, product)
            view.text = amountAfterVat.currencyFormat()
          } else {
            view.text = discountedPrice.currencyFormat()
          }


        } else {
          if (product != null) {
            val amountAfterVat = CalculationUtil.getPriceAfterVATOGive(prices, product)
            view.text = amountAfterVat.currencyFormat()
          } else {
            view.text = prices.currencyFormat()
          }

        }
      }

    } catch (e: Exception) {

    }

  }

  @JvmStatic
  @BindingAdapter(value = ["setRegularPrice", "offers", "product"], requireAll = false)
  fun setPcrRegularPrice(
    view: TextView,
    prices: Double?,
    offers: com.lifepharmacy.application.model.pcr.pcrlist.Offers?,
    product: Products?
  ) {
    try {
      prices?.let {
        if (offers != null && offers.type == "flat_percentage_discount") {
          if (product != null) {
            val amountAfterVat = CalculationUtil.getPcrPriceAfterVATOGive(prices, product)
            view.text = amountAfterVat.currencyFormat()
          } else {
            view.text = prices.currencyFormat()
          }
          view.visibility = View.VISIBLE
        } else {
          view.visibility = View.GONE
          view.text = prices.currencyFormat()
        }
      }

    } catch (e: Exception) {

    }

  }
  @JvmStatic
  @BindingAdapter(value = ["setRegularPrice", "offers", "product"], requireAll = false)
  fun setRegularPrice(
    view: TextView,
    prices: Double?,
    offers: Offers?,
    product: ProductDetails?
  ) {
    try {
      prices?.let {
        if (offers != null && offers.type == "flat_percentage_discount") {
          if (product != null) {
            val amountAfterVat = CalculationUtil.getPriceAfterVATOGive(prices, product)
            view.text = amountAfterVat.currencyFormat()
          } else {
            view.text = prices.currencyFormat()
          }
          view.visibility = View.VISIBLE
        } else {
          view.visibility = View.GONE
          view.text = prices.currencyFormat()
        }
      }

    } catch (e: Exception) {

    }

  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter(value = ["setPercentage"], requireAll = false)
  fun setPercentage(
    view: TextView,
    setPercentage: Offers?
  ) {
    try {
      if (setPercentage != null && setPercentage.type == "flat_percentage_discount") {
        view.visibility = View.VISIBLE
        view.text = "${setPercentage.value?.roundToInt().toString()} %"
      } else {
        view.visibility = View.GONE
      }
    } catch (e: Exception) {

    }

  }

  @JvmStatic
  @BindingAdapter(value = ["tvtextColor", "defaultColor"], requireAll = false)
  fun setTVTextColor(
    view: TextView,
    textColor: String?,
    defaultColor: String?
  ) {
    try {
      view.setTextColor(
        Color.parseColor(
          textColor ?: "#365FC9"
        )
      )
    } catch (e: Exception) {
      view.setTextColor(
        Color.parseColor(
          defaultColor
        )
      )
    }
  }


  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter(value = ["setTransactionTitle", "trailingText"], requireAll = false)
  fun setTransactionTitle(
    view: TextView,
    transactionMainModel: TransactionMainModel?,
    leadingText: String?
  ) {
    try {
      if (leadingText != null) {
        if (transactionMainModel != null) {
          if (transactionMainModel?.purpose == "wallet_recharge" && transactionMainModel?.method == "card") {
            view.text = view.context.getString(R.string.top_up)
          } else if (transactionMainModel?.purpose == "wallet_recharge" && transactionMainModel?.method == "refund") {
            view.text = view.context.getString(R.string.wallet_top_up_via) + " " + leadingText
          } else if (transactionMainModel?.purpose == "return_to_source" && transactionMainModel?.method == "refund") {
            view.text = view.context.getString(R.string.refund_to_bank) + " " + leadingText
          } else {
            view.text = view.context.getString(R.string.purchase) + " " + leadingText
          }
        } else {
          view.text = view.context.getString(R.string.purchase) + " " + leadingText
        }

      } else {
        if (transactionMainModel != null) {

          if (transactionMainModel?.purpose == "wallet_recharge" && transactionMainModel?.method == "card") {
            view.text = view.context.getString(R.string.top_up)
          } else if (transactionMainModel?.purpose == "wallet_recharge" && transactionMainModel?.method == "refund") {
            view.text = view.context.getString(R.string.wallet_top_up_via)
          } else if (transactionMainModel?.purpose == "return_to_source" && transactionMainModel?.method == "refund") {
            view.text = view.context.getString(R.string.refund_to_bank) + " " + leadingText
          } else {
            view.text = view.context.getString(R.string.purchase)
          }
        } else {
          view.text = view.context.getString(R.string.purchase)
        }

      }


    } catch (e: Exception) {

    }

  }


  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setCountReviews")
  fun setCountReviews(
    view: TextView,
    ratings: ProductRating?
  ) {
    try {
      ratings?.let {
        var sumRating =
          ratings.ratingDetails.x1 + ratings.ratingDetails.x2 + ratings.ratingDetails.x3 + ratings.ratingDetails.x4 + ratings.ratingDetails.x5
        view.text = "($sumRating Review )"
      }

    } catch (e: Exception) {

    }

  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setReviewsCountTitle")
  fun setReviewsCountTitle(
    view: TextView,
    ratings: ProductRating?
  ) {
    try {
      ratings?.let {
        var sumRating =
          ratings.ratingDetails.x1 + ratings.ratingDetails.x2 + ratings.ratingDetails.x3 + ratings.ratingDetails.x4 + ratings.ratingDetails.x5
        view.text =
          view.context.getString(R.string.based_on) + " " + sumRating + " " + view.context.getString(
            R.string.rating
          )
      }

    } catch (e: Exception) {

    }

  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("checkAndSetPrice")
  fun checkAndSetPrice(
    view: TextView,
    prices: PriceX?
  ) {
    try {
      prices?.let {
        if (prices.offerPrice == null || prices.offerPrice == 0.0) {
          view.text = prices.regularPrice.currencyFormat()
        } else {
          view.text = prices.offerPrice.currencyFormat()
        }
      }


    } catch (e: Exception) {

    }

  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter(value = ["setPriceDetailsCount", "firstTitle"], requireAll = false)
  fun setPriceDetailsCount(
    view: TextView,
    string: String?,
    firstTitle: String?,
  ) {
    try {
      string?.let {
        view.text = firstTitle +
            " " + view.context.getString(R.string.open_small_bracket) + string + "" + view.context.getString(
          R.string.close_small_bracket
        )
      }


    } catch (e: Exception) {

    }

  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter(value = ["setDateFromServer", "startDateTitle"], requireAll = false)
  fun setDateFromServer(
    view: TextView,
    string: String?,
    startDateTitle: String?
  ) {
    try {
      string?.let {
        if (startDateTitle.isNullOrEmpty()) {
          view.text = DateTimeUtil.getFormatTimeString(it)
        } else {
          view.text = startDateTitle + " " + DateTimeUtil.getFormatTimeString(it)
        }

      }
    } catch (e: Exception) {
    }
  }

  @BindingAdapter(value = ["setShortVersionDate", "startDateTitle"], requireAll = false)
  fun setShortVersionDate(
    view: TextView,
    string: String?,
    startDateTitle: String?
  ) {
    try {
      string?.let {
        if (startDateTitle.isNullOrEmpty()) {
          view.text = DateTimeUtil.getShortFormatTimeString(it)
        } else {
          view.text = startDateTitle + " " + DateTimeUtil.getShortFormatTimeString(it)
        }

      }
    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter(value = ["setDateWithOutTime", "startDateTitle"], requireAll = false)
  fun setDateWithOutTime(
    view: TextView,
    string: String?,
    startDateTitle: String?
  ) {
    try {
      string?.let {
        if (startDateTitle != null) {
          view.text = startDateTitle + " " + DateTimeUtil.getFormatDateOnlyString(it)
        } else {
          view.text = DateTimeUtil.getFormatDateOnlyString(it)
        }

      }
    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setOfferTypeTag")
  fun setOfferTypeTag(
    view: TextView,
    offer: Offers?,
  ) {
    try {
      view.visibility = View.GONE
      offer?.let {
        view.visibility = View.VISIBLE
        when (offer.getTypeEnum()) {
          OffersType.BXGY -> {
            view.text =
              "${view.context.getString(R.string.buy)} ${offer.xValue} ${view.context.getString(R.string.get)} ${offer.yValue}"
          }
          OffersType.FREE_GIFT -> {
            view.text =
              "${view.context.getString(R.string.free)} ${offer.value?.roundToInt()} ${
                view.context.getString(
                  R.string.gift
                )
              }"
          }
          OffersType.FLAT -> {
            view.text =
              "${view.context.getString(R.string.flat)} ${offer.value?.roundToInt()}% ${
                view.context.getString(
                  R.string.off
                )
              }"

          }
          OffersType.NON -> {
            view.visibility = View.GONE
          }
        }

      }
    } catch (e: Exception) {
      view.visibility = View.GONE
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setBlogDate")
  fun setBlogDate(
    view: TextView,
    string: String?,
  ) {
    try {
      string?.let {
        view.text = "" + DateTimeUtil.getStringFromServerBlogDate(it)
      }
    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("amount", "prices")
  fun setPriceWithCurrency(
    view: TextView,
    amount: Double?,
    prices: ArrayList<Price>?
  ) {
    try {
      amount?.let {
        var currency = prices?.let { it1 -> PricesUtil.getRelativePrice(it1, view.context) }
        view.text = (currency?.currency ?: "AED") + " " + amount.currencyFormat()
      }
    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("prices")
  fun setPriceFromList(
    view: TextView,
    prices: ArrayList<Price>?
  ) {
    try {
      var correctPrice = prices?.let { it1 -> PricesUtil.getRelativePrice(it1, view.context) }
      view.text = correctPrice?.currency + " " + correctPrice?.price?.regularPrice?.currencyFormat()

    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter(value = ["setPriceWithPrefsCurrency", "sign"], requireAll = false)
  fun setPriceWithPrefsCurrency(
    view: TextView,
    amount: Double?,
    sign: String?
  ) {
    try {
      val preference: SharedPreferences =
        view.context.applicationContext.getSharedPreferences(
          view.context.packageName,
          Context.MODE_PRIVATE
        )
      val currency = preference.getString(
        ConstantsUtil.SH_CURRENCY,
        "AED"
      )
      amount?.let {
        view.text = currency + " " + amount.currencyFormat()
        if (sign != null) {
          view.text = sign + " " + currency + " " + amount.currencyFormat()
        }
      }
    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setPriceWithPrefsCurrencyForOrderItem")
  fun setPriceWithPrefsCurrencyForOrderItem(
    view: TextView,
    linePrice: Double?,
  ) {
    try {
      val preference: SharedPreferences =
        view.context.applicationContext.getSharedPreferences(
          view.context.packageName,
          Context.MODE_PRIVATE
        )
      val currency = preference.getString(
        ConstantsUtil.SH_CURRENCY,
        "AED"
      )
      linePrice?.let {
        if (it > 0) {
          view.text = currency + " " + it.currencyFormat()

        } else {
          view.text = view.context.getText(R.string.free)
        }

      }
    } catch (e: Exception) {
    }
  }


  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setNotEnoughBalance")
  fun setNotEnoughBalance(
    view: TextView,
    amount: Double?
  ) {
    try {
      val preference: SharedPreferences =
        view.context.applicationContext.getSharedPreferences(
          view.context.packageName,
          Context.MODE_PRIVATE
        )
      val currency = preference.getString(
        ConstantsUtil.SH_CURRENCY,
        "AED"
      )
      amount?.let {
        view.text =
          view.context.getString(R.string.not_enough_amount) + " " + currency + " " + it.currencyFormat() + " " + view.context.getString(
            R.string.with_card
          )
      }
    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter(value = ["setCharges"], requireAll = false)
  fun setCharges(
    view: TextView,
    amount: Double?,
  ) {
    try {
      val preference: SharedPreferences =
        view.context.applicationContext.getSharedPreferences(
          view.context.packageName,
          Context.MODE_PRIVATE
        )
      val currency = preference.getString(
        ConstantsUtil.SH_CURRENCY,
        "AED"
      )
      amount?.let {
        if (amount > 0.0) {
          view.text = currency + " " + amount.currencyFormat()
        } else {
          view.text = view.context.getString(R.string.free)
        }

      }
    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setPriceStringWithPrefsCurrency")
  fun setPriceStringWithPrefsCurrency(
    view: TextView,
    amount: String?,
  ) {
    try {
      val preference: SharedPreferences =
        view.context.applicationContext.getSharedPreferences(
          view.context.packageName,
          Context.MODE_PRIVATE
        )
      val currency = preference.getString(
        ConstantsUtil.SH_CURRENCY,
        "AED"
      )
      amount?.let {
        view.text = "$currency ${amount.stringToNullSafeDouble().currencyFormat()}"
      }
    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setAddress")
  fun setAddress(
    view: TextView,
    setAddress: AddressModel?,
  ) {
    try {
      if (setAddress == null || (setAddress.streetAddress.isNullOrEmpty() && setAddress.area.isNullOrEmpty())) {
        view.text = view.context.getString(R.string.no_address_selected)
      } else {
        if (setAddress.type.isNullOrEmpty()) {
          view.text = "${setAddress.streetAddress}"
        } else {
          view.text = "${setAddress.type}:  ${setAddress.streetAddress},${setAddress.area} "
        }
      }

    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setStringPriceWithPrefsCurrency")
  fun setStringPriceWithPrefsCurrency(
    view: TextView,
    amount: String?,
  ) {
    try {
      val preference: SharedPreferences =
        view.context.applicationContext.getSharedPreferences(
          view.context.packageName,
          Context.MODE_PRIVATE
        )
      val currency = preference.getString(
        ConstantsUtil.SH_CURRENCY,
        null
      )
      amount?.let {
        view.text = "$currency $amount"
      }
    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setTextForFreeShipping")
  fun setTextForFreeShipping(
    view: TextView,
    amount: Double?,
  ) {
    try {
      val preference: SharedPreferences =
        view.context.applicationContext.getSharedPreferences(
          view.context.packageName,
          Context.MODE_PRIVATE
        )
      val gson = Gson()
      val config: Config? = gson.fromJson(
        preference.getString(ConstantsUtil.SH_CONFIG, null),
        Config::class.java
      )
      amount?.let {
        if (it >= config?.mINIMUMORDER ?: ConstantsUtil.MINIMUM_ORDER) {
          view.text = view.context.getString(R.string.eligible_for_free_shipping)
        } else {
          val difference = (config?.mINIMUMORDER ?: ConstantsUtil.MINIMUM_ORDER) - it
          view.text =
            view.context.getString(R.string.add) + " " + difference.currencyFormat() + " " + view.context.getString(
              R.string.more_for_free_shipping
            )
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("checked", "count")
  fun setInstantSwitchText(
    view: SwitchCompat,
    checked: Boolean?,
    count: String?,
  ) {
    try {
      val preference: SharedPreferences =
        view.context.applicationContext.getSharedPreferences(
          view.context.packageName,
          Context.MODE_PRIVATE
        )
      val gson = Gson()
      var config = gson.fromJson(
        preference.getString(ConstantsUtil.SH_CONFIG, null),
        Config::class.java
      )
      checked?.let {
        if (checked) {

          val text =
            "<font color=#1EC444><b>${config.iNSTANTTIME.toString()}  ${view.context.getString(R.string.mins)}</b></font> <font color=#002579> ${
              view.context.getString(
                R.string.delivery
              )
            }</font> ( $count )"

          val html = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY);
          view.text = html
//
//          view.text =
//            config.iNSTANTTIME.toString() + view.context.getString(R.string.mins_delivery) + " " + view.context.getString(
//              R.string.open_small_bracket
//            ) + count + " " + view.context.getString(R.string.close_small_bracket)

        } else {
          val text =
            "<font color=#002579>${view.context.getString(R.string.expected_delivery_in)} </font> <font color=#1EC444> <b>${config.iNSTANTTIME.toString()}  ${
              view.context.getString(
                R.string.mins
              )
            }</b></font>"

          val html = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY);
          view.text = html
//          view.text = view.context.getString(R.string.instant_switch_title)
        }
      }

    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("count")
  fun setInstantTextFieldText(
    view: TextView,
    count: String?,
  ) {
    try {
      val preference: SharedPreferences =
        view.context.applicationContext.getSharedPreferences(
          view.context.packageName,
          Context.MODE_PRIVATE
        )
      val gson = Gson()
      var config = gson.fromJson(
        preference.getString(ConstantsUtil.SH_CONFIG, null),
        Config::class.java
      )

      val text =
        "${config.iNSTANTTIME.toString()}  ${view.context.getString(R.string.mins_delivery)}"
      view.text = text
//

    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setCurrency")
  fun setCurrency(
    view: TextView,
    prices: ArrayList<Price>?
  ) {
    try {
      var currency = prices?.let { it1 -> PricesUtil.getRelativePrice(it1, view.context) }
      view.text = currency?.currency
    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setPrice")
  fun setPrice(
    view: TextView,
    prices: ArrayList<Price>?
  ) {
    try {
      prices?.let {
        var currency = prices.let { it1 -> PricesUtil.getRelativePrice(it1, view.context) }
        view.text = currency?.price?.offerPrice?.currencyFormat()
      }
    } catch (e: Exception) {
    }
  }

  @JvmStatic
  @BindingAdapter(value = ["setSoldOutTextVisibility", "enteredQty"], requireAll = false)
  fun setSoldOutTextVisibility(
    view: TextView,
    availability: ProductDetails?,
    qty: Int?,
  ) {
    when (availability?.stockAvailability(qty ?: 1)) {
      ShipmentType.OUT_OF_STOCK -> {
        view.visibility = View.VISIBLE
      }
      else -> {
        view.visibility = View.GONE
      }
    }
  }


  @JvmStatic
  @BindingAdapter(value = ["ifItemSoldOutVisibilityLL", "enteredQtyLL"], requireAll = false)
  fun ifItemSoldOutVisibilityLL(
    view: LinearLayout,
    ifItemSoldOutVisibilityLL: ProductDetails?,
    qty: Double?,
  ) {
    when (ifItemSoldOutVisibilityLL?.stockAvailability(qty?.toInt() ?: 1)) {
      ShipmentType.OUT_OF_STOCK -> {
        view.visibility = View.GONE
      }
      else -> {
        view.visibility = View.VISIBLE
      }
    }
  }

  @JvmStatic
  @BindingAdapter(value = ["setAddCartVisibility", "isInCart"], requireAll = false)
  fun setAddPcrCartVisibility(
    view: TextView,
    availability: Products?,
    isInCart: Boolean?,
  ) {
    if (isInCart == true || availability == null) {
      view.visibility = View.GONE
    } else {
      view.visibility = View.VISIBLE
    }
//    if ((isInCart == true || availability == null)
//      || (availability.instant?.isAvailable != true && availability.express?.isAvailable != true)
//    ) {
//      view.visibility = View.GONE
//    } else {
//      view.visibility = View.VISIBLE
//    }
  }


  @JvmStatic
  @BindingAdapter(value = ["setAddCartVisibility", "isInCart"], requireAll = false)
  fun setAddCartVisibility(
    view: TextView,
    availability: ProductDetails?,
    isInCart: Boolean?,
  ) {
    if (isInCart == true) {
      view.visibility = View.GONE
    } else {
      when (availability?.stockAvailability()) {
        ShipmentType.OUT_OF_STOCK -> {
          view.visibility = View.GONE
        }
        else -> {
          view.visibility = View.VISIBLE
        }
      }
    }
//    if ((isInCart == true || availability == null)
//      || (availability.instant?.isAvailable != true && availability.express?.isAvailable != true)
//    ) {
//      view.visibility = View.GONE
//    } else {
//      view.visibility = View.VISIBLE
//    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setOffersTag")
  fun setOffersTag(
    view: TextView,
    offers: Offers?,
  ) {
    offers?.let {
      when (offers.getTypeEnum()) {
        OffersType.BXGY -> {
          view.text =
            "${view.context.getString(R.string.buy)} ${offers.xValue} ${view.context.getString(R.string.get)} ${offers.yValue}"
        }
        OffersType.FLAT -> {
          view.text =
            "${view.context.getString(R.string.flat)} ${offers.value?.roundToInt()} %${
              view.context.getString(
                R.string.off
              )
            }"
        }
        OffersType.FREE_GIFT -> {
          view.text = ""
        }
        OffersType.NON -> {
          view.text = ""
        }
        else -> {
          view.visibility = View.GONE
        }
      }
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter(value = ["bindStartingDate"], requireAll = false)
  fun setVoucherStartDate(
    view: TextView,
    bindStartingDate: String?,
  ) {
    try {
      view.text =
        "${view.context.getString(R.string.valid_from)} ${
          DateTimeUtil.getCalenderStringFromStringDateOnlyWithoutTimeZone(
            bindStartingDate ?: ""
          )
        }"

    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter(value = ["bindEndDate", "bindVoucher"], requireAll = false)
  fun setVoucherEndDate(
    view: TextView,
    bindEndDate: String?,
    voucher: VoucherModel?
  ) {
    try {
      when (voucher?.let { StatusUtil.voucherStatusEnum(it) }) {
        VoucherStatus.VALID -> {
          view.text =
            "${view.context.getString(R.string.expiring_on)}  ${
              DateTimeUtil.getFormatDateOnlyStringWithOutTimeZone(
                bindEndDate ?: ""
              )
            }"
        }
        VoucherStatus.EXPIRED -> {
          view.text =
            "${view.context.getString(R.string.expired_on)}  ${
              DateTimeUtil.getFormatDateOnlyStringWithOutTimeZone(
                bindEndDate ?: ""
              )
            }"
        }
        else -> {
          "  ${DateTimeUtil.getFormatDateOnlyStringWithOutTimeZone(bindEndDate ?: "")}"
        }
      }


    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setVoucherStatus")
  fun setVoucherStatus(
    view: TextView,
    voucher: VoucherModel?,
  ) {
    voucher?.let {
      view.text = StatusUtil.getVoucherStatusUtil(it, view.context)
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter(value = ["setLabelDrawable", "bindEndDate"], requireAll = false)
  fun setLabelDrawable(
    view: TextView,
    setLabel: LabelModel?,
    bindEndDate: String?
  ) {
    try {
      when (setLabel?.iconType) {
        1 -> {
          view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.stars, 0);
        }
        2 -> {
          view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.stars, 0);
        }
        3 -> {
          view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.stars, 0);
        }
      }
      view.text = setLabel?.labelText

    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setDeliveryTime", "enteredQty", "deliverySlot")
  fun setDeliveryTime(
    view: TextView,
    setDeliveryTime: Availability?,
    enteredQty: String?,
    deliverySlot: DeliverySlot,
  ) {
    val preference: SharedPreferences =
      view.context.applicationContext.getSharedPreferences(
        view.context.packageName,
        Context.MODE_PRIVATE
      )
    val gson = Gson()
    val config: Config? = gson.fromJson(
      preference.getString(ConstantsUtil.SH_CONFIG, null),
      Config::class.java
    )
    val qty = enteredQty?.stringToNullSafeDouble()?.toInt() ?: 0
    when (setDeliveryTime?.getAvailability(qty)) {
      ShipmentType.INSTANT -> {
        view.visibility = View.VISIBLE
        view.text = deliverySlot.time
        Log.e(TAG, "setDeliveryTime: ${deliverySlot.time}")

      }
      ShipmentType.EXPRESS -> {
        view.visibility = View.VISIBLE
        Log.e(TAG, "setDeliveryTime: ${deliverySlot.time}")
        view.text = deliverySlot.time
        Logger.d(
          "isDeliverCanBeToday", DateTimeUtil.isDeliverCanBeToday(
            config?.sameDayEndTime?.roundToInt() ?: 14
          ).toString()
        )
        if (DateTimeUtil.isDeliverCanBeToday(
            config?.sameDayEndTime?.roundToInt() ?: 14
          ) && config?.sameDayDeliveryZone?.polygons?.let {
            GoogleUtils.isUserInGivePolyGoneList(
              it
            )
          } == true
        ) {
//          view.text = view.context.getString(R.string.today)
          view.text = view.context.getString(R.string.str24_to_48_hours)
        } else {
//          view.text = view.context.getString(R.string.tomorrow)
          view.text = view.context.getString(R.string.str24_to_48_hours)
        }

      }
      else -> {
        view.visibility = View.GONE
      }
    }

  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setTodayDeliveryTimer", "enteredQty")
  fun setTodayDeliveryTimer(
    view: TextView,
    setDeliveryTime: Availability?,
    enteredQty: String?
  ) {
    val preference: SharedPreferences =
      view.context.applicationContext.getSharedPreferences(
        view.context.packageName,
        Context.MODE_PRIVATE
      )
    val gson = Gson()
    val config = gson.fromJson(
      preference.getString(ConstantsUtil.SH_CONFIG, null),
      Config::class.java
    )
    val qty = enteredQty?.stringToNullSafeDouble()?.toInt() ?: 0
    when (setDeliveryTime?.getAvailability(qty)) {
      ShipmentType.INSTANT -> {
        view.visibility = View.VISIBLE
        view.text = "${config.iNSTANTTIME} mins"

      }
      ShipmentType.EXPRESS -> {
        view.visibility = View.VISIBLE
        Logger.d(
          "isDeliverCanBeToday", DateTimeUtil.isDeliverCanBeToday(
            config.sameDayEndTime?.roundToInt() ?: 14
          ).toString()
        )
        if (DateTimeUtil.isDeliverCanBeToday(
            config.sameDayEndTime?.roundToInt() ?: 14
          ) && config.sameDayDeliveryZone?.polygons?.let {
            GoogleUtils.isUserInGivePolyGoneList(
              it
            )
          } == true
        ) {
          view.text = view.context.getString(R.string.today)
        } else {
          view.text = view.context.getString(R.string.tomorrow)
        }

      }
      else -> {
        view.visibility = View.GONE
      }
    }

  }

  @JvmStatic
  @BindingAdapter("setCounterVisibility", "enteredQty")
  fun setCounterVisibility(
    view: TextView,
    availability: ProductDetails?,
    enteredQty: String?
  ) {
    val preference: SharedPreferences =
      view.context.applicationContext.getSharedPreferences(
        view.context.packageName,
        Context.MODE_PRIVATE
      )
    val gson = Gson()
    val config = gson.fromJson(
      preference.getString(ConstantsUtil.SH_CONFIG, null),
      Config::class.java
    )
    val qty = enteredQty?.stringToNullSafeDouble()?.toInt() ?: 0
    when (availability?.stockAvailability(qty)) {
      ShipmentType.INSTANT -> {
        view.visibility = View.GONE
      }
      ShipmentType.EXPRESS -> {
        if (DateTimeUtil.isDeliverCanBeToday(
            config.sameDayEndTime?.roundToInt() ?: 14
          ) && config.sameDayDeliveryZone?.polygons?.let {
            GoogleUtils.isUserInGivePolyGoneList(
              it
            )
          } == true
        ) {
          view.visibility = View.VISIBLE
        } else {
          view.visibility = View.GONE
        }

      }
      else -> {
        view.visibility = View.GONE
      }
    }
  }

  @JvmStatic
  @BindingAdapter("setCounterVisibilityLL", "enteredQtyForLL")
  fun setCounterVisibilityLayout(
    view: LinearLayout,
    availabilityLL: ProductDetails?,
    enteredQtyLL: String?
  ) {
    val preference: SharedPreferences =
      view.context.applicationContext.getSharedPreferences(
        view.context.packageName,
        Context.MODE_PRIVATE
      )
    val gson = Gson()
    val config = gson.fromJson(
      preference.getString(ConstantsUtil.SH_CONFIG, null),
      Config::class.java
    )
    val qty = enteredQtyLL?.stringToNullSafeDouble()?.toInt() ?: 0
    when (availabilityLL?.stockAvailability(qty)) {
      ShipmentType.INSTANT -> {
        view.visibility = View.GONE
      }
      ShipmentType.EXPRESS -> {
        if (DateTimeUtil.isDeliverCanBeToday(
            config.sameDayEndTime?.roundToInt() ?: 14
          ) && config.sameDayDeliveryZone?.polygons?.let {
            GoogleUtils.isUserInGivePolyGoneList(
              it
            )
          } == true
        ) {
          view.visibility = View.VISIBLE
        } else {
          view.visibility = View.GONE
        }

      }
      else -> {
        view.visibility = View.GONE
      }
    }
  }

  @SuppressLint("setStatusTint")
  @JvmStatic
  @BindingAdapter("setStatusTint")
  fun setStatusTint(
    view: TextView,
    setStatusTint: Int?,
  ) {
    try {
      when (setStatusTint) {
        0 -> {
          view.backgroundTintList = (view.context?.let {
            ColorStateList.valueOf(Color.parseColor("#FFF4DD"))
          })
        }
        1 -> {
          view.backgroundTintList = (view.context?.let {
            ColorStateList.valueOf(Color.parseColor("#DDFFF6"))
          })
        }
        2 -> {
          view.backgroundTintList = (view.context?.let {
            ColorStateList.valueOf(Color.parseColor("#DDFAFF"))
          })
        }
        3 -> {
          view.backgroundTintList = (view.context?.let {
            ColorStateList.valueOf(Color.parseColor("#F1FFDD"))
          })
        }
        4 -> {
          view.backgroundTintList = (view.context?.let {
            ColorStateList.valueOf(Color.parseColor("#FFDDDD"))
          })
        }

      }

    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setStatusBackColor")
  fun setStatusBackColor(
    view: TextView,
    status: Int?
  ) {
    try {
      status.let {
        when (it) {
          0 -> {
            view.backgroundTintList = (view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#D6F6FF"))
            })
          }
          1 -> {
            view.backgroundTintList = (view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#D6DCFF"))
            })
          }
          2 -> {
            view.backgroundTintList = (view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#F7D6FF"))
            })

          }
          3 -> {
            view.backgroundTintList = (view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#FFFAD6"))
            })

          }
          4 -> {
            view.backgroundTintList = (view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#D6FFEF"))
            })

          }
          5 -> {
            view.backgroundTintList = (view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#BFFFC4"))
            })

          }
          6 -> {
            view.backgroundTintList = (view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#FFDBD6"))
            })

          }
          else -> {

          }
        }
      }
    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setStatusBackColorCard")
  fun setStatusBackColorCard(
    view: CardView,
    status: Int?
  ) {
    try {
      status.let {
        when (it) {
          0 -> {
            view.setCardBackgroundColor((view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#D6F6FF"))
            }!!))
          }
          1 -> {
            view.setCardBackgroundColor((view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#D6DCFF"))
            }!!))
          }
          2 -> {
            view.setCardBackgroundColor((view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#F7D6FF"))
            }!!))

          }
          3 -> {
            view.setCardBackgroundColor((view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#FFFAD6"))
            }!!))

          }
          4 -> {
            view.setCardBackgroundColor((view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#D6FFEF"))
            }!!))

          }
          5 -> {
            view.setCardBackgroundColor((view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#BFFFC4"))
            }!!))

          }
          6 -> {
            view.setCardBackgroundColor((view.context?.let {
              ColorStateList.valueOf(Color.parseColor("#FFDBD6"))
            }!!))

          }
          else -> {

          }
        }
      }
    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setShipmentStatusDeliveryLabel")
  fun setShipmentStatusDeliveryLabel(
    view: TextView,
    subOrderItem: SubOrderItem,
  ) {
    try {
      if (subOrderItem.status == 5 || subOrderItem.status == 6) {
        view.text =
          "${subOrderItem.statusLabel}  ${view.context.getString(R.string.on_small_collen)}"
      } else {
        if (subOrderItem.fulfilmentType?.toLowerCase(Locale.ROOT) == "instant") {
          view.text = view.context.getString(R.string.expected_delivery_in)
        } else {
          view.text = view.context.getString(R.string.expected_delivery_by)
        }

      }

    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter("setShipmentStatusDeliveryDate")
  fun setShipmentStatusDeliveryDate(
    view: TextView,
    subOrderItem: SubOrderItem,
  ) {
    try {
      if (subOrderItem.status == 5 || subOrderItem.status == 6) {
        view.text = subOrderItem.updatedAt?.let {
          DateTimeUtil.getFormatDateOnlyString(
            it
          )
        }
      } else {
        if (subOrderItem.fulfilmentType?.toLowerCase(Locale.ROOT) == "instant") {
          timerCountDown(view, subOrderItem)
        } else {
          view.text = subOrderItem.expectedDate?.let {
            DateTimeUtil.getStringDateFromStringWithoutTimeZoneAndMilSecs(
              it
            )
          }
        }


      }

    } catch (e: Exception) {
    }
  }

//  @SuppressLint("SetTextI18n")
//  @JvmStatic
//  @BindingAdapter(value = ["setShipmentDeliveryDate","currentStatus","isDeliveryStatus"],requireAll = false)
//  fun setShipmentDeliveryDate(
//    view: TextView,
//    date: String?,
//    currentStatus: Int?,
//    isDeliveryStatus:Boolean
//  ) {
//    try {
//      if (currentStatus == 5 || currentStatus == 6) {
//        view.text = date?.let {
//          DateTimeUtil.getFormatDateOnlyString(
//            it
//          )
//        }
//      } else {
//        if (isDeliveryStatus){
//          view.text = date?.let {
//            DateTimeUtil.getStringDateFromStringWithoutTimeZoneAndMilSecs(
//              it
//            )
//          }
//        }else{
//          view.text = date?.let {
//            DateTimeUtil.getFormatDateOnlyString(
//              it
//            )
//          }
//        }
//      }
//
//    } catch (e: Exception) {
//    }
//  }

  @SuppressLint("SetTextI18n")
  @JvmStatic
  @BindingAdapter(value = ["statusDate", "subOrderItem", "isDeliveryStatus"], requireAll = false)
  fun setShipmentDeliveryDate(
    view: TextView,
    date: String?,
    subOrderItem: SubOrderItem,
    isDeliveryStatus: Boolean
  ) {
    try {
      if (isDeliveryStatus) {
        if (subOrderItem.status == 5 || subOrderItem.status == 6) {
          view.text = date?.let {
            DateTimeUtil.getFormatTimeString(
              it
            )
          }
        } else {
          if (subOrderItem.fulfilmentType?.toLowerCase(Locale.ROOT) == "instant") {
            timerCountDown(view, subOrderItem)
          } else {
            view.text = date?.let {
              DateTimeUtil.getStringDateFromStringWithoutTimeZoneAndMilSecs(
                it
              )
            }
          }
        }
      } else {
        view.text = date?.let {
          DateTimeUtil.getFormatTimeString(
            it
          )
        }
      }
    } catch (e: Exception) {
    }
  }

  private fun timerCountDown(textView: TextView, item: SubOrderItem) {
    val duration: Long? =
      item.expectedDate?.let {
        DateTimeUtil.getCalenderObjectFromStringWithoutTimeZoneAndMilSec(it)?.let {
          DateTimeUtil.getTimeDiffInMilsFromToDatesWithMinusOneMin(
            DateTimeUtil.getCurrentDateAndTime(),
            it
          )
        }
      } //6 hours
//   var minusTime =  duration?.minus(1000)
    object : CountDownTimer(duration ?: 20000, 1000) {
      override fun onTick(millisUntilFinished: Long) {
        var millisUntilFinished = millisUntilFinished
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val elapsedHours = millisUntilFinished / hoursInMilli
        millisUntilFinished = millisUntilFinished % hoursInMilli
        val elapsedMinutes = millisUntilFinished / minutesInMilli
        millisUntilFinished = millisUntilFinished % minutesInMilli
        val elapsedSeconds = millisUntilFinished / secondsInMilli
//        val yy = String.format("%02d minutes", elapsedMinutes)
        var yy = "59 minutes"
        yy = if (elapsedHours.toString() == "0") {
          String.format("%02d minutes", elapsedMinutes)
        } else {
//          String.format("%02d hours & %02d minutes", elapsedHours, elapsedMinutes)
          String.format("%02d hours", elapsedHours)
        }

        textView.text = yy
//        :%02d secs
      }

      override fun onFinish() {

      }
    }.start()
  }


}