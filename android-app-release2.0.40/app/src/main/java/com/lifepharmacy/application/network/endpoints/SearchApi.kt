package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.search.SearchMainModel
import retrofit2.Response
import retrofit2.http.*
import com.lifepharmacy.application.utils.URLs
interface SearchApi {

  @GET(URLs.SEARCH+"/{term}")
  suspend fun search( @Path("term") term: String,@Query("skip")skip:String, @Query("take")take:String): Response<GeneralResponseModel<SearchMainModel>>

}
