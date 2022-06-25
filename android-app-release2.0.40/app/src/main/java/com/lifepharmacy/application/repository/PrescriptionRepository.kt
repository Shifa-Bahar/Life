package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.prescription.PrescriptionRequestBody
import com.lifepharmacy.application.network.endpoints.PrescriptionApi
import com.lifepharmacy.application.utils.NetworkUtils
import javax.inject.Inject

class PrescriptionRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val api: PrescriptionApi) :
  BaseRepository() {

  suspend fun uploadPrescription(body: PrescriptionRequestBody) =
    getResult ({api.uploadPrescription(body)},networkUtils)
//
//  suspend fun createOrder(body: PlaceOrderRequest) =
//    getResult({ cartApi.createOrder(body) }, networkUtils)
//
//  suspend fun createTransaction(body: TransactionModel) =
//    getResult({ cartApi.createTransaction(body) }, networkUtils)
//
//  suspend fun validateCoupon(body: ValidateCouponRequestBody) =
//    getResult ({cartApi.validateCoupon(body)},networkUtils)
}