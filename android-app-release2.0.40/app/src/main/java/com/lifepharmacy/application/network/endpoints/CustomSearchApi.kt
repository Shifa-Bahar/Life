package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.search.custome.CustomQuery
import com.lifepharmacy.application.model.search.custome.CustomSuggestions
import com.lifepharmacy.application.utils.URLs
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CustomSearchApi {

  @GET(URLs.SEARCH_SUGESTIONS)
  suspend fun getSuggestions(
    @Query("query") query: String,
  ): Response<CustomSuggestions>

  @GET(URLs.SEARCH_QUERY)
  suspend fun getSearchQuery( @Query("query") query: String,): Response<CustomQuery>
}