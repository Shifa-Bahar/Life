package com.lifepharmacy.application.ui.cart.viewmodel

import android.app.Application
import android.util.Log
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.managers.OffersManagers
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.cart.*
import com.lifepharmacy.application.model.config.DeliverySlot
import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.PlaceOrderRequest
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.CartRepository
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.utils.CalculationUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.CreditCardFormatTextWatcher
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Extensions.doubleDigitDouble
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeInt
import com.lifepharmacy.application.utils.universal.InputEditTextValidator
import com.lifepharmacy.application.utils.universal.Logger
import java.util.*

/**
 * Created by Zahid Ali
 */
class CartViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val cartRepository: CartRepository,
  val offersManagers: OffersManagers,
  application: Application
) : BaseViewModel(application) {

  var couponText: InputEditTextValidator =
    InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      null
    )
  var note: InputEditTextValidator =
    InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      null
    )
  var deliveryins: ArrayList<DeliveryInstructions> = ArrayList()
  var isUpdatingCart = MutableLiveData<Boolean>()
  var showLaodingOrderProgressBar = MutableLiveData<Boolean>()
  var selectedInstantDeliverySlot = MutableLiveData<DeliverySlot?>()
  var selectedStandardDeliverySlot = MutableLiveData<DeliverySlot?>()
  var selectedCard: CardMainModel? = null
  var isInstantChecked = MutableLiveData<Boolean>()
  var isManualInstantChecked = MutableLiveData<Boolean>()
  var isLeaveAtMyDoor = MutableLiveData<Boolean>()
  var isSavecard: Boolean = true
  var paymentview: Boolean = false
  var isOrderTotalGreaterThanMinimumOrder = MutableLiveData<Boolean>()
  lateinit var ccdata: CreditCardFormatTextWatcher

  var isThereCart = MutableLiveData<Boolean>()
  var isCouponApplied = MutableLiveData<Boolean>()
  var couponModel = MutableLiveData<CouponModel>()
  var sunOfGroupsAmountWithoutDiscount = MutableLiveData<Double>()
  var isAnyOutOfStock = MutableLiveData<Boolean>()
  var isAnyFreeGift = MutableLiveData<Boolean>()
  var isCartChanged = MutableLiveData<Boolean>()
  var discountAmount = MutableLiveData<Double>()
  var offerTotal = MutableLiveData<Double>()
  var offerTotalTaxable = ObservableField<String>()
//  var expressTiming = MutableLiveData<String>()

  var totalAmount = MutableLiveData<Double>()
  var vatPrices = MutableLiveData<Double>()
  var codCharges = MutableLiveData<Double>()

  var instantCharge = MutableLiveData<Double>()
  var standardCharge = MutableLiveData<Double>()
  var paymentDetailsTitle = MutableLiveData<String>()
  var paymentDetailsSubTotalTitle = MutableLiveData<String>()
  var freeGiftMessage = MutableLiveData<FreeGiftMessage>()

  //  var instantCount = ObservableField<Double>()
//  var instantOnlyCount = ObservableField<Double>()
//  var expressCount = ObservableField<Double>()
  var shipmentCharges = MutableLiveData<Double>()
  var card_pg: Int? = 1
  var selectedPaymentType = MutableLiveData<PaymentType>()
  var isWalletEnable = MutableLiveData<Boolean>()
  var orderId: Int = 0
  var orderIdpcr: Int = 0
  var sumtotal = appManager.persistenceManager.totalwithoutadditonal
  var add = appManager.persistenceManager.additional_charges_amount
  var totalAmountPcr = sumtotal.plus(add)
  var token: String = ""
  var card4dig: String = ""

  //  var walletAmount: Double = 0.0
  var orderDisplayId: String = ""
  var selectedtypenew: Boolean = false
  var selectedtypenewwallet: Boolean = false
  var cardno: String = ""
  var cardNumber: String = ""
  var cardNospace: String = ""
  var cardend: String = ""
  var cardScheme: String = ""
  var cardType: String = ""
  var cardYear: String = ""
  var completeYear: String = ""
  var cardMonth: String = ""
  var cardCvv: String = ""
  var cardName: String = ""

  var placeOrderItems: ArrayList<OrderItem> = ArrayList()
  var deliveryInstructions: ArrayList<String> = ArrayList()

  var shipmentSelected: Int = 1

  var walletDifference = MutableLiveData<Double>()
  var walletDifferencepcr = MutableLiveData<Double>()

  fun updateCart() =
    performNwOperation { cartRepository.updateCart(makeCreateCartModel()) }

  fun createCart() =
    performNwOperation { cartRepository.createCart(makeCreateCartModel()) }

  fun createOrder(body: PlaceOrderRequest) =
    performNwOperation { cartRepository.createOrder(body) }


  fun setStandardSlot(deliverySlot: DeliverySlot) {
    selectedStandardDeliverySlot.value = deliverySlot
    doAmountCalculations(appManager.persistenceManager.getLoggedInUser())
  }

  fun setInstantSlot(deliverySlot: DeliverySlot) {
    selectedInstantDeliverySlot.value = deliverySlot
    doAmountCalculations(appManager.persistenceManager.getLoggedInUser())
  }


  fun Bindpaymenttype() {
    Log.e(TAG, "Bindpaymenttype:${selectedPaymentType.value} ")
    when (selectedPaymentType.value) {
      PaymentType.CARD -> {
        cardend = card4dig
        cardScheme = selectedCard?.cardData?.paymentInfo?.cardScheme.toString()
      }
      PaymentType.CASH -> {
        cardend = "CASH"
      }
      PaymentType.NEW -> {
        if (appManager.persistenceManager.defaultpg != 2) {
          if (!cardNumber.isNullOrEmpty()) {
            cardNospace = cardNumber.replace(" ", "")
            if (cardNospace.length > 16) {
              cardend = if (cardNospace.length > 16)
                cardNospace.subSequence(12, 16).toString() + cardNospace.substring(16) else
                cardNospace.substring(0, 16).toUpperCase()
            } else {
              cardend = "NEW CARD"
            }
          } else {
            cardend = "NEW CARD"
          }
        } else {
          cardend = "NEW CARD"
        }

      }
      PaymentType.WALLET -> {
        cardend = "wallet"
      }

    }
//    Log.e(TAG, "Bindpaymenttype: ${selectedPaymentType.value}")
//    if (selectedPaymentType.value == PaymentType.NEW) {
//      var cardnum = cardNumber.replace(" ", "")
//      if (!cardno.isNullOrEmpty() && cardno.length == 16) {
//        cardno = cardnum
//        cardend = cardno.subSequence(12, 16).toString()
//      } else {
//        cardend = "New Card"
//      }
//    } else if (selectedPaymentType.value.toString().toLowerCase() == "card") {
//      var cardnum = selectedCard?.cardData?.paymentInfo?.paymentDescription.toString()
//      Log.e(TAG, "Bindpaymenttype: $cardnum")
//      if (!cardnum.isNullOrEmpty() && cardnum.length == 16) {
//        cardno = cardnum
//        cardend = cardnum.subSequence(12, 16).toString()
//      }
//    } else if (selectedPaymentType.value == PaymentType.CASH) {
//      cardend = "CASH"
//    } else if (selectedPaymentType.value == PaymentType.WALLET || selectedPaymentType.value == PaymentType.WALLET_WITH_LESS) {
//      cardend = "Wallet"
//    }
  }

  fun bindDefaultPaymentMethod() {
    if (appManager.storageManagers.config.isWalletEnabled == true) {
      selectedPaymentType.value = PaymentType.WALLET
    }
    if (appManager.storageManagers.config.isCodeEnabled == true) {
      selectedPaymentType.value = PaymentType.CASH
    }
    if (appManager.storageManagers.config.isCardEnabled == true) {
      selectedPaymentType.value = PaymentType.CARD
    }
  }

  fun getTransactionModers(string: Int?): TransactionModel {
    val transactionModel = TransactionModel()
    transactionModel.orderId = orderIdpcr
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
    }

    transactionModel.amount =
      (appManager.persistenceManager.totalwithoutadditonal).plus(appManager.persistenceManager.additional_charges_amount)
        .currencyFormat().toDouble()
//      String.format("%.1f", ((appManager.persistenceManager.totalamount))).toDouble() ?: 0.0

    return transactionModel
  }

  fun checkWalletIsValidPcr(user: User?) {
    var total =
      appManager.persistenceManager.totalwithoutadditonal.plus(appManager.persistenceManager.additional_charges_amount)
        .currencyFormat().toDouble()
    totalAmountPcr = total
    Log.e(TAG, "checkWalletIsValidPcr:ssstotal $total")
    if (user?.walletBalance != null) {
      isWalletEnable.value = true
//        (total?.doubleDigitDouble() ?: 0.0) <= (user.walletBalance?.doubleDigitDouble()
//          ?: 0.0)
      if (total ?: 0.0 > user.walletBalance ?: 0.0 && selectedPaymentType.value?.name?.toLowerCase(
          Locale.ROOT
        ) == "wallet"
      ) {
        selectedPaymentType.value = PaymentType.WALLET_WITH_LESS
        val userWallet = user.walletBalance ?: 0.0
        val totalAmountLocal = total
        val temDiff = total.minus(userWallet)
        Log.e(TAG, "checkWalletIsValid: temDiff $temDiff")
        walletDifference.value = temDiff.doubleDigitDouble()
        walletDifferencepcr.value = temDiff.doubleDigitDouble()
      }
    } else {
      isWalletEnable.value = false
    }

  }

  //  fun checkWalletIsValidPcr11(user: User?) {
//    Logger.d("NewwwwwcheckWallet", appManager.persistenceManager.getLoggedInUser()?.walletBalance.toString())
//
//    totalAmountPcr.let {
//      Logger.d("Total", it.toString())
//    }
//
//    if (appManager.persistenceManager.getLoggedInUser()?.walletBalance != null) {
//      isWalletEnable.value =
//        (totalAmountPcr.doubleDigitDouble()?: 0.0) <= (appManager.persistenceManager.getLoggedInUser()?.walletBalance?.doubleDigitDouble()
//          ?: 0.0)
//      if (totalAmountPcr ?: 0.0 > appManager.persistenceManager.getLoggedInUser()?.walletBalance ?: 0.0 && selectedPaymentType.value?.name?.toLowerCase(
//          Locale.ROOT
//        ) == "wallet"
//      ) {
//        selectedPaymentType.value = PaymentType.WALLET_WITH_LESS
//        val userWallet = appManager.persistenceManager.getLoggedInUser()?.walletBalance ?: 0.0
//        val totalAmountLocal = totalAmountPcr ?: 0.0
//        val temDiff = totalAmountLocal.minus(userWallet)
//        Logger.d("difference", temDiff.toString())
//        walletDifference.value = temDiff.doubleDigitDouble()
//      }
//    } else {
//      isWalletEnable.value = false
//    }
//
//  }
  fun setLastPaymentType() {
    if (appManager.persistenceManager.isPaymentMethod()) {
      when (appManager.persistenceManager.getLastPayment()) {
        "card" -> {
          if (appManager.storageManagers.config.isCardEnabled == true) {
            selectedPaymentType.value = PaymentType.CARD
          }
        }
        "cash" -> {
          if (appManager.storageManagers.config.isCodeEnabled == true) {
            selectedPaymentType.value = PaymentType.CASH
          }
        }
        "wallet" -> {
          if (appManager.storageManagers.config.isWalletEnabled == true) {
            selectedPaymentType.value = PaymentType.WALLET
          }
        }
      }
    } else {
      Log.e(TAG, "setLastPaymentType: 5$selectedtypenew")
      if (selectedtypenew && selectedtypenewwallet) {
        selectedPaymentType.value = PaymentType.NEW_FOR_WALLET
      } else if (selectedtypenew) {
        selectedPaymentType.value = PaymentType.NEW
      } else {
        selectedPaymentType.value = PaymentType.CARD
      }
    }
  }

  fun getdeliveryins(deliveryselected: ArrayList<DeliveryInstructions>?): ArrayList<String> {

    var productsItems: ArrayList<String> = ArrayList()
    deliveryselected?.forEach { deliveryselect ->
      productsItems?.add(deliveryselect.instruction.toString())
    }
    return productsItems
  }

  //  placeOrder.isLeaveAtMyDoor = isLeaveAtMyDoor.value ?: false
  fun getPlaceOrderModel(addressId: Int): PlaceOrderRequest {
    makeCartToPlaceOrderItem()
    deliveryInstructions = getdeliveryins(appManager.storageManagers.deliveryinsselected)
    Log.e(TAG, "getPlaceOrderModel: $deliveryInstructions")
    val placeOrder = PlaceOrderRequest()
    placeOrder.addressId = addressId
    placeOrder.discount = discountAmount.value?.doubleDigitDouble() ?: 0.0
    placeOrder.items = placeOrderItems
    placeOrder.total = totalAmount.value?.doubleDigitDouble() ?: 0.0
    placeOrder.isInstantRequested = isInstantChecked.value ?: false
    placeOrder.instantNotRequested = isManualInstantChecked.value ?: false
    placeOrder.deliveryinstructions = deliveryInstructions
    placeOrder.subTotal = sunOfGroupsAmountWithoutDiscount.value?.doubleDigitDouble() ?: 0.0
    placeOrder.deliveryFees = shipmentCharges.value?.doubleDigitDouble() ?: 0.0
    placeOrder.vat = vatPrices.value?.doubleDigitDouble() ?: 0.0
    if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "cash") {
      placeOrder.codCharge = codCharges.value?.doubleDigitDouble() ?: 0.0
    }
    placeOrder.cartId = appManager.persistenceManager.getCartID()

    couponModel.value?.let {
      placeOrder.coupon = it.couponCode
    }

    placeOrder.notes = note.getValue()
    placeOrder.instantSlotId = selectedInstantDeliverySlot.value?.slotId
    placeOrder.standardSlotID = selectedStandardDeliverySlot.value?.slotId
    return placeOrder
  }

  fun createTransaction(body: TransactionModel) =
    if (appManager.persistenceManager.defaultpg == 2 &&
      selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "new"
    ) {
      performNwOperation { cartRepository.createTransactionNew(body) }
    } else if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "card" && card_pg == 2) {
      performNwOperation { cartRepository.createTransactionNew(body) }
    } else {
      Log.e(TAG, "createTransaction: ${selectedPaymentType.value}", )
      performNwOperation { cartRepository.createTransaction(body) }
    }


  fun validateCoupon() =
    performNwOperation { cartRepository.validateCoupon(makeCouponValidateBody()) }

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
    if (appManager.persistenceManager.defaultpg == 2 && token != "" &&
      selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "new"
    ) {
      transactionModel.purpose = "order_creation"
      transactionModel.token = token
      transactionModel.save_card = isSavecard
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
    }

    transactionModel.amount = totalAmount.value ?: 0.0

    return transactionModel
  }


  private fun calculateDiscountTotal() {
//    var amount = calculateRegularTotal() - calculateOffersAmount()
    var amount = offersManagers.getSumOfGroupsDiscountedAmount()
    couponModel.value?.let {
      amount += it.couponValue ?: 0.0
    }
    var discountTotal = 0.0
    offersManagers.freeGiftArray?.let {
      val discountsInDoubleArray = it.map { item ->
        (CalculationUtil.getUnitPrice(item.productDetails, viewModelContext)
          .times(item.qty))
      }
      if (!discountsInDoubleArray.isNullOrEmpty()) {
        discountTotal = discountsInDoubleArray.reduce { acc, d -> acc + d }
      }

      amount = amount.plus(discountTotal)
    }
    discountAmount.value = (amount)
  }

  private fun calculateOffersAmount(): Double {
    var offerDiscount = 0.0
    offersManagers.freeGiftArray?.let {
      val discountsInDoubleArray = it.map { item ->
        (CalculationUtil.getUnitPrice(item.productDetails, viewModelContext)
          .times(item.qty))
      }
      if (!discountsInDoubleArray.isNullOrEmpty()) {
        offerDiscount = discountsInDoubleArray.reduce { acc, d -> acc + d }
      }

    }
    val amount: Double =
      offersManagers.getSumOfGroupsAmountWithDiscount() ?: 0.0
    offerTotal.value = (amount)
    return amount
  }

  private fun calculateOffersForTaxable(): Double {
    val amount: Double = offersManagers.getSumOfGroupsVAT() ?: 0.0
    offerTotalTaxable.set(offersManagers.getSumOfGroupsVAT().currencyFormat())
    return amount
  }

  private fun calculateRegularTotal(): Double {

    var subTotalWithOutDiscountGifts = 0.0
    offersManagers.freeGiftArray?.let {
      val discountsInDoubleArray = it.map { item ->
        (CalculationUtil.getUnitPrice(item.productDetails, viewModelContext)
          .times(item.qty))
      }
      if (!discountsInDoubleArray.isNullOrEmpty()) {
        subTotalWithOutDiscountGifts = discountsInDoubleArray.reduce { acc, d -> acc + d }
      }

    }
    val amount: Double =
      offersManagers.getSumOfGroupsAmountWithoutDiscount().plus(subTotalWithOutDiscountGifts) ?: 0.0
    sunOfGroupsAmountWithoutDiscount.value = (amount)
    return amount
  }

  fun doAmountCalculations(user: User? = null, isFromCart: Boolean = false) {
    calculateOffersAmount()
    calculateRegularTotal()
    calculateDiscountTotal()
    calculateVAT()
    calculateOffersForTaxable()
    calculateCompleteShipmentCharges(user, isFromCart)
  }

  fun doAmountCalculationsPcr(user: User? = null, isFromCart: Boolean = false) {
    calculateOffersAmount()
    calculateRegularTotal()
    calculateDiscountTotal()
    calculateVAT()
    calculateOffersForTaxable()
    calculateCompleteShipmentChargesPcr(user, isFromCart)
  }

  private fun makeCreateCartModel(): CreateCartRequest {
    val createCartRequest = CreateCartRequest()
    val items = ArrayList<CartItemRequestModel>()
    offersManagers.getAllProductsWithQTY().let { it ->
      items.addAll(it.map { cartModel ->
        CartItemRequestModel(
          id = cartModel.productDetails.id,
          qty = cartModel.qty,
          offerId = cartModel.productDetails.offers?.id
        )
      })
    }
    offersManagers.outOfProductsArray.let {
      items.addAll(it.map { cartModel ->
        CartItemRequestModel(
          id = cartModel.productDetails.id,
          qty = cartModel.qty,
          offerId = cartModel.productDetails.offers?.id
        )
      })

//      for (item in it) {
//        items.add(
//          CartItemRequestModel(
//            id = item.productDetails.id,
//            qty = item.addedQTY,
//            offerId = item.productDetails.offers?.id
//          )
//        )
//      }
    }
    if (appManager.persistenceManager.isThereCart()) {
      createCartRequest.cartID = appManager.persistenceManager.getCartID()
    }
    if (couponText.getValue().isNotEmpty()) {
      createCartRequest.couponCode = couponText.getValue()
    }
    if (appManager.persistenceManager.isLoggedIn()) {
      createCartRequest.userID = appManager.persistenceManager.getLoggedInUser()?.id.toString()
    }
    createCartRequest.deviceID = appManager.persistenceManager.getFCMToken()

    createCartRequest.items = items
    createCartRequest.cartTotal = totalAmount.value?.doubleDigitDouble()
    return createCartRequest
  }

  private fun makeCartToPlaceOrderItem() {
    placeOrderItems.clear()
    offersManagers.getAllProductsWithQTY().let {
      for (item in it) {
//        Logger.d("ProuductBundleDisocunt", "${item.discountedAmount}")
        //        if (cartManager.isItemChecked(item)) {
        val placeOrderItem = OrderItem()
        placeOrderItem.discount = item.discountedAmount ?: 0.0
        placeOrderItem.id = item.productDetails.id
        placeOrderItem.grossLineTotal = item.cartGrossTotal ?: 0.0
        placeOrderItem.lineTotal = CalculationUtil.getLineTotal(item, viewModelContext)
        placeOrderItem.vat = item.cartVAT?.doubleDigitDouble() ?: 0.0
        placeOrderItem.unitPrice =
          CalculationUtil.getUnitPrice(item.productDetails, viewModelContext)
        placeOrderItem.netLineTotal = CalculationUtil.getNetLineTotal(item, viewModelContext)
        placeOrderItem.qty = item.qty
        placeOrderItems.add(placeOrderItem)
        //        }
      }
    }
    offersManagers.freeGiftArray.let {
      val freeGiftsPlaceOrderRequestArray = it.map { item ->
        OrderItem(
          discount = (CalculationUtil.getUnitPrice(item.productDetails, viewModelContext)
            .times(item.qty)),
          id = item.productDetails.id,
          grossLineTotal = (CalculationUtil.getUnitPrice(item.productDetails, viewModelContext)
            .times(item.qty)),
          lineTotal = CalculationUtil.getLineTotal(item, viewModelContext),
          vat = 0.0,
          unitPrice = CalculationUtil.getUnitPrice(item.productDetails, viewModelContext),
          netLineTotal = 0.0,
          qty = item.qty

        )
      }
      placeOrderItems.addAll(freeGiftsPlaceOrderRequestArray)
    }
  }

  private fun makeCouponValidateBody(): ValidateCouponRequestBody {
    return ValidateCouponRequestBody(
      cartId = appManager.persistenceManager.getCartID().stringToNullSafeInt(),
      couponCode = couponText.getValue(),
      cartTotal = offerTotal.value ?: 0.0
    )
  }

  private fun calculateExpressShipmentCharges() {
    val sumGroupsWithDiscount = offerTotal.value?.plus(offersManagers.getSumOfGroupsVAT())
    var chargerForMinimumOrder = 0.0

    sumGroupsWithDiscount?.let {
      val difference = it.minus(appManager.storageManagers.config.mINIMUMORDER ?: 100.0)
      chargerForMinimumOrder =
        if (difference >= 0 || appManager.offersManagers.expressCount.value == 0) {
          0.0
        } else {
          appManager.storageManagers.config.eXPRESSDELIVERYFEE ?: ConstantsUtil.EXPRESS_DELIVERY_FEE
        }

      standardCharge.value =
        chargerForMinimumOrder.plus(selectedStandardDeliverySlot.value?.fees ?: 0.0)
    }
    Logger.e("ExpressChargers", standardCharge.value.toString())
  }


  fun calculateCompleteShipmentCharges(user: User?, isFromCart: Boolean = false) {
    calculateExpressShipmentCharges()
    val charges: Double =
      if (isInstantChecked.value == true || offersManagers.instantOnlyCountMut.value ?: 0 > 0) {
        Logger.e("ExpressChargers", standardCharge.value.toString())
        Logger.e("Instant", instantCharge.value.toString())
//        val tempCharges = (expressCharge.value ?: 0.0).plus(instantCharge.value ?: 0.0)
//        Logger.e("tempChargessChargers", tempCharges.toString())
        (standardCharge.value ?: 0.0).plus(selectedInstantDeliverySlot.value?.fees ?: 0.0)
      } else {
        (standardCharge.value ?: 0.0)
      }

    shipmentCharges.value = (charges)
    calculateTotalAmount(user, isFromCart)

  }

  fun calculateCompleteShipmentChargesPcr(user: User?, isFromCart: Boolean = false) {
    calculateExpressShipmentCharges()
    val charges: Double =
      if (isInstantChecked.value == true || offersManagers.instantOnlyCountMut.value ?: 0 > 0) {
        Logger.e("ExpressChargers", standardCharge.value.toString())
        Logger.e("Instant", instantCharge.value.toString())
//        val tempCharges = (expressCharge.value ?: 0.0).plus(instantCharge.value ?: 0.0)
//        Logger.e("tempChargessChargers", tempCharges.toString())
        (standardCharge.value ?: 0.0).plus(selectedInstantDeliverySlot.value?.fees ?: 0.0)
      } else {
        (standardCharge.value ?: 0.0)
      }

    shipmentCharges.value = (charges)
    calculateTotalAmountPcr(user, isFromCart)

  }

  private fun calculateTotalAmountPcr(user: User?, isFromCart: Boolean = false) {
    calculateVAT()
    var charges: Double = 0.0
    if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "cash") {

      if (isFromCart == false) {
        charges = shipmentCharges.value ?: 0.0
          .plus(appManager.storageManagers.config.cODCHARGES ?: ConstantsUtil.COD_CHARGES)
      }
      couponModel.value?.let {
        charges -= it.couponValue ?: 0.0
      }
    } else {

      if (isFromCart == false) {
        charges = shipmentCharges.value ?: 0.0
      }
      couponModel.value?.let {
        charges -= it.couponValue ?: 0.0
      }

    }
    val totalAmountDouble = totalAmountPcr
    checkWalletIsValidPcr(user)
    checkMinimumOrder()
  }


  private fun calculateTotalAmount(user: User?, isFromCart: Boolean = false) {
    calculateVAT()
    var charges: Double = 0.0
    if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "cash") {

      if (isFromCart == false) {
        charges = shipmentCharges.value ?: 0.0
          .plus(appManager.storageManagers.config.cODCHARGES ?: ConstantsUtil.COD_CHARGES)
      }
      couponModel.value?.let {
        charges -= it.couponValue ?: 0.0
      }
    } else {

      if (isFromCart == false) {
        charges = shipmentCharges.value ?: 0.0
      }
      couponModel.value?.let {
        charges -= it.couponValue ?: 0.0
      }

    }
    val totalAmountDouble =
      offersManagers.getSumOfGroupsAmountWithDiscount() + charges.plus(vatPrices.value ?: 0.0)
    totalAmount.value = (totalAmountDouble)
    checkWalletIsValid(user)
    checkMinimumOrder()
  }

  private fun checkWalletIsValid(user: User?) {
    Logger.d("Wallet", user?.walletBalance.toString())
    totalAmount.value?.let { Logger.d("Total", it.toString()) }
    if (user?.walletBalance != null) {
      isWalletEnable.value =
        (totalAmount.value?.doubleDigitDouble() ?: 0.0) <= (user.walletBalance?.doubleDigitDouble()
          ?: 0.0)
      if (totalAmount.value ?: 0.0 > user.walletBalance ?: 0.0 && selectedPaymentType.value?.name?.toLowerCase(
          Locale.ROOT
        ) == "wallet"
      ) {
        selectedPaymentType.value = PaymentType.WALLET_WITH_LESS
        val userWallet = user.walletBalance ?: 0.0
        val totalAmountLocal = totalAmount.value ?: 0.0
        val temDiff = totalAmountLocal.minus(userWallet)
        Logger.d("difference", temDiff.toString())
        walletDifference.value = temDiff.doubleDigitDouble()

      }
    } else {
      isWalletEnable.value = false
    }

  }

  private fun checkMinimumOrder() {
    isOrderTotalGreaterThanMinimumOrder.value =
      totalAmount.value ?: 0.0 >= appManager.storageManagers.config.minimumOrderValue ?: 1.0
  }

  fun setWalletDifference(total: Double?) {
    val userWallet = appManager.persistenceManager.getLoggedInUser()?.walletBalance ?: 0.0
    val totalAmountLocal = total ?: 0.0
    val temDiff = totalAmountLocal.minus(userWallet)
    Logger.d("shiffaa  --difference", temDiff.toString())
    walletDifference.value = temDiff.doubleDigitDouble()
  }

  private fun calculateVAT() {
    val charges = offersManagers.getSumOfGroupsVAT()
    vatPrices.value = (charges)
  }
//
//  fun setExpressDeliveryTime() {
//    if (DateTimeUtil.isDeliverCanBeToday(
//        appManager.storageManagers.config.sameDayEndTime?.roundToInt() ?: 14
//      ) && appManager.storageManagers.config.sameDayDeliveryZone?.polygons?.let {
//        GoogleUtils.isUserInGivePolyGoneList(
//          it
//        )
//      } == true
//    ) {
//      expressTiming.value = (viewModelContext.getString(R.string.today))
//    } else {
//      expressTiming.value = (viewModelContext.getString(R.string.tomorrow))
//    }
//  }

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

  fun setInstantDefaultSelectedSlot(): DeliverySlot? {
    var defaultSelectedSlot: DeliverySlot? = null
    var isAnySelected: Boolean = false
    val anySelected = appManager.storageManagers.instantDeliverySlots.firstOrNull {
      it.selected == true
    }
    val firstActive =
      appManager.storageManagers.instantDeliverySlots.firstOrNull { it.isActive == true }
    isAnySelected = anySelected != null
    if (isAnySelected) {
      for (item in appManager.storageManagers.instantDeliverySlots) {
        if (item.selected == true && item.isActive == true)
          defaultSelectedSlot = item
      }

    } else {
      for (item in appManager.storageManagers.instantDeliverySlots) {
        if (firstActive == item) {
          defaultSelectedSlot = item
        }
      }
    }
    defaultSelectedSlot?.let { setInstantSlot(it) }
    return defaultSelectedSlot
  }

  fun setStandardDefaultSelectedSlot(): DeliverySlot? {
    var defaultSelectedSlot: DeliverySlot? = null
    var isAnySelected: Boolean = false
    val anySelected = appManager.storageManagers.standardDeliverySlots.firstOrNull {
      it.selected == true
    }
    val firstActive =
      appManager.storageManagers.standardDeliverySlots.firstOrNull { it.isActive == true }
    isAnySelected = anySelected != null
    if (isAnySelected) {
      for (item in appManager.storageManagers.standardDeliverySlots) {
        if (item.selected == true && item.isActive == true)
          defaultSelectedSlot = item
      }

    } else {
      for (item in appManager.storageManagers.standardDeliverySlots) {
        if (firstActive == item) {
          defaultSelectedSlot = item
        }
      }
    }
    defaultSelectedSlot?.let { setStandardSlot(it) }
    return defaultSelectedSlot
  }
}