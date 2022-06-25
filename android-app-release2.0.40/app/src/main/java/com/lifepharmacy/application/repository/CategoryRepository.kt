package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.category.RootCategory
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.network.endpoints.CategoryApi
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import com.lifepharmacy.application.utils.NetworkUtils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CategoryRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val api: CategoryApi) :
  BaseRepository() {


//  suspend fun getCategories() =
//    getResult({ api.requestCategories() }, networkUtils)
//
//  suspend fun getRootCategories() =
//    getResult({ api.getRootCategories() }, networkUtils)



  suspend fun getSubSubCategories(id:String) =
    getResult({ api.getSubCategories(id) }, networkUtils)


  fun getRootCategories(
    handler: HandleNetworkCallBack<GeneralResponseModel<ArrayList<RootCategory>>>
  ) {

    if (!networkUtils.isConnectedToInternet) {
      handler.handleWebserviceCallBackFailure(networkUtils.networkErrorMessage)
      return
    }

    job = Job()

    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val getSession = api.requestRootCategories()
          getSession.enqueue(object :
            Callback<GeneralResponseModel<ArrayList<RootCategory>>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<ArrayList<RootCategory>>>,
              response: Response<GeneralResponseModel<ArrayList<RootCategory>>>
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

            override fun onFailure(call: Call<GeneralResponseModel<ArrayList<RootCategory>>>, t: Throwable) {
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