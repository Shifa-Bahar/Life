package com.lifepharmacy.application.managers

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.facebook.appevents.AppEventsConstants
import com.google.firebase.perf.v1.PerfSession
import com.google.gson.Gson
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.managers.analytics.AnalyticsManagers
import com.lifepharmacy.application.managers.analytics.addToWishList
import com.lifepharmacy.application.managers.analytics.removeFromWishList
import com.lifepharmacy.application.model.WishListAll
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.channel.Channel
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.orders.PlaceOrderRequest
import com.lifepharmacy.application.model.product.Offers
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.product.ProductListingMainModel
import com.lifepharmacy.application.model.wishlist.AddWishListRequestBody
import com.lifepharmacy.application.model.wishlist.DeleteWishListBody
import com.lifepharmacy.application.repository.EventRepository
import com.lifepharmacy.application.repository.WishListRepository
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.utils.*
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.GoogleUtils
import com.lifepharmacy.application.utils.universal.Logger
import com.onesignal.OneSignal
import javax.inject.Inject


class EventlistmainManager
@Inject constructor(
  var persistenceManager: PersistenceManager,
  private var eventRepository: EventRepository,
  val analyticsManagers: AnalyticsManagers
) {
  var wishListItemsMut = MutableLiveData<ArrayList<ProductDetails>>()
  private var wishListItems: ArrayList<ProductDetails>? = ArrayList()

  //1.item_viewed event
  fun eventTrackingItemViewed(product: ProductDetails) {

    val params = hashMapOf<String, Any?>()
    params["event_data"] = setItemDataModel(product)
    params["event_type"] = "item_viewed"
    params["source"] = "android"
    params["channel"] = setEventChannel()
    params["user_properties"] = setUserProperties()
    eventcallltorepository(params)
  }

  //2.item_list_viewed event
  fun eventTrackingItemListViewed(product: ProductListingMainModel) {
    val params = hashMapOf<String, Any?>()
    params["event_data"] = setItemForViewList(product)
    params["event_type"] = "item_list_viewed"
    params["source"] = "android"
    params["channel"] = setEventChannel()
    params["user_properties"] = setUserProperties()
    eventcallltorepository(params)
  }

  //3.item_added_to_cart event
  fun eventTrakingItemAddedToCart(product: ProductDetails, quantity: Int = 1) {

    val cartId = persistenceManager.getCartID()
    var original_price: String
    original_price =
      setRegularPrice(product.prices?.get(0)?.price?.regularPrice, product.offers, product)

    val params = hashMapOf<String, Any?>()
    params["event_data"] = setItemDataModel(product)
    params["qty"] = quantity
    params["cart_id"] = cartId
    params["item_value"] = original_price
    params["event_type"] = "item_added_to_cart"
    params["source"] = "android"
    params["channel"] = setEventChannel()
    params["user_properties"] = setUserProperties()
    eventcallltorepository(params)

  }

  //4. item_removed_from_cart evet
  fun eventTrakingItemRemovedFromCart(product: ProductDetails, quantity: Int = 1) {

    val cartId = persistenceManager.getCartID()
    var original_price: String
    original_price =
      setRegularPrice(product.prices?.get(0)?.price?.regularPrice, product.offers, product)

    val params = hashMapOf<String, Any?>()
    params["event_data"] = setItemDataModel(product)
    params["qty"] = quantity
    params["cart_id"] = cartId
    params["item_value"] = original_price
    params["event_type"] = "item_removed_from_cart"
    params["source"] = "android"
    params["channel"] = setEventChannel()
    params["user_properties"] = setUserProperties()
    eventcallltorepository(params)

  }

  //5.item_added_to_wishlist AND 6.item_removed_from_wishlist events
  fun eventTrakingItemAddedAndRemovedWishlist(product: ProductDetails, isAdded: Boolean) {
    val params = hashMapOf<String, Any?>()
    params["event_data"] = setItemDataModel(product)
    params["event_type"] = if (isAdded) "item_added_to_wishlist" else "item_removed_from_wishlist"
    params["source"] = "android"
    params["channel"] = setEventChannel()
    params["user_properties"] = setUserProperties()
    eventcallltorepository(params)
  }


  fun setProductsOrderPlace(product: ArrayList<OrderItem>?): ArrayList<ProductDetails>? {

    var productsItems: java.util.ArrayList<ProductDetails>? = java.util.ArrayList()
    product?.forEach { product ->
      productsItems?.add(product.productDetails)
    }
    return productsItems
  }

  //7.checkout_initiated, 8.checkout_failed and 9.checkout_completed events
  fun eventTrakingCheckoutStatus(
    product: ArrayList<CartModel>?,
    totalAmount: Double,
    totalDiscount: Double,
    actionType: String
  ) {
    val cartId = persistenceManager.getCartID()
    val params = hashMapOf<String, Any?>()
    params["event_data"] = setCartProductsModel(product)
    params["event_type"] = actionType
    params["source"] = "android"
    params["cart_id"] = cartId
    params["discount"] = totalDiscount
    params["total"] = totalAmount
    params["channel"] = setEventChannel()
    params["user_properties"] = setUserProperties()
    eventcallltorepository(params)
  }

  //creating an array of cart product
  fun setCartProductsModel(product: ArrayList<CartModel>?): ArrayList<Any>? {

    var productsItems: ArrayList<Any>? = ArrayList()
    product?.forEach { product ->
      productsItems?.add(setItemDataModel(product.productDetails))
    }
    return productsItems
  }

  fun eventTrakingCheckoutStatusorder(
    product: OrderResponseModel,
    totalAmount: Double,
    totalDiscount: Double,
    actionType: String
  ) {
    val cartId = persistenceManager.getCartID()
    val params = hashMapOf<String, Any?>()
    params["event_data"] = setCartProductsModelorder(product.items)
    params["event_type"] = actionType
    params["source"] = "android"
    params["cart_id"] = cartId
    params["discount"] = totalDiscount
    params["total"] = totalAmount
    params["channel"] = setEventChannel()
    params["user_properties"] = setUserProperties()
    eventcallltorepository(params)
  }

  // setting channel
  fun setEventChannel(): ArrayList<Any>? {
    var channelItems: ArrayList<Any>? = ArrayList()
    var channel = persistenceManager.getChannel().channelarray
    Log.e(TAG, "setEventChannel: $channel")

    val params = hashMapOf<String, Any?>()
    channel?.forEach { item ->
      params["key"] = item.utmsource ?: ""
      params["value"] = item.utmcampaignid ?: ""
      channelItems?.add(params)
    }

    return channelItems
  }

  fun setCategory(product: ProductDetails): ArrayList<Any>? {
    var categoryItems: ArrayList<Any>? = ArrayList()
    var category = product.categories

    val params = hashMapOf<String, Any?>()
    category?.forEach { item ->
      params["slug"] = item.slug ?: ""
      params["category_name"] = item.name ?: ""
      params["id"] = item.id ?: ""
      categoryItems?.add(params)
    }

    return categoryItems
  }

  fun setItemBrand(product: ProductDetails): Any {

    val itemBrand = hashMapOf<String, Any?>()
    itemBrand["slug"] = product.brand?.slug ?: ""
    itemBrand["id"] = product.brand?.id ?: ""
    itemBrand["brand_name"] = product.brand?.name ?: ""

    return itemBrand
  }

  //creating an array of cart product
  fun setCartProductsModelorder(product: ArrayList<OrderItem>?): ArrayList<Any>? {

    var productsItems: ArrayList<Any>? = ArrayList()
    product?.forEach { item ->
      productsItems?.add(setItemDataModel(item.productDetails))
    }
    return productsItems
  }

  //creating an array of product list by using the existing item data model
  fun setItemForViewList(product: ProductListingMainModel): ArrayList<Any>? {
    var productsItems: ArrayList<Any>? = ArrayList()
    product.products?.forEach { product ->
      productsItems?.add(setItemDataModel(product))
    }
    return productsItems
  }

  // user_properties
  fun setUserProperties(): Any {
    val itemDict = hashMapOf<String, Any?>()
    itemDict["gender"] = persistenceManager.getLoggedInUser()?.gender?.toLowerCase() ?: ""
    itemDict["uid"] = persistenceManager.getUUID() ?: ""
    itemDict["email"] = persistenceManager.getLoggedInUser()?.email ?: ""
    itemDict["dob"] = persistenceManager.getLoggedInUser()?.dob ?: ""
    itemDict["push_id"] = ConstantsUtil.ONE_SIGNAL_KEY ?: ""
    itemDict["phone"] = persistenceManager.getLoggedInUser()?.phone ?: ""
    itemDict["location"] = setUserLocation()
    itemDict["user_id"] = persistenceManager.getLoggedInUser()?.id ?: ""

    return itemDict
  }


  // user location
  fun setUserLocation(): Any {
    val address = persistenceManager.getAddressList()
    var coordinatelist: ArrayList<Double>? = ArrayList()
    persistenceManager.getLong()?.toDoubleOrNull().let {
      if (it != null) {
        coordinatelist?.add(it)
      }
    }
    persistenceManager.getLat()?.toDoubleOrNull().let {
      if (it != null) {
        coordinatelist?.add(it)
      }
    }

    val itemDict = hashMapOf<String, Any?>()
    itemDict["type"] = "point"
    itemDict["coordinates"] = coordinatelist ?: ""


    return itemDict
  }

  fun setRegularPrice(
    prices: Double?,
    offers: Offers?,
    product: ProductDetails?
  ): String {
    var price: String = ""
    try {
      prices?.let {
        if (offers != null && offers.type == "flat_percentage_discount") {
          if (product != null) {
            val amountAfterVat = CalculationUtil.getPriceAfterVATOGive(prices, product)
            price = amountAfterVat.currencyFormat()
          } else {
            price = prices.currencyFormat()
          }
        } else {
          price = prices.currencyFormat()
        }
      }

    } catch (e: Exception) {

    }
    return price
  }

  fun setFormatePrice(
    prices: Double?,
    offers: Offers?,
    product: ProductDetails?
  ): String {
    var price: String = ""
    try {
      prices?.let {
        if (offers != null && offers.type == "flat_percentage_discount") {
          val percentagePrice = (prices * offers.value!!) / 100
          val discountedPrice = prices - percentagePrice

          if (product != null) {
            val amountAfterVat = CalculationUtil.getPriceAfterVATOGive(discountedPrice, product)
            price = amountAfterVat.currencyFormat()
          } else {
            price = discountedPrice.currencyFormat()
          }


        } else {
          if (product != null) {
            val amountAfterVat = CalculationUtil.getPriceAfterVATOGive(prices, product)
            price = amountAfterVat.currencyFormat()
          } else {
            price = prices.currencyFormat()
          }

        }
      }

    } catch (e: Exception) {

    }
    return price
  }

  // I'm setting model for product detail.
  fun setItemDataModel(product: ProductDetails): Any {
//    original_price = setRegularPrice(regprice, product.offers, product)
    var originalprice: String =
      setRegularPrice(product.prices?.get(0)?.price?.regularPrice, product.offers, product)

    var displayprice: String =
      setFormatePrice(product.prices?.get(0)?.price?.regularPrice, product.offers, product)
//    setFormatePrice(product.prices?.get(0)?.price?.regularPrice, product.offers, product)
    val itemDict = hashMapOf<String, Any?>()
    itemDict["product_id"] = product.id ?: ""
    itemDict["sku"] = product.inventory?.sku ?: ""
    itemDict["product_name"] = product.title ?: ""
    itemDict["price"] = originalprice ?: ""
    itemDict["sale_price"] = displayprice ?: ""
    itemDict["on_offer"] = product.offers != null
    itemDict["in_stock"] = product.getAvailableQTy() > 0
    itemDict["qty"] = "1" ?: ""
    itemDict["categories"] = setCategory(product)
    itemDict["brand"] = setItemBrand(product)

    return itemDict
  }

  fun eventcallltorepository(params: Any) {
    eventRepository.eventcallNetwork(
      params,
      object :
        HandleNetworkCallBack<GeneralResponseModel<ProductDetails>> {
        override fun handleWebserviceCallBackFailure(error: String?) {
          Logger.e("Error", "$error ")
        }

        override fun handleWebserviceCallBackSuccess(response: retrofit2.Response<GeneralResponseModel<ProductDetails>>) {
          response.body()?.data?.let {
            Log.e(TAG, "handleWebserviceCallBackSuccess: eventcallltorepository")
          }
        }

      })
  }


}