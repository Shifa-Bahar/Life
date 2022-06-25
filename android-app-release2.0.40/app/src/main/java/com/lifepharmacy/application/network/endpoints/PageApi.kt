package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.PageModel
import com.lifepharmacy.application.model.general.GeneralResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import com.lifepharmacy.application.utils.URLs
interface PageApi {
  @GET(URLs.PAGE + "/{slug}")
  suspend fun getPage(@Path("slug") slug: String): Response<GeneralResponseModel<PageModel>>
}
