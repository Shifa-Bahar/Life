package com.lifepharmacy.application.ui.pcr.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.enums.ProductDetailSelectedState
import com.lifepharmacy.application.enums.ScrollingState
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.ui.utils.AppScrollState
import com.lifepharmacy.application.managers.FiltersManager
import com.lifepharmacy.application.managers.OffersManagers
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.category.Section
import com.lifepharmacy.application.model.filters.FilterModel
import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.PlaceOrderRequest
import com.lifepharmacy.application.model.orders.outofstock.OutOfStockRequestBody
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Address
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Appointment_details
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Order
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Payment_details
import com.lifepharmacy.application.model.pcr.pcrlist.Prices
import com.lifepharmacy.application.model.pcr.pcrlist.Products
import com.lifepharmacy.application.model.pcr.pcrrequestmodel.Items
import com.lifepharmacy.application.model.pcr.pcrrequestmodel.PlacePcrOrderRequest
import com.lifepharmacy.application.model.review.Product
import com.lifepharmacy.application.repository.PcrListingRepository
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.utils.CalculationUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Extensions.doubleDigitDouble
import com.lifepharmacy.application.utils.universal.Logger
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Zahid Ali
 */
class PcrListingViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: PcrListingRepository,
//  val cartUtil: CartManager,
  public val filtersManager: FiltersManager,
  val offersManagers: OffersManagers,
  application: Application, private val scrollState: AppScrollState
) : BaseViewModel(application) {

  var titleString: String? = ""
  var colid: String? = ""
  var colidfromlisttodate: String? = ""
  var colidfromdatetopay: String? = ""
  var colidfrompaytoproceed: String? = ""
  var appoinmentid: Int? = null
  var collection_id = appManager.persistenceManager.collection_id

  //appoinment details
  var status: String? = ""
  var appoinmentorderid: String? = ""
  var paymentdetails: Payment_details? = null
  var orderdetails: Order? = null
  var appaddress: Address? = null
  var appointmentDetails: Appointment_details? = null
  var Products: Products? = null

  //  var id = MutableLiveData<String>()
  var type: String? = null
  var listing_type: String? = null
  var slug: String? = null
  var imageUrl: String? = ""
  var isSearch: Boolean = false
  var selectedDetails = MutableLiveData<ProductDetailSelectedState>()
  var previewProductMut = MutableLiveData<Products>()
  var position: Int = 0
  var orderId: Int = 0
  var vatAmount: Double = 0.0
  var distotal: Double = 0.0
  var previewPrice = MutableLiveData<Prices>()
  var previewQTY = MutableLiveData<String>()
  var selectedFilteredItems = ArrayList<FilterModel>()
  var isGirdSelected = ObservableField<Boolean>()
  var isSubOption = MutableLiveData<Boolean>()
  var isFilterApplied = MutableLiveData<Boolean>()
  var isRangeSelected = MutableLiveData<Boolean>()
  var isMainOptions = MutableLiveData<Boolean>()
  var title = MutableLiveData<String>()
  var image = MutableLiveData<String>()
  var rangeFrom = MutableLiveData<String>()
  var rangeTo = MutableLiveData<String>()
  var isSubClicked = false
  var placeOrderItems: ArrayList<Items> = ArrayList()
  var isList = ObservableField<Boolean>()
  var isThereFilter = ObservableField<Boolean>()
  var parentSelectedQuickSection = Section()
  var vatPrices = MutableLiveData<Double>()
  var sumtotal = appManager.persistenceManager.totalwithoutadditonal
  var add = appManager.persistenceManager.additional_charges_amount
  var totalAmount = sumtotal.plus(add).toDouble()

  //  = MutableLiveData<Double>()
  var discountAmount = MutableLiveData<Double>()
  var isWalletEnable = MutableLiveData<Boolean>()
  var walletDifference = MutableLiveData<Double>()


  var selectedPaymentType = MutableLiveData<PaymentType>()
  var skip = 0
  var take = 30
  var sunOfGroupsAmountWithoutDiscount = MutableLiveData<Double>()
  fun getScrollStateRepo(): AppScrollState {
    return scrollState
  }

  fun checkWalletIsValid(user: User?) {
    var total =
      appManager.persistenceManager.totalwithoutadditonal.plus(appManager.persistenceManager.additional_charges_amount)
        .currencyFormat().toDouble()

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
      }
    } else {
      isWalletEnable.value = false
    }

  }

  fun checkWalletIsValidPcr() {
    Logger.d(
      "Wallet from pcr frag",
      appManager.persistenceManager.getLoggedInUser()?.walletBalance.toString()
    )

    totalAmount.let {
      Logger.d("Total", it.toString())
    }

    if (appManager.persistenceManager.getLoggedInUser()?.walletBalance != null) {
      isWalletEnable.value =
        (totalAmount.doubleDigitDouble()
          ?: 0.0) <= (appManager.persistenceManager.getLoggedInUser()?.walletBalance?.doubleDigitDouble()
          ?: 0.0)
      if (totalAmount ?: 0.0 > appManager.persistenceManager.getLoggedInUser()?.walletBalance ?: 0.0 && selectedPaymentType.value?.name?.toLowerCase(
          Locale.ROOT
        ) == "wallet"
      ) {
        selectedPaymentType.value = PaymentType.WALLET_WITH_LESS
        val userWallet = appManager.persistenceManager.getLoggedInUser()?.walletBalance ?: 0.0
        val totalAmountLocal = totalAmount ?: 0.0
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

  private fun makeCartToPlaceOrderItem() {
    var distotal: Double = 0.0
    var gl: Double = 0.0
    var vatAmount: Double = 0.0
    placeOrderItems.clear()
    appManager.persistenceManager.pcritemselected.let {
      for (item in it) {
        val placeOrderItem = Items()
        gl = (CalculationUtil.getUnitPcrPrice(item, viewModelContext)).times(item.selected_qty)
          .currencyFormat().toDouble()

        if (item.offers != null) {
          distotal =
            ((CalculationUtil.getUnitPcrPrice(item, viewModelContext)).times(item.offers?.value!!)
              .div(100).times(item.selected_qty)).currencyFormat().toDouble()
        } else {
          distotal = 0.0
        }
        if (item.offers != null) {
          vatAmount = ((gl.minus(distotal)).times(item.vat_percentage).div(100))
            .currencyFormat().toDouble()
        } else {
          vatAmount = (gl.times(item.vat_percentage).div(100))
            .currencyFormat().toDouble()
        }
        placeOrderItem.id = item.id
        placeOrderItem.sku = item.inventory.sku.toString()
        placeOrderItem.qty = item.selected_qty
        placeOrderItem.unitPrice = CalculationUtil.getUnitPcrPrice(item, viewModelContext)
        placeOrderItem.grossLineTotal = gl
        placeOrderItem.discount = distotal
        placeOrderItem.lineTotal = gl.minus(distotal)
        placeOrderItem.vat = vatAmount
        placeOrderItem.netLineTotal = (gl.minus(distotal)).plus(vatAmount)
        placeOrderItems.add(placeOrderItem)
      }
    }
//    offersManagers.freeGiftArray.let {
//      val freeGiftsPlaceOrderRequestArray = it.map { item ->
//        Items(
//          discount = (CalculationUtil.getUnitPrice(item.productDetails, viewModelContext)
//            .times(item.qty)),
//          id = item.productDetails.id,
//          grossLineTotal = (CalculationUtil.getUnitPrice(item.productDetails, viewModelContext)
//            .times(item.qty)),
//          lineTotal = CalculationUtil.getLineTotal(item, viewModelContext),
//          vat = 0.0,
//          unitPrice = CalculationUtil.getUnitPrice(item.productDetails, viewModelContext),
//          netLineTotal = 0.0,
//          qty = item.qty
//
//        )
//      }
//      placeOrderItems.addAll(freeGiftsPlaceOrderRequestArray)
//    }
  }

  //  {
//    "date": "2022-01-31",
//    "time": "08:00 - 09:00",
//    "additional_charges_amount": 30,
//    "address_id": 62219,
//    "collection_id;" : "6d4ab037-a059-4588-9f71-494a19a6f473",
//    "items": [
//    {
//      "id": "78f24ca4-9ff2-4a18-a549-2852e089d890",
//      "sku": "999920",
//      "qty": 3,
//      "unit_price": 180,
//      "gross_line_total": 540,
//      "discount": 90,
//      "line_total": 450,
//      "net_line_total": 472.5,
//      "vat": 22.5
//    }
//    ],
//    "discount": 90,
//    "vat": 22.5,
//    "sub_total": 540,
//    "total": 502.5
//  }
  fun getPlaceOrderModel(addressId: Int): PlacePcrOrderRequest {
    makeCartToPlaceOrderItem()
    val placeOrder = PlacePcrOrderRequest()
    placeOrder.date = appManager.persistenceManager.datepcrglobal.toString()
    placeOrder.time = appManager.persistenceManager.timepcrglobal.toString()
    placeOrder.additional_charges_amount = appManager.persistenceManager.additional_charges_amount
    placeOrder.addressId = appManager.persistenceManager.addressid!!
    placeOrder.collection_id = appManager.persistenceManager.collection_id
    placeOrder.items = placeOrderItems
    placeOrder.discount =
      String.format("%.1f", (appManager.persistenceManager.discounttotal ?: 0.0)).toDouble()
    placeOrder.vat = String.format("%.1f", (appManager.persistenceManager.vattotal)).toDouble()
    placeOrder.sub_total = appManager.persistenceManager.subtotal
    placeOrder.total =
      appManager.persistenceManager.totalwithoutadditonal.plus(appManager.persistenceManager.additional_charges_amount)
        .currencyFormat().toDouble()


//    if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "cash") {
//      placeOrder.codCharge = codCharges.value?.doubleDigitDouble() ?: 0.0
//    }
//    placeOrder.cartId = appManager.persistenceManager.getCartID()

//    couponModel.value?.let {
//      placeOrder.coupon = it.couponCode
//    }

//    placeOrder.notes = note.getValue()
//    placeOrder.instantSlotId = selectedInstantDeliverySlot.value?.slotId
//    placeOrder.standardSlotID = selectedStandardDeliverySlot.value?.slotId
    return placeOrder
  }

  fun getPcrProductSliderList(productDetail: Products): List<CarouselItem> {
    val list = arrayListOf<CarouselItem>()
    list.add(
      CarouselItem(
        imageUrl = productDetail.images.featured_image

      )
    )
    for (item in productDetail.images.gallery_images) {
      list.add(
        CarouselItem(
          imageUrl = item

        )
      )
    }
    return list
  }


  fun getScrollStateMut(): MutableLiveData<ScrollingState> {
    return scrollState.getScrollingState()
  }
//    fun getDiscounts() =
//        performNwOperation { superSellerRepository.getDiscounts() }
//fun rateDriverShipment() =
//  performNwOperation {
//    ratingRepository.rateShipment(
//      shipmentId.toString() ?: "0",
//      makeShipmentRatingRequestBody()
//    )
//  }

  fun getPcrProducts() =
    performNwOperation {
      repository.getPcrProducts(colid)
    }

  fun getPcrDate() =
    performNwOperation {
      repository.getPcrDate(colid)
    }

  fun getPcrAddress() =
    performNwOperation {
      repository.getPcrAddress()
    }

  fun createPcrOrder(body: PlacePcrOrderRequest) =
    performNwOperation { repository.createPcrOrder(colid, body) }

  fun createPcrTransaction(body: TransactionModel) =
    performNwOperation { repository.createPcrTransaction(body) }


  fun getAppoinmentList() =
    performNwOperation { repository.getAppoinmentList(skip.toString(), take.toString()) }

  fun getPcrAppoinmentDetail() =
    performNwOperation { repository.getPcrAppoinmentDetail(appoinmentid!!) }

//    fun getFilters() =
//        performNwOperation { superSellerRepository.getFilters() }

//    fun getQuickOptions() =
//        performNwOperation { superSellerRepository.getQuickOptions() }

  fun setFilterSelectedItems(item: FilterModel) {
    if (selectedFilteredItems.contains(item)) {
      selectedFilteredItems.remove(item)
    } else {
      selectedFilteredItems.add(item)
    }
  }

//  fun getTransactionModer(string: Int?): TransactionModel {
//    val transactionModel = TransactionModel()
//    transactionModel.orderId = orderId
//    transactionModel.type = "charge"
//    if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "new" || selectedPaymentType.value?.name?.toLowerCase(
//        Locale.ROOT
//      ) == "new_for_wallet"
//    ) {
//      transactionModel.CardType = "new"
//    } else {
//      if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "card") {
//        transactionModel.CardType = "saved"
//        transactionModel.card_id = string
//      }
//
//    }
//    when (selectedPaymentType.value) {
//      PaymentType.NEW, PaymentType.NEW_FOR_WALLET, PaymentType.CARD -> {
//        transactionModel.method = "card"
//      }
//      PaymentType.CASH -> {
//        transactionModel.method = "cash"
//      }
//      PaymentType.WALLET -> {
//        transactionModel.method = "wallet"
//      }
//    }
//
//    transactionModel.amount =
//      (appManager.persistenceManager.totalwithoutadditonal).plus(appManager.persistenceManager.additional_charges_amount)
//        .currencyFormat().toDouble()
////      String.format("%.1f", ((appManager.persistenceManager.totalamount))).toDouble() ?: 0.0
//
//    return transactionModel
//  }


  fun saveSelectedRange(from: String, to: String) {
    filtersManager.fromPrice = from
    filtersManager.toPrice = to
  }

//  fun addParentFilter() {
//    if (id != null && type != null) {
//      appManager.filtersManager.addFirstFilter(id.toString(), type!!)
//    }
//    if (listing_type != null && slug != null) {
//      appManager.filtersManager.addFirstFilter(slug!!, listing_type!!)
//    }
//  }

  fun setValuesFromURL(url: String) {
    try {
      val uri: Uri = Uri.parse(url)
      val args = uri.queryParameterNames
      if (!args.isNullOrEmpty()) {
        listing_type = args.first() + "Slug"
        slug = uri.getQueryParameter(args.first())
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}