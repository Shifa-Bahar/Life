package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.cart.*
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.orders.PlaceOrderRequest
import com.lifepharmacy.application.model.orders.outofstock.OutOfStockRequestBody
import com.lifepharmacy.application.model.payment.TransactionModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import com.lifepharmacy.application.utils.URLs

interface CartApi {
  @POST(URLs.CREATE_ORDER)
  suspend fun createOrder(@Body body: PlaceOrderRequest): Response<GeneralResponseModel<OrderResponseModel>>

  @POST(URLs.CREATE_TRANSACTION)
  suspend fun createTransaction(@Body body: TransactionModel): Response<GeneralResponseModel<TransactionModel>>

  @POST(URLs.CREATE_TRANSACTION_NEW)
  suspend fun createTransactionNew(@Body body: TransactionModel): Response<GeneralResponseModel<TransactionModel>>

  @POST(URLs.COUPON)
  suspend fun validateCoupon(@Body body: ValidateCouponRequestBody): Response<GeneralResponseModel<CouponModel>>

  @POST(URLs.CREATE_CART)
  suspend fun createCart(@Body body: CreateCartRequest): Response<GeneralResponseModel<CartResponseModel>>

  @POST(URLs.UPDATE_CART)
  suspend fun updateCart(@Body body: CreateCartRequest): Response<GeneralResponseModel<CartResponseModel>>

  @GET(URLs.CART + "{id}")
  suspend fun getCartDetails(@Path("id") id: String): Response<GeneralResponseModel<CartResponseModel>>

  @POST(URLs.CREATE_OUT_OF_STOCK_ORDER)
  suspend fun createOutOfStockOrder(@Body body: OutOfStockRequestBody): Response<GeneralResponseModel<OrderResponseModel>>
}
