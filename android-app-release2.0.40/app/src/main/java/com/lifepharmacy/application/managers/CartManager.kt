package com.lifepharmacy.application.managers

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.R
import com.lifepharmacy.application.model.CartAll
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.utils.universal.Utils
import javax.inject.Inject


class CartManager
@Inject constructor(var persistenceManager: PersistenceManager,var offersManagers: OffersManagers) {
  var cartItemsMut = MutableLiveData<ArrayList<CartModel>>()
  var cartQtyCountMut = MutableLiveData<Int>()
  var selectedItemsMut = MutableLiveData<ArrayList<String>>()
  var instantCount = MutableLiveData<Int>()
  var expressCount = MutableLiveData<Int>()
  var selectedItemsCartMut = MutableLiveData<ArrayList<CartModel>>()
  var instantProductsMut = MutableLiveData<ArrayList<CartModel>>()
  var expressProductsMut = MutableLiveData<ArrayList<CartModel>>()
  var cartItems: ArrayList<CartModel>? = ArrayList()
  var selectedItems = ArrayList<String>()

  fun addProduct(productDetails: ProductDetails, activity: Activity, qty: Int = 1) {
    val cartModel =
      cartItems?.firstOrNull { cartModel -> cartModel.productDetails.id == productDetails.id }
    if (cartModel == null) {
      if (qty >= productDetails.availability?.express?.qty!!) {
        AlertManager.showErrorMessage(activity, "Not enough quantity in Stock")
      } else {
        val tempCartModel = CartModel(qty, productDetails)
        cartItems?.add(tempCartModel)
        selectUnselected(tempCartModel)
        AlertManager.showSuccessMessage(
          activity,
          "${activity.getString(R.string.product_added_to_cart)} "
        )
      }

    } else {
      if (qty >= productDetails.availability?.express?.qty!!) {
        AlertManager.showErrorMessage(activity, "Not enough quantity in Stock")
      } else {
        addUpdateQTY(cartModel,cartModel.qty+qty)
        AlertManager.showSuccessMessage(
          activity,
          "${activity.getString(R.string.product_added_to_cart)} "
        )
      }
    }

//    Utils.vibrate(activity)
    saveCartToPref()
    cartItemsMut.value = cartItems
    calculateItemsCount()
  }

  fun addMultipleProducts(
    productDetails: ArrayList<ProductDetails>,
    activity: Activity,
    qty: Int = 1
  ) {
    for (item in productDetails) {
      val cartModel =
        cartItems?.firstOrNull { cartModel -> cartModel.productDetails.id == item.id }
      if (cartModel == null) {
        val tempCartModel = CartModel(qty, item)
        cartItems?.add(tempCartModel)
      } else {
        addUpdateQTY(cartModel,cartModel.qty+qty)


      }

    }
    AlertManager.showSuccessMessage(
      activity,
      "${activity.getString(R.string.product_added_to_cart)} "
    )
//    Utils.vibrate(activity)
    saveCartToPref()
    cartItemsMut.value = cartItems
    calculateItemsCount()
  }

  fun removeItem(productDetails: ProductDetails, activity: Activity) {
    try {
      val cartModel =
        cartItems?.firstOrNull { cartModel -> cartModel.productDetails.id == productDetails.id }
      if (cartModel != null) {
        cartItems?.remove(cartModel)
        saveCartToPref()
        cartItemsMut.value = cartItems
        val string =
          selectedItems.firstOrNull { selectedItems -> selectedItems == cartModel.productDetails.id }
        if (string != null) {
          selectedItems.remove(string)

        }
//        Utils.vibrate(activity)
        selectedItemsMut.value = selectedItems
      }
      calculateItemsCount()
    }catch (e:Exception){

    }

  }

  fun plusQty(productDetails: ProductDetails, activity: Activity) {
    val cartModel =
      cartItems?.firstOrNull { cartModel -> cartModel.productDetails.id == productDetails.id }
    if (cartModel != null) {
      if (cartModel.qty >= cartModel.productDetails.availability?.express?.qty!!) {
        AlertManager.showErrorMessage(activity, "Not enough quantity in Stock")
      } else {
        addUpdateQTY(cartModel,cartModel.qty+1)
//        Utils.vibrate(activity)
        AlertManager.showSuccessMessage(
          activity,
          "${activity.getString(R.string.product_added_to_cart)} "
        )

      }

      if (cartModel.productDetails.availability?.instant != null && cartModel.productDetails.availability?.instant?.isAvailable == true) {
        if (cartModel.qty == cartModel.productDetails.availability?.instant?.getQTY()!! + 1) {
          AlertManager.showInfoMessage(
            activity,
            activity.getString(R.string.express),
            "${cartModel.productDetails.title} ${activity.getString(R.string.delivery_has_been_converted)} ${activity.getString(
              R.string.express
            )}"
          )
        }
      }

    }
    saveCartToPref()
    cartItemsMut.value = cartItems
    calculateItemsCount()
  }

  fun minus(productDetails: ProductDetails, activity: Activity) {
    val cartModel =
      cartItems?.firstOrNull { cartModel -> cartModel.productDetails.id == productDetails.id }
    if (cartModel != null) {
      if (cartModel.qty > 1) {
        addUpdateQTY(cartModel,cartModel.qty - 1)
//        cartModel.qty -= 1
//        var tempCartModel = cartModel
//        cartItems?.remove(cartModel)
//        cartItems?.add(tempCartModel)

        if (cartModel.productDetails.availability?.instant != null && cartModel.productDetails.availability?.instant?.isAvailable == true) {
          if (cartModel.qty == cartModel.productDetails.availability?.instant?.getQTY()!!) {
            AlertManager.showInfoMessage(
              activity,
              activity.getString(R.string.instant),
              "${cartModel.productDetails.title} ${activity.getString(R.string.delivery_has_been_converted)} ${activity.getString(
                R.string.instant
              )}"

            )
          }
        }
//        Utils.vibrate(activity)
      } else {
        removeItem(productDetails, activity)
      }
    }
    saveCartToPref()
    cartItemsMut.value = cartItems
    calculateItemsCount()
  }

  fun checkPlus(productDetails: ProductDetails, activity: Activity, qty: Int) {
    val cartModel =
      cartItems?.firstOrNull { cartModel -> cartModel.productDetails.id == productDetails.id }
    if (cartModel != null) {
      if (cartModel.qty + qty >= cartModel.productDetails.availability?.express?.qty!!) {
        AlertManager.showErrorMessage(activity, "Not enough quantity in Stock")
      }
      if (cartModel.productDetails.availability?.instant != null && cartModel.productDetails.availability?.instant?.isAvailable == true) {
        if (cartModel.qty + qty == cartModel.productDetails.availability?.instant?.getQTY()!! + 1) {
          AlertManager.showInfoMessage(
            activity,
            activity.getString(R.string.express),
            "${cartModel.productDetails.title} ${activity.getString(R.string.delivery_will_converted)} ${activity.getString(
              R.string.express
            )}"
          )
        }
      }
    } else {
      if (qty >= productDetails.availability?.express?.qty!!) {
        AlertManager.showErrorMessage(activity, "Not enough quantity in Stock")
      }
      if (+qty == productDetails.availability?.instant?.getQTY()!! + 1) {
        AlertManager.showInfoMessage(
          activity,
          activity.getString(R.string.express),
          "${productDetails.title} ${activity.getString(R.string.delivery_will_converted)} ${activity.getString(
            R.string.express
          )}"
        )
      }
    }
  }

  fun checkMinus(productDetails: ProductDetails, activity: Activity, qty: Int) {
    val cartModel =
      cartItems?.firstOrNull { cartModel -> cartModel.productDetails.id == productDetails.id }
    if (cartModel != null) {
      if (cartModel.productDetails.availability?.instant != null && cartModel.productDetails.availability?.instant?.isAvailable == true) {
        if (cartModel.qty + qty == cartModel.productDetails.availability?.instant?.getQTY()!!) {
          AlertManager.showInfoMessage(
            activity,
            activity.getString(R.string.instant),
            "${cartModel.productDetails.title} ${activity.getString(R.string.delivery_will_converted)} ${activity.getString(
              R.string.instant
            )}"

          )
        }
      }
    } else {
      if (qty == productDetails.availability?.instant?.getQTY()!!) {
        AlertManager.showInfoMessage(
          activity,
          activity.getString(R.string.instant),
          "${productDetails.title} ${activity.getString(R.string.delivery_will_converted)} ${activity.getString(
            R.string.instant
          )}"

        )
      }
    }
  }

//  fun checkIfItemExistInCar(productDetails: ProductDetails): Boolean {
//    val cartModel =
//      cartItems?.firstOrNull { cartModel -> cartModel.productDetails.id == productDetails.id }
//    return cartModel != null
//
//  }
//
//  fun getItemQtyExisting(productDetails: ProductDetails): Int? {
//    return cartItems?.firstOrNull { cartModel -> cartModel.productDetails.id == productDetails.id }?.qty
//  }

  private fun saveCartToPref() {
    val cart = cartItems?.let { CartAll(it) }
    if (cart != null) {
      persistenceManager.saveCartList(cart)
    }
  }

//  fun loadCartFromPref() {
//    persistenceManager.getCartList()?.list?.let {
//      cartItems = it
//      cartItemsMut.value = cartItems
//    }
//    calculateItemsCount()
//  }

  fun selectUnselected(cartModel: CartModel) {
    val string =
      selectedItems.firstOrNull { selectedItems -> selectedItems == cartModel.productDetails.id }
    if (string != null) {
      selectedItems.remove(string)
    } else {
      selectedItems.add(cartModel.productDetails.id)
    }
    selectedItemsMut.value = selectedItems
//    refreshCart()
  }

//  fun selectedAll(boolean: Boolean) {
//    cartItems?.let {
//      if (boolean) {
//        for (item in it) {
//          val string =
//            selectedItems.firstOrNull { selectedItems -> selectedItems == item.productDetails.id }
//          if (string == null) {
//            selectedItems.add(item.productDetails.id)
//          }
//        }
//      } else {
//        selectedItems.clear()
//      }
//      selectedItemsMut.value = selectedItems
//    }
//
//  }

  fun isItemChecked(cartModel: CartModel): Boolean {
    val string = selectedItems.firstOrNull { string -> string == cartModel.productDetails.id }
    return string != null
  }
//
//  fun setCheckedItems() {
//    val checkedItems = ArrayList<CartModel>()
//    cartItems?.let { it ->
//      for (item in it) {
//        if (isItemChecked(item)) {
//          checkedItems.add(item)
//        }
//      }
//    }
//    selectedItemsCartMut.value = checkedItems
//  }

  fun getCalculateCounts() {
    var countInstant = 0
    selectedItemsCartMut.value?.let {
      if (!it.isNullOrEmpty()) {
        for (item in it) {
          if (item.productDetails.availability != null && item.productDetails.availability?.instant?.isAvailable == true && item.productDetails.availability?.instant?.getQTY() ?: 0 >= item.qty) {
            countInstant++
          }
        }
      }
    }
    instantCount.value = countInstant
  }

  fun changeDeliveryOption(instantOn: Boolean) {
    val instantProducts = ArrayList<CartModel>()
    var expressProducts = ArrayList<CartModel>()
    selectedItemsCartMut.value?.let {
      if (instantOn) {
        if (!it.isNullOrEmpty()) {
          for (item in it) {
            if (item.productDetails.availability != null && item.productDetails.availability?.instant?.isAvailable == true && item.productDetails.availability?.instant?.getQTY() ?: 0 >= item.qty) {
              instantProducts.add(item)

            } else {
              expressProducts.add(item)
            }
          }
        }
        instantCount.value = instantProducts.size
      } else {
        getCalculateCounts()
        expressProducts = it
      }
    }

    expressCount.value = expressProducts.size
    instantProductsMut.value = instantProducts
    expressProductsMut.value = expressProducts
  }

  private fun calculateItemsCount() {
    var count = 0
    cartItemsMut.value?.let {
      if (it.isNotEmpty()) {
        for (item in it) {
          count += item.qty
        }
      }
    }
    cartQtyCountMut.value = count
//    offersManagers.refresh()
  }

  private fun addUpdateQTY(cartModel: CartModel,qty: Int) {
    cartItems?.remove(cartModel)
    cartModel.qty = qty
    cartItems?.add(cartModel)

  }

  fun clearCart() {
    cartItems?.clear()
    selectedItems.clear()
    cartQtyCountMut.value = 0
    selectedItemsCartMut.value = cartItems
    selectedItemsMut.value = selectedItems
    cartItemsMut.value = cartItems
    saveCartToPref()
  }

//  private fun refreshCart() {
//    offersManagers.refresh()
//  }
}