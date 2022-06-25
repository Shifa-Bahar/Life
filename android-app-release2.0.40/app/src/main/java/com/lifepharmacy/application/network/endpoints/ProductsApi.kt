package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.filters.FilterMainRequest
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.product.ProductListingMainModel
import retrofit2.Response
import retrofit2.http.*
import com.lifepharmacy.application.utils.URLs
interface ProductsApi {
//    @FormUrlEncoded
    @POST(URLs.PRODUCTS)
    suspend fun requestProducts(@Query("skip")skip:String, @Query("take")take:String,@Body body: FilterMainRequest): Response<GeneralResponseModel<ProductListingMainModel>>


}
