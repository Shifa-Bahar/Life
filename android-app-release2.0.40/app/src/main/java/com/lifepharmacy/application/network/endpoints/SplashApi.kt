package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.category.RootCategory
import com.lifepharmacy.application.model.config.Config
import com.lifepharmacy.application.model.config.DeliverySlot
import com.lifepharmacy.application.model.config.SlotsMainModel
import com.lifepharmacy.application.model.events.EventsResponse
import com.lifepharmacy.application.model.filters.FilterMainResponse
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.home.HomeResponseItem
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.orders.PlaceOrderRequest
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import com.lifepharmacy.application.utils.URLs
import retrofit2.http.Body
import retrofit2.http.Path

interface SplashApi {
  @GET(URLs.FILTERS)
  fun requestFilters(): Call<GeneralResponseModel<FilterMainResponse>>

//  @GET(URLs.FILTERS)
//  suspend fun requestFilters(): Response<GeneralResponseModel<FilterMainResponse>>

  @POST(URLs.CHECK_TOKEN)
  suspend fun checkToken(): Response<GeneralResponseModel<VerifyOTPResponse>>

//  @Body body: PlaceOrderRequest
  @GET(URLs.CONFIG)
  fun getSettings(): Call<GeneralResponseModel<Config>>

  @GET(URLs.LANDING_PAGE + "{id}")
  fun getHomeData(@Path("id") id: String): Call<GeneralResponseModel<ArrayList<HomeResponseItem>>>

  @GET(URLs.ROOT_CATEGORY)
  fun requestRootCategories(): Call<GeneralResponseModel<ArrayList<RootCategory>>>

  @GET(URLs.AVAILABLE_SLOTS)
  fun getAvailableSlots(): Call<GeneralResponseModel<ArrayList<DeliverySlot>>>


  @GET(URLs.NEW_AVAILABLE_SLOTS)
  fun getNewAvailableSlots(): Call<GeneralResponseModel<SlotsMainModel>>
}
