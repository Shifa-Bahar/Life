package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.*
import com.lifepharmacy.application.model.cart.CreateCartRequest
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.home.HomeProductFeedRequest
import com.lifepharmacy.application.model.notifications.ChangeStatusRequestBody
import com.lifepharmacy.application.model.response.RecommendedMainModel
import com.lifepharmacy.application.network.endpoints.BlogApi
import com.lifepharmacy.application.network.endpoints.HomeApi
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import com.lifepharmacy.application.utils.NetworkUtils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class HomeRepository
@Inject constructor(
  private val networkUtils: NetworkUtils,
  private val homeApi: HomeApi,
  private val blogApi: BlogApi
) :
  BaseRepository() {

  suspend fun getHomeData() =
    getResult({ homeApi.getHomeData() }, networkUtils)

  suspend fun getLandingPage(id: String) =
    getResult({ homeApi.getLandingPage(id) }, networkUtils)

  suspend fun getInapmsgRating() =
    getResult({ homeApi.getInapmsgRating() }, networkUtils)

  suspend fun getProductDetails(productId: String) =
    getResult({ homeApi.requestProduct(productId) }, networkUtils)

  suspend fun getBlog() =
    getResultWithoutGeneralResponseModel({ blogApi.getBlog() }, networkUtils)

  suspend fun createCart(body: CreateCartRequest) =
    getResult({ homeApi.createCart(body) }, networkUtils)


  suspend fun updateCart(body: CreateCartRequest) =
    getResult({ homeApi.updateCart(body) }, networkUtils)

  suspend fun getCartDetails(id: String) =
    getResult({ homeApi.getCartDetails(id) }, networkUtils)

  suspend fun getHomeFeed(body: HomeProductFeedRequest) =
    getResult({ homeApi.homeProductFeed(body) }, networkUtils)

  suspend fun getUserMessages() =
    getResult({ homeApi.getUserMassages() }, networkUtils)


  suspend fun changeUserMessageStatus(body: ChangeStatusRequestBody) =
    getResponseBodyResultWithOutReturn({ homeApi.changeUserMessageStatus(body) }, networkUtils)


  fun getRecommended(
    handler: HandleNetworkCallBack<GeneralResponseModel<RecommendedMainModel>>
  ) {

    if (!networkUtils.isConnectedToInternet) {
      handler.handleWebserviceCallBackFailure(networkUtils.networkErrorMessage)
      return
    }

    job = Job()

    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val getSession = homeApi.getRecommended()
          getSession.enqueue(object :
            Callback<GeneralResponseModel<RecommendedMainModel>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<RecommendedMainModel>>,
              response: Response<GeneralResponseModel<RecommendedMainModel>>
            ) {
              if (response.isSuccessful && response.code() < 400) {

                handler.handleWebserviceCallBackSuccess(response)


              } else {
                // Handle error returned from server
                handler.handleWebserviceCallBackFailure(
                  response.errorBody().toString()
                )
              }

            }

            override fun onFailure(
              call: Call<GeneralResponseModel<RecommendedMainModel>>,
              t: Throwable
            ) {
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

  suspend fun getProducts() =
    getResultMock {
      var productModel = ProductModel()
      var arrayList = ArrayList<ProductModel>()
      arrayList.add(productModel)
      arrayList.add(productModel)
      arrayList.add(productModel)
      arrayList.add(productModel)
      arrayList.add(productModel)
      arrayList.add(productModel)
      arrayList.add(productModel)
      GeneralResponseModel(arrayList, "No result found", true)
    }


// Image URL with caption

}