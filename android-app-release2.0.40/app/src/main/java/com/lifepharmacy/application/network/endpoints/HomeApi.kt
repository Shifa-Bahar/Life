package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.cart.CartResponseModel
import com.lifepharmacy.application.model.cart.CreateCartRequest
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.general.GeneralResponseModelWithoutData
import com.lifepharmacy.application.model.home.HomeProductFeedRequest
import com.lifepharmacy.application.model.home.HomeProductFeedResponse
import com.lifepharmacy.application.model.home.HomeResponseItem
import com.lifepharmacy.application.model.inappmsg.Inappmsgrating
import com.lifepharmacy.application.model.notifications.ChangeStatusRequestBody
import com.lifepharmacy.application.model.notifications.InAppNotificationDataModel
import com.lifepharmacy.application.model.notifications.InAppNotificationMainModel
import com.lifepharmacy.application.model.notifications.MessagesMainModel
import com.lifepharmacy.application.model.pcr.pcrlist.PcrData
import com.lifepharmacy.application.model.product.ProductDetail
import com.lifepharmacy.application.model.response.RecommendedMainModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import com.lifepharmacy.application.utils.URLs

interface HomeApi {

  @GET(URLs.HOME)
  suspend fun getHomeData(): Response<GeneralResponseModel<ArrayList<HomeResponseItem>>>

  @GET(URLs.LANDING_PAGE + "{id}")
  suspend fun getLandingPage(@Path("id") id: String): Response<GeneralResponseModel<ArrayList<HomeResponseItem>>>

  @GET(URLs.INAPPMSG)
  suspend fun getInapmsgRating(): Response<GeneralResponseModel<Inappmsgrating>>

  @GET(URLs.PRODUCTS + "/{productID}")
  suspend fun requestProduct(@Path("productID") productID: String): Response<GeneralResponseModel<ProductDetail>>

  @GET(URLs.TRENDING_SEARCH)
  fun getRecommended(): Call<GeneralResponseModel<RecommendedMainModel>>


  @POST(URLs.CREATE_CART)
  suspend fun createCart(@Body body: CreateCartRequest): Response<GeneralResponseModel<CartResponseModel>>

  @POST(URLs.HOME_PRODUCTS_FEED)
  suspend fun homeProductFeed(@Body body: HomeProductFeedRequest): Response<GeneralResponseModel<HomeProductFeedResponse>>

  @POST(URLs.UPDATE_CART)
  suspend fun updateCart(@Body body: CreateCartRequest): Response<GeneralResponseModel<CartResponseModel>>

  @GET(URLs.CART + "{id}")
  suspend fun getCartDetails(@Path("id") id: String): Response<GeneralResponseModel<CartResponseModel>>

  @GET(URLs.USER_MESSAGES)
  suspend fun getUserMassages(): Response<GeneralResponseModel<MessagesMainModel>>


  @POST(URLs.USER_MESSAGES_CHANGE_STATUS)
  suspend fun changeUserMessageStatus(@Body body: ChangeStatusRequestBody): Response<GeneralResponseModelWithoutData>

}
