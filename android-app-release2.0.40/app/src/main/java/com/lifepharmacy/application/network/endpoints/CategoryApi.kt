package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.category.CategoryMainModel
import com.lifepharmacy.application.model.category.RootCategory
import com.lifepharmacy.application.model.general.GeneralResponseModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import com.lifepharmacy.application.utils.URLs

interface CategoryApi {
  @GET(URLs.CATEGORIES)
  suspend fun requestCategories(): Response<GeneralResponseModel<ArrayList<CategoryMainModel>>>


  @GET(URLs.CATEGORIES)
  fun requestRootCategories(): Call<GeneralResponseModel<ArrayList<RootCategory>>>


  @GET(URLs.ROOT_CATEGORY)
  suspend fun getRootCategories(): Response<GeneralResponseModel<ArrayList<RootCategory>>>

  @GET(URLs.CATEGORY_BY_ID+"/{categoryID}")
  suspend fun getSubCategories(@Path("categoryID") categoryID: String): Response<GeneralResponseModel<CategoryMainModel>>
}
