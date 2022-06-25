package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.profile.UpdateUserRequestModel
import com.lifepharmacy.application.model.request.NumberOTPRequest
import com.lifepharmacy.application.model.request.NumberOTPVerifyRequest
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import com.lifepharmacy.application.network.endpoints.AccountApi
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import com.lifepharmacy.application.utils.NetworkUtils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AuthRepository
@Inject constructor(val networkUtils: NetworkUtils, private val authApi: AccountApi) :
  BaseRepository() {


  suspend fun sendOTP(number: NumberOTPRequest) =
    getResult({ authApi.requestOTP(number) }, networkUtils)

  suspend fun updateAndSendOTP(number: NumberOTPRequest) =
    getResult({ authApi.updateUserAndSendOTP(number) }, networkUtils)

  suspend fun verifyOTP(otp: NumberOTPVerifyRequest) =
    getResult({ authApi.verifyNumberOTP(otp) }, networkUtils)

  suspend fun updateUser(body: UpdateUserRequestModel) =
    getResult({ authApi.updateUser(body) }, networkUtils)


  fun updateUser(
    handler: HandleNetworkCallBack<GeneralResponseModel<VerifyOTPResponse>>
  ) {

    if (!networkUtils.isConnectedToInternet) {
      handler.handleWebserviceCallBackFailure(networkUtils.networkErrorMessage)
      return
    }

    job = Job()

    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val getSession = authApi.updateUser()
          getSession.enqueue(object :
            Callback<GeneralResponseModel<VerifyOTPResponse>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<VerifyOTPResponse>>,
              response: Response<GeneralResponseModel<VerifyOTPResponse>>
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

            override fun onFailure(call: Call<GeneralResponseModel<VerifyOTPResponse>>, t: Throwable) {
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

//    suspend fun getProducts() =
//        getResultMock {
//            var productModel = ProductModel()
//            var arrayList = ArrayList<ProductModel>()
//            arrayList.add(productModel)
//            arrayList.add(productModel)
//            arrayList.add(productModel)
//            GeneralResponseModel(arrayList, "Please Check Your Phone for OTP", true)
//        }
}