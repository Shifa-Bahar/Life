package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.blog.BlogModel
import com.lifepharmacy.application.utils.URLs
import retrofit2.Response
import retrofit2.http.GET

interface BlogApi {
  @GET(URLs.BLOG_LISTING)
  suspend fun getBlog(): Response<ArrayList<BlogModel>>
}
