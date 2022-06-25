package com.lifepharmacy.application.managers

import android.app.Activity
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.lifepharmacy.application.R
import com.lifepharmacy.application.enums.ShipmentType
import com.lifepharmacy.application.managers.analytics.AnalyticsManagers
import com.lifepharmacy.application.managers.analytics.addToCart
import com.lifepharmacy.application.managers.analytics.cartViewed
import com.lifepharmacy.application.managers.analytics.removeFromCart
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.cart.CartResponseModel
import com.lifepharmacy.application.model.cart.OffersAll
import com.lifepharmacy.application.model.cart.OffersCartModel
import com.lifepharmacy.application.model.pcr.pcrlist.Products
import com.lifepharmacy.application.model.product.Offers
import com.lifepharmacy.application.model.product.OffersType
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.products.fragment.AnimationShipmentTypeChangedDialog
import com.lifepharmacy.application.ui.utils.LoadingState
import com.lifepharmacy.application.ui.utils.dailogs.AnimationComboBoxDialog
import com.lifepharmacy.application.ui.utils.dailogs.AnimationOfferCompleteDialog
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Logger
import com.lifepharmacy.application.utils.universal.Utils
import javax.inject.Inject

class OffersManagers
@Inject constructor(
  var persistenceManager: PersistenceManager,
  val loadingState: LoadingState,
  val analyticsManagers: AnalyticsManagers
) {

  private var groups = ArrayList<OffersCartModel>()

  private var productsArray = ArrayList<CartModel>()
  var outOfProductsArray = ArrayList<CartModel>()
  var freeGiftArray = ArrayList<CartModel>()
  var freeGiftsToShow = ArrayList<CartModel>()
  var outOfProductsArrayMut = MutableLiveData<ArrayList<CartModel>>()
  var freeGiftProductArrayMut = MutableLiveData<ArrayList<CartModel>>()
  var instantCount = MutableLiveData<Int>()
  var instantOnlyCountMut = MutableLiveData<Int>()
  var expressCount = MutableLiveData<Int>()
  var offersArrayMut = MutableLiveData<ArrayList<OffersCartModel>>()
  var cartQtyCountMut = MutableLiveData<Int>()
  var instantProductsMut = MutableLiveData<ArrayList<CartModel>>()
  var expressProductsMut = MutableLiveData<ArrayList<CartModel>>()
  var isUpdatingFromServer = false
  var cartIsNotChangedFromServer = true
  var inComboSheet = false
  var isFreeGiftShown = false
  var selectedOffer = MutableLiveData<OffersCartModel>()
  private lateinit var activity: Activity
  fun getSumOfGroupsAmountWithoutDiscount(): Double {
    var total = 0.0

    for (item in groups) {
      total += item.groupAmountWithoutDiscount ?: 0.0
    }
    return total
  }

  fun getSumOfGroupsAmountWithDiscount(): Double {
    var total = 0.0

    for (item in groups) {
      total += item.groupAmountWithDiscount ?: 0.0
    }
    return total
  }

  fun getSumOfGroupsDiscountedAmount(): Double {
    var total = 0.0

    for (item in groups) {
      total += item.groupDiscountedAmount ?: 0.0
    }
    return total
  }

  fun getSumOfGroupsVAT(): Double {
    var total = 0.0

    for (item in groups) {
      total += item.groupVAT ?: 0.0
    }
    return total
  }

  fun checkIfPcrProductAlreadyExistInCart(productDetails: Products): Boolean {
    var returnOffersCartModel: CartModel? = null
    for (index in 0 until groups.size) {
      val item = groups[index]
      val product =
        item.products.firstOrNull { product -> product.productDetails.id == productDetails.id }
      if (product != null) {
        returnOffersCartModel = product
        break
      }
    }
    return returnOffersCartModel != null
  }

  fun checkIfProductAlreadyExistInCart(productDetails: ProductDetails): Boolean {
    var returnOffersCartModel: CartModel? = null
    if (productDetails.offers != null) {
      when (productDetails.offers!!.getTypeEnum()) {
        OffersType.BXGY, OffersType.FREE_GIFT -> {
          val offerModel =
            groups.firstOrNull { offer -> offer.offers?.id == productDetails.offers!!.id }
          returnOffersCartModel =
            offerModel?.products?.firstOrNull { product -> product.productDetails.id == productDetails.id }
        }
        else -> {
          for (index in 0 until groups.size) {
            val item = groups[index]
            val product =
              item.products.firstOrNull { product -> product.productDetails.id == productDetails.id }
            if (product != null) {
              returnOffersCartModel = product
              break
            }
          }
        }
      }
    } else {
      for (index in 0 until groups.size) {
        val item = groups[index]
        val product =
          item.products.firstOrNull { product -> product.productDetails.id == productDetails.id }
        if (product != null) {
          returnOffersCartModel = product
          break
        }
      }
    }
    return returnOffersCartModel != null
  }

  private fun checkAndGetPcrProductAlreadyExistInCart(productDetails: Products): OffersCartModel? {
    var returnOffersCartModel: OffersCartModel? = null
    for (index in 0 until groups.size) {
      val item = groups[index]
      val product =
        item.products.firstOrNull { product -> product.productDetails.id == productDetails.id }
      if (product != null) {
        returnOffersCartModel = item
        break
      }
    }

    return returnOffersCartModel
  }

  private fun checkAndGetProductAlreadyExistInCart(productDetails: ProductDetails): OffersCartModel? {
    var returnOffersCartModel: OffersCartModel? = null
    if (productDetails.offers != null) {
      when ((productDetails.offers ?: return null).getTypeEnum()) {
        OffersType.BXGY, OffersType.FREE_GIFT -> {
          returnOffersCartModel =
            groups.firstOrNull { offer -> offer.offers?.id == productDetails.offers?.id }
        }
        else -> {
          for (index in 0 until groups.size) {
            val item = groups[index]
            val product =
              item.products.firstOrNull { product -> product.productDetails.id == productDetails.id }
            if (product != null) {
              returnOffersCartModel = item
              break
            }
          }
        }
      }
    } else {
      for (index in 0 until groups.size) {
        val item = groups[index]
        val product =
          item.products.firstOrNull { product -> product.productDetails.id == productDetails.id }
        if (product != null) {
          returnOffersCartModel = item
          break
        }
      }
    }
    return returnOffersCartModel
  }


  fun addProduct(
    activity: Activity, productDetails: ProductDetails, position: Int = 1, qty: Int = 1,
    onlyExpress: Boolean = false, onlyInstant: Boolean = false
  ) {
    if (!isUpdatingFromServer) {
      cartIsNotChangedFromServer = true
    }
    setAnalyticsEvent(productDetails, position)
    this.activity = activity
    val filteredOfferItem = checkAndGetProductAlreadyExistInCart(productDetails)

    if (filteredOfferItem != null) {
      val currentQTY = getQtyOfProduct(filteredOfferItem, productDetails)
      val newQTY = currentQTY + qty
      if (newQTY <= productDetails.getAvailableQTy()) {
        addToExistingOffersModel(
          activity,
          filteredOfferItem,
          productDetails,
          newQTY,
          qty,
          onlyExpress,
          onlyInstant
        )
        if (!isUpdatingFromServer) {
          Utils.vibrate(activity)
        }

      } else {
        if (!isUpdatingFromServer) {
          AlertManager.showErrorMessage(activity, activity.getString(R.string.not_enough_stock))
        }
      }
    } else {
      if (qty <= productDetails.getAvailableQTy()) {
        addNewOfferModel(
          activity,
          productDetails,
          qty,
          onlyExpress = onlyExpress,
          onlyInstant = onlyInstant
        )
        if (!isUpdatingFromServer) {
          Utils.vibrate(activity)
        }
      } else {
        if (!isUpdatingFromServer) {
          AlertManager.showErrorMessage(activity, activity.getString(R.string.not_enough_stock))
        }

      }
    }

    if (!isUpdatingFromServer) {
      refresh()
    }
  }

  private fun setAnalyticsEvent(productDetails: ProductDetails, position: Int, qty: Int = 1) {

    analyticsManagers.addToCart(productDetails = productDetails, position = position, qty = qty)
  }

  fun addCheckProduct(activity: Activity, productDetails: ProductDetails, qty: Int = 1) {
//    CoroutineScope(Dispatchers.IO).launch {
    val filteredOfferItem = checkAndGetProductAlreadyExistInCart(productDetails)

    if (filteredOfferItem != null) {
      val currentQTY = getQtyOfProduct(filteredOfferItem, productDetails)
      val newQTY = currentQTY + (qty)
      if (newQTY <= productDetails.getAvailableQTy()) {
        if (productDetails.availability?.instant != null && productDetails.availability?.instant?.isAvailable == true) {
          Log.e(TAG, "addCheckProduct:cur in cart 1 $currentQTY")
          Log.e(TAG, "addCheckProduct:new = cart+total qty $newQTY")
          Log.e(TAG, "addCheckProduct:qty $qty")
          Log.e(TAG, "addCheckProduct:instat qty ${productDetails.availability!!.instant?.qty}", )

          if (newQTY == productDetails.availability?.instant?.getQTY()!! + 2) {
            if (!isUpdatingFromServer) {
              activity.findNavController(R.id.fragment).navigate(
                R.id.animationShipmentTypeChangedDialog,
                AnimationShipmentTypeChangedDialog.getBundle(
                  "Only ${productDetails.availability?.instant?.qty?.toInt()} ${activity!!.getString(R.string.shipment_type_changed)}"
                )
              )

//              AlertManager.showInfoMessage(
//                activity,
//                activity.getString(R.string.express),
//                "${productDetails.title} ${activity.getString(R.string.delivery_has_been_converted)} ${
//                  activity.getString(
//                    R.string.express
//                  )
//                }"
//              )
            }

          }
        }
      } else {
        if (!isUpdatingFromServer) {
          AlertManager.showErrorMessage(activity, activity.getString(R.string.not_enough_stock))
        }
      }
    } else {
      if (qty <= productDetails.getAvailableQTy()) {
        if (productDetails.availability?.instant != null && productDetails.availability?.instant?.isAvailable == true) {
          if (qty == productDetails.availability?.instant?.getQTY()!! + 1) {
            if (!isUpdatingFromServer) {
              activity.findNavController(R.id.fragment).navigate(
                R.id.animationShipmentTypeChangedDialog,
                AnimationShipmentTypeChangedDialog.getBundle(
                  "Only ${productDetails.availability?.instant?.qty?.toInt()} ${activity!!.getString(R.string.shipment_type_changed)} "
                )
              )
//              AlertManager.showInfoMessage(
//                activity,
//                activity.getString(R.string.express),
//                "${productDetails.title} ${activity.getString(R.string.delivery_has_been_converted)} ${
//                  activity.getString(
//                    R.string.express
//                  )
//                }"
//              )
            }

          }
        }
      } else {
        if (!isUpdatingFromServer) {
          AlertManager.showErrorMessage(activity, activity.getString(R.string.not_enough_stock))
        }

      }
    }
//    }

    this.activity = activity

    refresh()
  }
  fun minusCheckProduct(activity: Activity, productDetails: ProductDetails, qty: Int = 1) {
//    CoroutineScope(Dispatchers.IO).launch {
    val filteredOfferItem = checkAndGetProductAlreadyExistInCart(productDetails)

    if (filteredOfferItem != null) {
      val currentQTY = getQtyOfProduct(filteredOfferItem, productDetails)
      val newQTY = currentQTY + qty
      if (newQTY <= productDetails.getAvailableQTy()) {
      } else {
        if (!isUpdatingFromServer) {
          AlertManager.showErrorMessage(activity, activity.getString(R.string.not_enough_stock))
        }
      }
    } else {
      if (qty <= productDetails.getAvailableQTy()) {
      } else {
        if (!isUpdatingFromServer) {
          AlertManager.showErrorMessage(activity, activity.getString(R.string.not_enough_stock))
        }

      }
    }
//    }

    this.activity = activity

    refresh()
  }

  private fun addNewOfferModel(
    activity: Activity, productDetails: ProductDetails, qty: Int = 1,
    onlyExpress: Boolean = false, onlyInstant: Boolean = false
  ) {
    val model = OffersCartModel()
    model.products.add(
      CartModel(
        qty,
        productDetails,
        onlyInstant = onlyInstant,
        onlyExpress = onlyExpress
      )
    )
    model.offers = productDetails.offers
    model.calculate(activity)
    groups.add(model)
    loadAnimations(productDetails, model)
    saveCartToPref()
  }

  private fun addToExistingOffersModel(
    activity: Activity,
    offersCartModel: OffersCartModel,
    productDetails: ProductDetails,
    newQTY: Int,
    qty: Int = 1,
    onlyExpress: Boolean = false, onlyInstant: Boolean = false

  ) {
    val index: Int? = groups.indexOf(offersCartModel)
    when (offersCartModel.getTypeEnum()) {
      OffersType.BXGY, OffersType.FREE_GIFT -> {
//        Logger.d("EnterQTY","${}")
        offersCartModel.products.add(
          CartModel(
            qty = qty,
            productDetails = productDetails,
            onlyInstant = onlyInstant,
            onlyExpress = onlyExpress
          )
        )
      }
      OffersType.NON, OffersType.FLAT -> {
        val alreadyProduct = offersCartModel.products[0]
        alreadyProduct.onlyExpress = onlyExpress
        alreadyProduct.onlyInstant = onlyInstant
        alreadyProduct.qty = newQTY
        offersCartModel.products.removeAt(0)
        offersCartModel.products.add(0, alreadyProduct)
//        offersCartModel.calculate(activity)
      }
    }
    offersCartModel.calculate(activity)
    val gson = Gson()
    val jsonStr = gson.toJson(offersCartModel)
    Logger.d("productsWithQTY", jsonStr)
    if (productDetails.availability?.instant != null && productDetails.availability?.instant?.isAvailable == true) {
      if (newQTY == productDetails.availability?.instant?.getQTY()!! + 1) {
        if (!isUpdatingFromServer) {
          AlertManager.showInfoMessage(
            activity,
            activity.getString(R.string.express),
            "${productDetails.title} ${activity.getString(R.string.delivery_has_been_converted)} ${
              activity.getString(
                R.string.express
              )
            }"
          )
        }

      }
    }

    if (index != null) {
      refreshArrayAt(index, offersCartModel)
    }
    loadAnimations(productDetails, offersCartModel)
  }

  fun checkAndGetExistingQTY(productDetails: ProductDetails): Int? {
    var qty: Int? = null
    val filterItem = checkAndGetProductAlreadyExistInCart(productDetails = productDetails)
    if (filterItem != null) {
      qty = getQtyOfProduct(filterItem, productDetails)
    }
    return qty
  }

  fun checkAndGetExistingPcrQTY(productDetails: Products): Int? {
    var qty: Int? = null
    val filterItem = checkAndGetPcrProductAlreadyExistInCart(productDetails = productDetails)
    if (filterItem != null) {
      qty = getQtyOfPcrProduct(filterItem, productDetails)
    }
    return qty
  }

  private fun getQtyOfPcrProduct(
    offersCartModel: OffersCartModel,
    productDetails: Products
  ): Int {
    var returnInt = 0
    when (offersCartModel.getTypeEnum()) {
      OffersType.FREE_GIFT, OffersType.BXGY -> {
        for (item in offersCartModel.products) {
          if (item.productDetails.id == productDetails.id) {
            returnInt += 1
          }
        }

      }
      OffersType.NON, OffersType.FLAT -> {
        returnInt = offersCartModel.products[0].qty
      }
    }
    return returnInt
  }

  private fun getQtyOfProduct(
    offersCartModel: OffersCartModel,
    productDetails: ProductDetails
  ): Int {
    var returnInt = 0
    when (offersCartModel.getTypeEnum()) {
      OffersType.FREE_GIFT, OffersType.BXGY -> {
        for (item in offersCartModel.products) {
          if (item.productDetails.id == productDetails.id) {
            returnInt += 1
          }
        }

      }
      OffersType.NON, OffersType.FLAT -> {
        returnInt = offersCartModel.products[0].qty
      }
    }
    return returnInt
  }

  fun removeProduct(
    activity: Activity,
    productDetails: ProductDetails,
    position: Int,
    qty: Int = 1
  ) {
    Log.e(TAG, "onClickMinus: Remove product")
    if (!isUpdatingFromServer) {
      cartIsNotChangedFromServer = true
    }
    analyticsManagers.removeFromCart(
      productDetails = productDetails,
      position = position,
      qty = qty
    )
    val filteredOfferItem = checkAndGetProductAlreadyExistInCart(productDetails)
    if (filteredOfferItem != null) {
      val index: Int? = groups.indexOf(filteredOfferItem)
      when (filteredOfferItem.getTypeEnum()) {
        OffersType.FREE_GIFT, OffersType.BXGY -> {
          val product =
            filteredOfferItem.products.firstOrNull { product -> product.productDetails.id == productDetails.id }
          val indexOfProduct: Int? = filteredOfferItem.products.indexOf(product)

          indexOfProduct?.let {
            if (filteredOfferItem.products.size >= it) {
              filteredOfferItem.products.removeAt(it)
              filteredOfferItem.calculate(activity)
              refresh()
            }
            index?.let { mainIndex ->
              refreshArrayAt(mainIndex, filteredOfferItem)
              if (!isUpdatingFromServer) {
                Utils.vibrate(activity)
              }
              if (filteredOfferItem.products.isEmpty()) {
                groups.removeAt(mainIndex)
                refresh()
              }
            }
//            if (index != null) {
//
//            }
          }

//          if (indexOfProduct != null) {
//
//          }

        }
        else -> {
          val alreadyProduct = filteredOfferItem.products[0]
          if (alreadyProduct.qty > 1) {
            val newQTY = alreadyProduct.qty - 1
            alreadyProduct.qty = newQTY
            filteredOfferItem.products.removeAt(0)
            filteredOfferItem.products.add(0, alreadyProduct)
            if (index != null) {
              refreshArrayAt(index, filteredOfferItem)
            }
            refresh()
            if (!isUpdatingFromServer) {
              Utils.vibrate(activity)
            }
          } else {
            if (index != null && index >= 0) {
              groups.removeAt(index)
              refresh()
              if (!isUpdatingFromServer) {
                Utils.vibrate(activity)
              }
            }

          }
        }
      }
    }
  }

  private fun refresh() {
    if (groups.size > 0 || outOfProductsArray.size > 0) {
      calculateItemsCount()
      calculateOfferTotals()
      offersArrayMut.value = groups
      val gson = Gson()
      val jsonStr = gson.toJson(groups)
      Logger.d("productsWithQTY", jsonStr)
      saveCartToPref()
    } else {
      clear()
    }
  }

  private fun refreshArrayAt(position: Int, offersCartModel: OffersCartModel) {
    groups.removeAt(position)
    groups.add(position, offersCartModel)
  }

  fun getAllProductsWithQTY(): ArrayList<CartModel> {
    val cartList = ArrayList<CartModel>()
    for (item in groups) {
      for (subItem in item.productsWithQTY) {
        cartList.add(subItem)
      }
    }
    return cartList
  }

  private fun saveCartToPref() {
    val offers = OffersAll(groups)
    persistenceManager.saveOffersCartList(offers)
  }

  fun loadCartFromPref(activity: Activity) {
    this.activity = activity
    persistenceManager.getOffersCartList()?.list?.let {
      groups = it
      refresh()
    }
  }

  fun clear() {
    freeGiftArray.clear()
    freeGiftProductArrayMut.value = freeGiftArray
    calculateItemsCount()
    calculateOfferTotals()
    groups.clear()
    offersArrayMut.value = groups
    cartQtyCountMut.value = 0
    persistenceManager.clearCartID()
//    saveCartToPref()
  }

  private fun calculateItemsCount() {
    var count = 0
    for (item in groups) {
      count += when (item.getTypeEnum()) {
        OffersType.BXGY, OffersType.FREE_GIFT -> {
          item.products.size
        }
        else -> {
          item.products[0].qty
        }
      }
    }
    cartQtyCountMut.value = count
//    offersManagers.refresh()
  }

  fun makeProductsArray() {
    productsArray.clear()
    for (item in groups) {
      val temp = item.isInstant
      for (subItem in item.productsWithQTY) {
        subItem.isInstant = temp
        productsArray.add(subItem)
      }
    }
    val gson = Gson()
    val jsonStr = gson.toJson(productsArray)
    Logger.d("ProductsArray", jsonStr)
  }

  fun changeDeliveryOption(instantOn: Boolean) {
//    val instantProducts = ArrayList<CartModel>()
//    var expressProducts = ArrayList<CartModel>()

    val instantOnly = ArrayList<CartModel>()
    val expressOnly = ArrayList<CartModel>()

    val giftInstant = ArrayList<CartModel>()
    val giftExpress = ArrayList<CartModel>()
    var instantCounter = 0
    var instantOnlyCounter = 0
//    val bothProducts = ArrayList<CartModel>()

    productsArray.let {
      for (item in it) {
        if (instantOn) {
          if (item.onlyInstant || ((!item.onlyInstant && !item.onlyExpress) && (item.productDetails.stockAvailability(
              item.qty
            ) == ShipmentType.INSTANT))
          ) {
            instantCounter += 1
            instantOnly.add(item)
          } else {
            expressOnly.add(item)
          }
          if (item.onlyInstant) {
            instantOnlyCounter += 1
          }
        } else {
          if (item.onlyExpress || (!item.onlyInstant && !item.onlyExpress) || (item.productDetails.stockAvailability(
              item.qty
            ) != ShipmentType.INSTANT)
          ) {
            if (!item.onlyExpress && (item.productDetails.stockAvailability() == ShipmentType.INSTANT)) {
              instantCounter += 1
            }
            expressOnly.add(item)
          } else {
            instantOnlyCounter += 1
            instantOnly.add(item)
          }
        }


//
//        when {
//          item.onlyInstant -> {
//
//          }
//          item.onlyExpress -> {
//            expressOnly.add(item)
//          }
//          else -> {
//            bothProducts.add(item)
//          }
//        }
      }

      instantProductsMut.value = instantOnly
      expressProductsMut.value = expressOnly
      instantCount.value = instantCounter
      instantOnlyCountMut.value = instantOnlyCounter
      expressCount.value = expressOnly.size
//      inBothShipmentProducts.value = bothProducts
//      if (instantOn) {
//        if (!it.isNullOrEmpty()) {
//          for (item in it) {
//            if (item.isInstant) {
//              instantProducts.add(item)
//            } else {
//              expressProducts.add(item)
//            }
//          }
//        }
//        instantCount.value = instantProducts.size
//      } else {
//        getCalculateCounts()
//        expressProducts = it
//      }
    }
    freeGiftsToShow?.let {
      for (item in it) {
        if (instantOn) {
          if (item.onlyInstant || ((!item.onlyInstant && !item.onlyExpress) && (item.productDetails.stockAvailability(
              item.qty
            ) == ShipmentType.INSTANT))
          ) {
            if (instantCounter > 0) {
              giftInstant.add(item)
            }

          } else {
            if (expressCount.value ?: 0 > 0) {
              giftExpress.add(item)
            }

          }
        } else {
          if (item.onlyExpress || (!item.onlyInstant && !item.onlyExpress) || (item.productDetails.stockAvailability(
              item.qty
            ) != ShipmentType.INSTANT)
          ) {
            if (expressCount.value ?: 0 > 0) {
              giftExpress.add(item)
            }
          } else {
            if (instantCounter > 0) {
              giftInstant.add(item)
            }
          }
        }

      }
    }
    freeGiftArray.clear()
    freeGiftArray.addAll(giftInstant)
    freeGiftArray.addAll(giftExpress)
    freeGiftProductArrayMut.value = freeGiftArray

//    expressCount.value = expressProducts.size
//    instantProductsMut.value = instantProducts
//    expressProductsMut.value = expressProducts
  }

//  fun getCalculateCounts() {
//    var countInstant = 0
//    productsArray.let {
//      if (!it.isNullOrEmpty()) {
//        for (item in it) {
//          if (item.isInstant) {
//            countInstant++
//          }
//        }
//      }
//    }
//    instantCount.value = countInstant
//  }

  private fun calculateOfferTotals() {
    for (item in groups) {
      item.calculate(activity)
    }
  }

  fun updateCartFromServer(
    activity: Activity,
    list: ArrayList<CartModel>,
    cartResponseModel: CartResponseModel?
  ) {
    cartResponseModel?.let { analyticsManagers.cartViewed(it) }
    isUpdatingFromServer = true
    groups.clear()
    for (item in list) {
      if (item.isChanged) {
        cartIsNotChangedFromServer = false
      }
      for (i in 1..item.qty) {
        addProduct(
          activity,
          item.productDetails,
          onlyExpress = item.onlyExpress,
          onlyInstant = item.onlyInstant
        )
      }
    }
    if (cartResponseModel != null) {
      updateOutStockItem(cartResponseModel.outOfStock)

      if (cartResponseModel.giftItems.isNullOrEmpty()) {
        isFreeGiftShown = false
      } else {
        if (!isFreeGiftShown) {
          isFreeGiftShown = true
          showFreeGiftBox()

        }
      }
      updateFreeGiftItems(cartResponseModel.giftItems)
    }
    refresh()
    isUpdatingFromServer = false
  }

  private fun updateOutStockItem(list: ArrayList<CartModel>) {
    outOfProductsArray.clear()
    outOfProductsArray.addAll(list)
    outOfProductsArrayMut.value = outOfProductsArray
  }


  fun removeOutOfStockItem(position: Int) {
    cartIsNotChangedFromServer = true
    outOfProductsArray.removeAt(position)
    outOfProductsArrayMut.value = outOfProductsArray
  }

  private fun updateFreeGiftItems(list: ArrayList<CartModel>) {
    freeGiftArray.clear()
    freeGiftsToShow.clear()
    freeGiftArray.addAll(list)
    freeGiftsToShow.addAll(list)
    freeGiftProductArrayMut.value = freeGiftsToShow
  }


  private fun loadAnimations(
    productDetails: ProductDetails,
    offersCartModel: OffersCartModel
  ) {
    when (productDetails.offers?.getTypeEnum()) {
      OffersType.BXGY, OffersType.FREE_GIFT -> {
        if (!isUpdatingFromServer) {
          if (offersCartModel.getSlotsCount() == offersCartModel.products.size) {
            offersCartModel.isValid = true
            activity.findNavController(R.id.fragment).navigate(
              R.id.animationOfferCompleteDialog,
              AnimationOfferCompleteDialog.getBundle(
                "${activity.getString(R.string.you_saved)} ${persistenceManager.getCurrency()} ${
                  offersCartModel.savedAmount().currencyFormat()
                }"
              )
            )
//            loadingState.setAnimationState(true, "combo_added.json",offersCartModel.savedAmount())
          } else {
            offersCartModel.isValid = false
            AlertManager.showSuccessMessage(
              activity,
              activity.getString(R.string.product_added_to_cart)
            )
            openBottomSheetOptions(productDetails, offersCartModel)
          }
        }
      }
      else -> {

      }
    }
  }

  private fun openBottomSheetOptions(
    productDetails: ProductDetails,
    offersCartModel: OffersCartModel
  ) {

    comboBoxAnimationDialog(productDetails, offersCartModel)
  }

  private fun comboBoxAnimationDialog(
    productDetails: ProductDetails,
    offersCartModel: OffersCartModel
  ) {
    selectedOffer.value = offersCartModel
    val temActivity = activity as FragmentActivity
    val fragmentManager = temActivity.supportFragmentManager
    if (productDetails.offers != null && !inComboSheet) {
      when (productDetails.offers!!.getTypeEnum()) {
        OffersType.BXGY, OffersType.FREE_GIFT -> {
          activity.findNavController(R.id.fragment).navigate(
            R.id.toComboBox,
            AnimationComboBoxDialog.getBundle(
              "${activity.getString(R.string.buy)} ${productDetails.offers?.xValue}  ${
                activity.getString(
                  R.string.get
                )
              } ${productDetails.offers?.yValue}", false
            )
          )
        }
        else -> {
        }
      }
    }
  }

  private fun showFreeGiftBox() {
    activity.findNavController(R.id.fragment).navigate(
      R.id.toComboBox,
      AnimationComboBoxDialog.getBundle(
        activity.getString(R.string.free_gift), true
      )
    )
  }

  fun isAllGroupValid(): Boolean {
    var returnBoolean = true
    for (group in groups) {
      if (!group.isValid) {
        returnBoolean = false
        break
      }
    }
    return returnBoolean
  }

  fun getFirstInvalidGroupOffer(): Offers? {
    var returnOffer: Offers? = null
    for (group in groups) {
      if (!group.isValid) {
        returnOffer = group.products.first().productDetails.offers
        break
      }
    }
    return returnOffer
  }

  fun getFirstInvalidGroupOfferLocation(): Int? {
    var returnOffer: Int? = 0
    for (group in groups) {
      if (!group.isValid) {
        returnOffer = groups.indexOf(group)
        break
      }
    }
    return returnOffer
  }
}