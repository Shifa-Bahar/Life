package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.config.Config
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.vouchers.VoucherMainResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.lifepharmacy.application.utils.URLs
import retrofit2.Call

interface VoucherApi {

  @GET(URLs.VOUCHERS_LIST)
  suspend fun getVouchersList(
    @Query("skip") skip: String,
    @Query("take") take: String
  ): Response<GeneralResponseModel<VoucherMainResponse>>

  @GET(URLs.VOUCHERS_LIST)
  fun getVouchersListForCache(
    @Query("skip") skip: String,
    @Query("take") take: String
  ): Call<GeneralResponseModel<VoucherMainResponse>>

}
