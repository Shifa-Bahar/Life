package com.lifepharmacy.application.repository

import android.widget.Toast
import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.wishlist.AddWishListRequestBody
import com.lifepharmacy.application.model.wishlist.DeleteWishListBody
import com.lifepharmacy.application.network.endpoints.WishListApi
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import com.lifepharmacy.application.utils.NetworkUtils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class EventRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val wishListApi: WishListApi) :
  BaseRepository() {


  fun eventcallNetwork(
    params:Any,
    handler: HandleNetworkCallBack<GeneralResponseModel<ProductDetails>>
  ) {

    if (!networkUtils.isConnectedToInternet) {
      handler.handleWebserviceCallBackFailure(networkUtils.networkErrorMessage)
      return
    }

    job = Job()

    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val getSession = wishListApi.eventcallList(params)
          getSession.enqueue(object :
            Callback<GeneralResponseModel<ProductDetails>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<ProductDetails>>,
              response: Response<GeneralResponseModel<ProductDetails>>
            ) {
              if (response.isSuccessful && response.code() < 400) {

//                handler.handleWebserviceCallBackSuccess()


              } else {
                // Handle error returned from server
                handler.handleWebserviceCallBackFailure(
                  response.errorBody().toString()
                )
              }

            }

            override fun onFailure(call: Call<GeneralResponseModel<ProductDetails>>, t: Throwable) {
              t.printStackTrace()
              handler.handleWebserviceCallBackFailure("Internal Server Error")
            }
          })
        } catch (e: Exception) {
          e.printStackTrace()
          handler.handleWebserviceCallBackFailure("Internal Server Error")
        }
        withContext(Dispatchers.Main) {
          theJob.complete()
        }
      }

    }

  }

}