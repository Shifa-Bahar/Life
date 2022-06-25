package com.lifepharmacy.application.repository

import android.util.Log
import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.filters.FilterMainResponse
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.network.endpoints.SplashApi
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import com.lifepharmacy.application.utils.NetworkUtils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SplashRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val api: SplashApi) :
  BaseRepository() {
//
//    suspend fun getFilters() =
//        getResult ({api.requestFilters()},networkUtils)

  suspend fun checkToken() =
    getResult({ api.checkToken() }, networkUtils)

  fun getFilters(
    handler: HandleNetworkCallBack<GeneralResponseModel<FilterMainResponse>>
  ) {

    if (!networkUtils.isConnectedToInternet) {
      handler.handleWebserviceCallBackFailure(networkUtils.networkErrorMessage)
      return
    }

    job = Job()

    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val getSession = api.requestFilters()
          getSession.enqueue(object :
            Callback<GeneralResponseModel<FilterMainResponse>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<FilterMainResponse>>,
              response: Response<GeneralResponseModel<FilterMainResponse>>
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
              call: Call<GeneralResponseModel<FilterMainResponse>>,
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
}