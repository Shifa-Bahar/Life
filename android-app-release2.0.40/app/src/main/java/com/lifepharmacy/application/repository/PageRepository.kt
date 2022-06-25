package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.network.endpoints.PageApi
import com.lifepharmacy.application.utils.NetworkUtils
import javax.inject.Inject

class PageRepository
@Inject constructor(val networkUtils: NetworkUtils, private val api: PageApi) :
  BaseRepository() {


  suspend fun requestPage(slug: String) =
    getResult({ api.getPage(slug) }, networkUtils)

}