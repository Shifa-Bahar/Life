package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.general.GeneralResponseModelWithoutData
import com.lifepharmacy.application.model.orders.*
import com.lifepharmacy.application.model.orders.suborder.SubOrderDetailForRating
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.review.RatingSubOrderRequest
import retrofit2.Response
import retrofit2.http.*
import com.lifepharmacy.application.utils.URLs
import okhttp3.ResponseBody

interface OrdersApi {
  @GET(URLs.ORDERS_LIST)
  suspend fun getOrdersList(
    @Query("skip") skip: String,
    @Query("take") take: String
  ): Response<GeneralResponseModel<ArrayList<OrderResponseModel>>>

  @GET(URLs.PRESCRIPTION_ORDERS_LIST)
  suspend fun getPrescriptionOrderList(
    @Query("skip") skip: String,
    @Query("take") take: String
  ): Response<GeneralResponseModel<ArrayList<PrescriptionOrder>>>

  @GET(URLs.ORDERS_LIST + "/{id}")
  suspend fun getOrderDetail(@Path("id") id: String): Response<GeneralResponseModel<OrderDetailResponseModel>>

  @POST(URLs.RETURN_ORDER)
  suspend fun returnOrder(@Body body: ReturnOrderRequestBody): Response<GeneralResponseModelWithoutData>

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

  @POST(URLs.RATING_PRODUCT + "/{id}")
  suspend fun rateProduct(
    @Path("id") id: String,
    @Body body: RateProductRequestBody
  ): Response<GeneralResponseModel<SubOrderDetailForRating>>


  @GET(URLs.RETURN_ORDER_LIST)
  suspend fun getReturnOrderList(
    @Query("skip") skip: String,
    @Query("take") take: String
  ): Response<GeneralResponseModel<ArrayList<ReturnOrderModel>>>


  @GET(URLs.BUY_IT_AGAIN)
  suspend fun getProductsBuyAgain(
  ): Response<GeneralResponseModel<ArrayList<ProductDetails>>>

  @Streaming
  @GET
  suspend fun downloadInvoice(@Url fileUrl: String): Response<ResponseBody>


  @GET(URLs.SUB_ORDER_DETAIL + "/{id}")
  suspend fun getSubOrderDetail(
    @Path("id") id: String
  ): Response<GeneralResponseModel<SubOrderDetail>>
}
