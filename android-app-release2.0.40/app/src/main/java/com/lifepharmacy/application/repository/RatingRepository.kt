package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.orders.RateProductRequestBody
import com.lifepharmacy.application.model.orders.RateShipmentRequestBody
import com.lifepharmacy.application.model.review.AllRatingRequestBody
import com.lifepharmacy.application.model.review.RatingSubOrderRequest
import com.lifepharmacy.application.network.endpoints.RatingApi
import com.lifepharmacy.application.utils.NetworkUtils
import javax.inject.Inject

class RatingRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val ratingApi: RatingApi) :
  BaseRepository() {

  suspend fun rateShipment(orderID: String, body: RateShipmentRequestBody) =
    getResult({ ratingApi.rateShipment(orderID, body) }, networkUtils)

  suspend fun rateSubOrder(orderID: String, body: RatingSubOrderRequest) =
    getResult({ ratingApi.rateSubOrder(orderID, body) }, networkUtils)

  suspend fun rateProduct(orderID: String, body: RateProductRequestBody) =
    getResult({ ratingApi.rateProduct(orderID, body) }, networkUtils)


  suspend fun rateOverAll(body: AllRatingRequestBody) =
    getResponseBodyResult({ ratingApi.rateOverAll(body) }, networkUtils)

}