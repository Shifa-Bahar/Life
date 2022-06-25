package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.payment.CardDeleteBody
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.model.payment.ReturnToCardRequest
import com.lifepharmacy.application.network.endpoints.WalletApi
import com.lifepharmacy.application.utils.HandleNetworkCallBack

import com.lifepharmacy.application.utils.NetworkUtils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class WalletRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val walletApi: WalletApi) :
  BaseRepository() {

  suspend fun createTransaction(body: TransactionModel) =
    getResult({ walletApi.topUp(body) }, networkUtils)

  suspend fun createTransactionNew(body: TransactionModel) =
    getResult({ walletApi.createTransactionNew(body) }, networkUtils)

  suspend fun returnToCard(body: ReturnToCardRequest) =
    getResult({ walletApi.returnToCard(body) }, networkUtils)

  fun getCards(
    handler: HandleNetworkCallBack<GeneralResponseModel<ArrayList<CardMainModel>>>
  ) {

    if (!networkUtils.isConnectedToInternet) {
      handler.handleWebserviceCallBackFailure(networkUtils.networkErrorMessage)
      return
    }

    job = Job()

    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val getSession = walletApi.getCards()
          getSession.enqueue(object :
            Callback<GeneralResponseModel<ArrayList<CardMainModel>>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<ArrayList<CardMainModel>>>,
              response: Response<GeneralResponseModel<ArrayList<CardMainModel>>>
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
              call: Call<GeneralResponseModel<ArrayList<CardMainModel>>>,
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


  suspend fun deleteCard(body: CardDeleteBody) =
    getResult({ walletApi.deleteCard(body) }, networkUtils)

  suspend fun getTransactions(skip: String, take: String) =
    getResult({ walletApi.transactions(skip, take) }, networkUtils)
}