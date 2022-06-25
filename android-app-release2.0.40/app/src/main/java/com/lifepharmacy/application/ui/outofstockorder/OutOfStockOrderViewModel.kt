package com.lifepharmacy.application.ui.outofstockorder

import android.app.Application
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.CartRepository
import com.lifepharmacy.application.managers.OffersManagers
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.cart.*
import com.lifepharmacy.application.model.config.DeliverySlot
import com.lifepharmacy.application.model.home.Price
import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.PlaceOrderRequest
import com.lifepharmacy.application.model.orders.outofstock.Item
import com.lifepharmacy.application.model.orders.outofstock.OutOfStockRequestBody
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.utils.CalculationUtil
import com.lifepharmacy.application.utils.DateTimeUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Extensions.doubleDigitDouble
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDoubleByDefault1
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeInt
import com.lifepharmacy.application.utils.universal.InputEditTextValidator
import com.lifepharmacy.application.utils.universal.Logger
import com.lifepharmacy.application.utils.universal.GoogleUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

/**
 * Created by Zahid Ali
 */
class OutOfStockOrderViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val cartRepository: CartRepository,
  application: Application
) : BaseViewModel(application) {
  var selectedPaymentType = MutableLiveData<PaymentType>()
  var isWalletEnable = MutableLiveData<Boolean>()
  var walletDifference = MutableLiveData<Double>()
  var totalAmount = MutableLiveData<Double>()
  var shipmentCharges = MutableLiveData<Double>()
  var expressCharge = MutableLiveData<Double>()
  var paymentDetailsTitle = MutableLiveData<String>()
  var paymentDetailsSubTotalTitle = MutableLiveData<String>()

  var amountWithoutDiscount = MutableLiveData<Double>()
  var vatPrices = MutableLiveData<Double>()
  var codCharges = MutableLiveData<Double>()
  var outOfStockCartItem = MutableLiveData<CartModel?>()
  var price = MutableLiveData<Price?>()
  var showLaodingOrderProgressBar = MutableLiveData<Boolean>()

  var orderId: Int = 0
  var orderDisplayId: String = ""
  fun doAmountCalculations() {

    calculateCompleteShipmentCharges()
  }


  fun setCartModel() {
    outOfStockCartItem.value = CartModel(
      qty = 1,
      productDetails = appManager.storageManagers.selectedOutOfstockProductItem
    )
    price.value =
      appManager.storageManagers.selectedOutOfstockProductItem.getRelativePrice(viewModelContext)
    Logger.d("Price", price.toString())
  }

  fun calculateCompleteShipmentCharges() {
    calculateExpressShipmentCharges()
    val charges: Double = (expressCharge.value ?: 0.0)
    shipmentCharges.value = (charges)
    calculateTotalAmount()

  }

  fun createTransaction(body: TransactionModel) =
    performNwOperation { cartRepository.createTransaction(body) }

  private fun calculateExpressShipmentCharges() {
    val sumGroupsWithDiscount = 0.0
    sumGroupsWithDiscount?.let {
      val difference = it.minus(appManager.storageManagers.config.mINIMUMORDER ?: 100.0)
      if (difference >= 0 || appManager.offersManagers.expressCount.value == 0) {
        expressCharge.value = 0.0
      } else {
        expressCharge.value =
          appManager.storageManagers.config.eXPRESSDELIVERYFEE ?: ConstantsUtil.EXPRESS_DELIVERY_FEE

      }
    }
    Logger.e("ExpressChargers", expressCharge.value.toString())
  }

  fun getTransactionModer(string: Int?): TransactionModel {
    val transactionModel =
      TransactionModel()
    transactionModel.orderId = orderId
    transactionModel.type = "charge"
    if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "new" || selectedPaymentType.value?.name?.toLowerCase(
        Locale.ROOT
      ) == "new_for_wallet"
    ) {
      transactionModel.CardType = "new"
    } else {
      if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "card") {
        transactionModel.CardType = "saved"
        transactionModel.card_id = string
      }

    }
    when (selectedPaymentType.value) {
      PaymentType.NEW, PaymentType.NEW_FOR_WALLET, PaymentType.CARD -> {
        transactionModel.method = "card"
      }
      PaymentType.CASH -> {
        transactionModel.method = "cash"
      }
      PaymentType.WALLET -> {
        transactionModel.method = "wallet"
      }
      else -> {
        transactionModel.method = "cash"
      }
    }

    transactionModel.amount = totalAmount.value ?: 0.0

    return transactionModel
  }

  private fun calculateTotalAmount() {

    if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "cash") {
      codCharges.value = appManager.storageManagers.config.cODCHARGES ?: 0.0
    } else {
      codCharges.value = 0.0
    }

    val vatForOne = outOfStockCartItem.value?.productDetails?.let {
      it.unitPrice?.let { it1 ->
        it.vatPercentageOutOfStock?.let { it2 ->
          CalculationUtil.getVATFromUnitPriceWithGivenVATPercentage(
            unitPrice = it1,
            vatPer = it2
          )
        }
      }
    }
    vatPrices.value =
      vatForOne?.times(outOfStockCartItem.value?.qty?.intToNullSafeDoubleByDefault1() ?: 1.0)
        ?.doubleDigitDouble()
    val charges: Double = shipmentCharges.value ?: 0.0
    amountWithoutDiscount.value =
      outOfStockCartItem.value?.productDetails?.unitPrice?.times(
        outOfStockCartItem.value?.qty ?: 1
      )

    outOfStockCartItem.value?.cartGrossTotal = amountWithoutDiscount.value
    totalAmount.value = amountWithoutDiscount.value?.plus(charges)?.plus(vatPrices.value ?: 0.0)
      ?.plus(codCharges.value ?: 1.0)

    checkWalletIsValid()
  }

  private fun checkWalletIsValid() {
    Logger.d("Wallet", appManager.persistenceManager.getLoggedInUser()?.walletBalance.toString())
    totalAmount.value?.let { Logger.d("Total", it.toString()) }
    if (appManager.persistenceManager.getLoggedInUser()?.walletBalance != null) {
      isWalletEnable.value =
        (totalAmount.value?.doubleDigitDouble()
          ?: 0.0) <= (appManager.persistenceManager.getLoggedInUser()?.walletBalance?.doubleDigitDouble()
          ?: 0.0)
      if (totalAmount.value ?: 0.0 > appManager.persistenceManager.getLoggedInUser()?.walletBalance ?: 0.0 && selectedPaymentType.value?.name?.toLowerCase(
          Locale.ROOT
        ) == "wallet"
      ) {
        selectedPaymentType.value = PaymentType.WALLET_WITH_LESS
        val userWallet = appManager.persistenceManager.getLoggedInUser()?.walletBalance ?: 0.0
        val totalAmountLocal = totalAmount.value ?: 0.0
        val temDiff = totalAmountLocal.minus(userWallet)
        Logger.d("difference", temDiff.toString())
        walletDifference.value = temDiff.doubleDigitDouble()

      }
    } else {
      isWalletEnable.value = false
    }

  }

  fun checkAndUpdateAccordingTopPreviousPaymentTyp() {
    if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "new") {
      selectedPaymentType.value = PaymentType.CARD
    }
    if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "new_for_wallet" || selectedPaymentType.value?.name?.toLowerCase(
        Locale.ROOT
      ) == "wallet_with_less"
    ) {
      selectedPaymentType.value = PaymentType.WALLET_WITH_LESS
    }
  }

  fun plus() {
    val temp = outOfStockCartItem.value
    temp?.qty = temp?.qty?.plus(1) ?: 1
    outOfStockCartItem.value = temp
    doAmountCalculations()
  }

  fun minus() {
    val temp = outOfStockCartItem.value
    temp?.qty = temp?.qty?.minus(1) ?: 1
    outOfStockCartItem.value = temp
    doAmountCalculations()
  }

  fun setWalletDifference(total: Double?) {
    val userWallet = appManager.persistenceManager.getLoggedInUser()?.walletBalance ?: 0.0
    val totalAmountLocal = total ?: 0.0
    val temDiff = totalAmountLocal.minus(userWallet)
    Logger.d("difference", temDiff.toString())
    walletDifference.value = temDiff.doubleDigitDouble()
  }

  fun creatOutOfStockOrder(body: OutOfStockRequestBody) =
    performNwOperation { cartRepository.createOutOfStockOrder(body) }


  fun makeOutOfStockRequestBody(addressId: Int): OutOfStockRequestBody {
    outOfStockCartItem.value?.let { }
    val listItems = ArrayList<Item>()
    val item = Item(
      grossLineTotal = outOfStockCartItem.value?.let {
        CalculationUtil.getGrossLineWithServerUnitPriceTotal(
          it,
        )
      },
      id = outOfStockCartItem.value?.let { it.productDetails.id },
      lineTotal = outOfStockCartItem.value?.let {
        CalculationUtil.getLineTotal(
          it,
          viewModelContext
        )
      },
      netLineTotal = outOfStockCartItem.value?.let {
        CalculationUtil.getNetLineOutOfStockTotal(
          it,
          viewModelContext
        )
      },
      qty = outOfStockCartItem.value?.qty,
      unitPrice = outOfStockCartItem.value?.let {
        it.productDetails.unitPrice
      },
      vat = vatPrices.value
    )
    listItems.add(item)
    return OutOfStockRequestBody(
      addressId = addressId,
      channel = "android",
      discount = 0,
      items = listItems,
      netVat = vatPrices.value,
      subTotal = amountWithoutDiscount.value,
      total = totalAmount.value
    )
  }

}