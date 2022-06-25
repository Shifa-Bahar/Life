package com.lifepharmacy.application.ui.whishlist.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.managers.WishListManager
import com.lifepharmacy.application.model.NotifyRequestModel
import com.lifepharmacy.application.model.WishListAll
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.wishlist.AddWishListRequestBody
import com.lifepharmacy.application.model.wishlist.DeleteWishListBody
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.ProductListRepository
import com.lifepharmacy.application.repository.ProductRepository
import com.lifepharmacy.application.repository.WishListRepository
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import okhttp3.Response

/**
 * Created by Zahid Ali
 */
class WishListViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  application: Application
) : BaseViewModel(application){

}