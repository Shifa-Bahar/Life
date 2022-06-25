package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.network.endpoints.PaymentApi
import com.lifepharmacy.application.utils.NetworkUtils
import javax.inject.Inject

class PaymentRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val api: PaymentApi) :
    BaseRepository() {
//    suspend fun createTransaction(body:CreateTransactionRequestBody) =
//        getResult ({api.createTransaction(body)},networkUtils)
}