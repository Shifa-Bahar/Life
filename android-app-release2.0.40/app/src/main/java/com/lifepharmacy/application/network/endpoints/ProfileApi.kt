package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.address.AddressDeleteRequest
import com.lifepharmacy.application.model.address.AddressMainModel
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.address.SaveAddressModel
import com.lifepharmacy.application.model.docs.CreateDocRequestBody
import com.lifepharmacy.application.model.docs.DeleteDocRequestBody
import com.lifepharmacy.application.model.docs.DocumentModel
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.notifications.NotificationModel
import com.lifepharmacy.application.model.outlet.Outletdata
import com.lifepharmacy.application.model.payment.TransactionModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.lifepharmacy.application.utils.URLs
interface ProfileApi {
  @POST(URLs.SAVE_ADDRESS)
  suspend fun saveAddress(@Body body: AddressModel): Response<GeneralResponseModel<SaveAddressModel>>


  @GET(URLs.GET_ADDRESS)
  fun getUserAddress(): Call<GeneralResponseModel<AddressMainModel>>

  @POST(URLs.DELETE_ADDRESS)
  suspend fun deleteAddress(@Body body: AddressDeleteRequest): Response<GeneralResponseModel<String>>

  @GET(URLs.NOTIFICATION)
  suspend fun getNotifications(@Query("skip") skip: String,
                               @Query("take") take: String): Response<GeneralResponseModel<ArrayList<NotificationModel>>>

  @GET(URLs.GET_DOCS)
  suspend fun getDocuments(): Response<GeneralResponseModel<ArrayList<DocumentModel>>>

  @GET(URLs.OUTLET_DETAILS)
  suspend fun outletDetails(): Response<GeneralResponseModel<Outletdata>>

  @POST(URLs.CREATE_DOC)
  suspend fun createDoc(@Body body: CreateDocRequestBody): Response<GeneralResponseModel<ArrayList<DocumentModel>>>

  @POST(URLs.DELETE_DOC)
  suspend fun deleteDoc(@Body body: DeleteDocRequestBody): Response<GeneralResponseModel<ArrayList<DocumentModel>>>
}
