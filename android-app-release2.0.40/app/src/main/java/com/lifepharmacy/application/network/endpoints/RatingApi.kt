package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.general.GeneralResponseModelWithoutData
import com.lifepharmacy.application.model.orders.*
import com.lifepharmacy.application.model.orders.suborder.SubOrderDetailForRating
import com.lifepharmacy.application.model.profile.UpdateUserRequestModel
import com.lifepharmacy.application.model.request.NumberOTPRequest
import com.lifepharmacy.application.model.request.NumberOTPVerifyRequest
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import com.lifepharmacy.application.model.review.AllRatingRequestBody
import com.lifepharmacy.application.model.review.RatingSubOrderRequest
import com.lifepharmacy.application.utils.URLs
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RatingApi {

  @POST(URLs.RATING_PRODUCT + "/{id}")
  suspend fun rateProduct(
    @Path("id") id: String,
    @Body body: RateProductRequestBody
  ): Response<GeneralResponseModel<SubOrderDetailForRating>>


  @POST(URLs.RATING_SHIPMENT + "/{id}")
  suspend fun rateShipment(
    @Path("id") id: String,
    @Body body: RateShipmentRequestBody
  ): Response<GeneralResponseModel<SubOrderDetailForRating>>

  @POST(URLs.RATING_SUBORDER + "/{id}")
  suspend fun rateSubOrder(
    @Path("id") id: String,
    @Body body: RatingSubOrderRequest
  ): Response<GeneralResponseModel<SubOrderDetailForRating>>


  @POST(URLs.RATING_ALL)
  suspend fun rateOverAll(
    @Body body: AllRatingRequestBody
  ): Response<GeneralResponseModelWithoutData>
}
