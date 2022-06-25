package com.lifepharmacy.application.managers

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.facebook.appevents.AppEventsConstants
import com.lifepharmacy.application.managers.analytics.AnalyticsManagers
import com.lifepharmacy.application.managers.analytics.addToWishList
import com.lifepharmacy.application.managers.analytics.removeFromWishList
import com.lifepharmacy.application.model.WishListAll
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.wishlist.AddWishListRequestBody
import com.lifepharmacy.application.model.wishlist.DeleteWishListBody
import com.lifepharmacy.application.repository.WishListRepository
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.FaceBookAnalyticsUtilUtil
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import com.lifepharmacy.application.utils.universal.Logger
import javax.inject.Inject


class WishListManager
@Inject constructor(
  var persistenceManager: PersistenceManager,
  private var wishListRepository: WishListRepository,
  val analyticsManagers: AnalyticsManagers
) {

  var wishListItemsMut = MutableLiveData<ArrayList<ProductDetails>>()
  private var wishListItems: ArrayList<ProductDetails>? = ArrayList()

  fun selectUnselected(product: ProductDetails) {
    val productDetails =
      wishListItems?.firstOrNull { selectedItems -> selectedItems.id == product.id }
    if (productDetails != null) {
      wishListItems?.remove(productDetails)
      persistenceManager.wishlistselected = false
      removeProductToWishListNetwork(productDetails)
    } else {
      wishListItems?.add(product)
      persistenceManager.wishlistselected = true
      addProductToWishListNetwork(product)
    }
    wishListItems?.let { updateWishList(it) }
  }

  init {
    requestWishListList()
  }

  private fun addProductToWishListNetwork(productDetails: ProductDetails) {
    analyticsManagers.addToWishList(productDetails)
    wishListRepository.addToWishListNetwork(
      AddWishListRequestBody(productId = productDetails.id),
      object :
        HandleNetworkCallBack<GeneralResponseModel<ArrayList<ProductDetails>>> {
        override fun handleWebserviceCallBackFailure(error: String?) {
          Logger.e("Error", "$error ")
        }

        override fun handleWebserviceCallBackSuccess(response: retrofit2.Response<GeneralResponseModel<ArrayList<ProductDetails>>>) {
          response.body()?.data?.let { updateWishList(it) }
        }

      })
  }

  private fun removeProductToWishListNetwork(productDetails: ProductDetails) {
    analyticsManagers.removeFromWishList(productDetails)
    wishListRepository.deleteFromWishListNetwork(
      DeleteWishListBody(id = productDetails.id),
      object :
        HandleNetworkCallBack<GeneralResponseModel<ArrayList<ProductDetails>>> {
        override fun handleWebserviceCallBackSuccess(response: retrofit2.Response<GeneralResponseModel<ArrayList<ProductDetails>>>) {
          response.body()?.data?.let { updateWishList(it) }
        }

        override fun handleWebserviceCallBackFailure(error: String?) {
          Logger.e("Error", "$error ")
        }

      })
  }

  fun requestWishListList() {
    wishListRepository.getWishListItem(object :
      HandleNetworkCallBack<GeneralResponseModel<ArrayList<ProductDetails>>> {
      override fun handleWebserviceCallBackSuccess(response: retrofit2.Response<GeneralResponseModel<ArrayList<ProductDetails>>>) {
        response.body()?.data?.let { updateWishList(it) }
      }

      override fun handleWebserviceCallBackFailure(error: String?) {
        Logger.e("Error", "$error ")
      }
    })
  }


  private fun updateWishList(list: ArrayList<ProductDetails>) {
    wishListItems = list
    wishListItemsMut.value = list
    saveWishListToPref()
  }

  fun checkIfItemExistInWishList(productDetails: ProductDetails): Boolean {
    val product =
      wishListItems?.firstOrNull { cartModel -> cartModel.id == productDetails.id }
    return product != null
  }


  private fun saveWishListToPref() {
    val wishList = wishListItems?.let { WishListAll(it) }
    if (wishList != null) {
      persistenceManager.saveWishList(wishList)
    }
  }

  private fun setAnalyticsEvent(activity: Context, productDetails: ProductDetails) {
    FaceBookAnalyticsUtilUtil.triggerEvent(
      context = activity,
      appEventConstant = AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST,
      id = productDetails?.inventory?.sku ?: "",
      type = "Product",
      amount = productDetails?.getRegularPriceWithoutVAT(activity)
        ?: 0.0
    )
  }
}