package com.lifepharmacy.application.network.endpoints


import com.lifepharmacy.application.model.address.AddressMainModel
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.filters.FilterMainRequest
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.orders.PlaceOrderRequest
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.model.pcr.appoinmentlist.AppoinmentList
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Appoinmentdata
import com.lifepharmacy.application.model.pcr.pcrdatetime.BookingData
import com.lifepharmacy.application.model.pcr.pcrlist.PcrData
import com.lifepharmacy.application.model.pcr.pcrrequestmodel.PlacePcrOrderRequest
import com.lifepharmacy.application.model.product.ProductDetail
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.product.ProductListingMainModel
import com.lifepharmacy.application.model.rewards.RewardItem
import com.lifepharmacy.application.model.vouchers.VoucherMainResponse
import retrofit2.Response
import retrofit2.http.*
import com.lifepharmacy.application.utils.URLs
import retrofit2.Call


interface PcrlistingApi {
  //    @FormUrlEncoded
//  @GET(URLs.PCR_LIST)
//  suspend fun requestPcrProducts(): Response<GeneralResponseModel<PcrData>>

  @GET(URLs.PCR_LIST + "/{id}")
  suspend fun requestPcrProducts(@Path("id") id: String): Response<GeneralResponseModel<PcrData>>

  @GET(URLs.PCR_LIST_DATE+ "/{id}/slots")
  suspend fun requestPcrDatetime(@Path("id") id: String): Response<GeneralResponseModel<BookingData>>

  @GET(URLs.PCR_ADDRESS_LIST)
  suspend fun requestPcrAddress(): Response<GeneralResponseModel<AddressMainModel>>

  @POST(URLs.PCR_CREATE_ORDER + "/{id}/create")
  suspend fun createPcrOrder(@Path("id") id: String, @Body body: PlacePcrOrderRequest): Response<GeneralResponseModel<OrderResponseModel>>

  @POST(URLs.CREATE_PCR_TRANSACTION)
  suspend fun createPcrTransaction(@Body body: TransactionModel): Response<GeneralResponseModel<TransactionModel>>

  @GET(URLs.PCR_APPOINMENT_LIST)
  suspend fun getAppoinmentList(
    @Query("skip") skip: String,
    @Query("take") take: String
  ): Response<GeneralResponseModel<ArrayList<AppoinmentList>>>

  @GET(URLs.PCR_APPOINMENT_DETAILS + "{id}")
  suspend fun getPcrAppoinmentDetail(@Path("id") id: Int): Response<GeneralResponseModel<Appoinmentdata>>

}
