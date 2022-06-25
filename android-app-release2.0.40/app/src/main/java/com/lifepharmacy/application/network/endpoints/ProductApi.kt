package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.NotifyRequestModel
import com.lifepharmacy.application.model.events.Eventres
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.general.GeneralResponseModelWithoutData
import com.lifepharmacy.application.model.product.ProductDetail
import com.lifepharmacy.application.model.product.ProductReview
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import retrofit2.Response
import retrofit2.http.*
import com.lifepharmacy.application.utils.URLs

interface ProductApi {
  @GET(URLs.PRODUCTS + "/{productID}")
  suspend fun requestProduct(@Path("productID") productID: String): Response<GeneralResponseModel<ProductDetail>>

  @POST(URLs.NOTIFY_PRODUCT)
  suspend fun notifyProduct(@Body body: NotifyRequestModel): Response<GeneralResponseModelWithoutData>

  @GET(URLs.PRODUCTS_REVIEWS + "/{productID}/ratings")
  suspend fun getProductReviews(
    @Path("productID") productID: String,
    @Query("skip") skip: String,
    @Query("take") take: String
  ): Response<GeneralResponseModel<ArrayList<ProductReview>>>
}
