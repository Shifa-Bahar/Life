package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.payment.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.lifepharmacy.application.utils.URLs
interface WalletApi {

  @GET(URLs.USER_CARDS)
  fun getCards(): Call<GeneralResponseModel<ArrayList<CardMainModel>>>

  @POST(URLs.DELETE_CARD)
  suspend fun deleteCard(@Body body: CardDeleteBody): Response<GeneralResponseModel<String>>

  @GET(URLs.TRANSACTIONS)
  suspend fun transactions(@Query("skip")skip:String, @Query("take")take:String): Response<GeneralResponseModel<ArrayList<TransactionMainModel>>>

  @POST(URLs.CREATE_TRANSACTION)
  suspend fun topUp(@Body body: TransactionModel): Response<GeneralResponseModel<TransactionModel>>

  @POST(URLs.CREATE_TRANSACTION_NEW)
  suspend fun createTransactionNew(@Body body: TransactionModel): Response<GeneralResponseModel<TransactionModel>>

  @POST(URLs.RETURN_TO_CARD)
  suspend fun returnToCard(@Body body: ReturnToCardRequest): Response<GeneralResponseModel<TransactionModel>>
}
