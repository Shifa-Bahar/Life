package com.lifepharmacy.application.repository

import com.algolia.search.saas.Client
import com.algolia.search.saas.Index
import com.algolia.search.saas.Query
import com.google.gson.Gson
import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.search.agolia.SuggestionMainObject
import com.lifepharmacy.application.network.endpoints.SearchApi
import com.lifepharmacy.application.utils.NetworkUtils
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Logger
import javax.inject.Inject
import com.algolia.search.saas.*
import com.algolia.search.saas.CompletionHandler
import com.lifepharmacy.application.model.search.agolia.AlgoliaSearchResult
import com.lifepharmacy.application.network.endpoints.CustomSearchApi
import com.lifepharmacy.application.utils.HandleAlgoliaCallBack

class SearchRepository
@Inject constructor(
  private val networkUtils: NetworkUtils,
  private val api: SearchApi,
  private val customSearchApi: CustomSearchApi
) :
  BaseRepository() {
  private var client: Client = Client(ConstantsUtil.ANGOLIA_ID, ConstantsUtil.ANGOLIA_KEY)
  private var index: Index = client.getIndex(ConstantsUtil.ANGOLIA_SUGUESSTION_INDEX)
  suspend fun searchQuery(skip: String, take: String, query: String) =
    getResult({ api.search(query, skip, take) }, networkUtils)


  suspend fun getCustomTrending() =
    getCustomSearchSuggestion({ customSearchApi.getSuggestions("trending_searches") }, networkUtils)

  suspend fun getCustomQueryProducts(query: String) =
    getCustomSearchSuggestion({ customSearchApi.getSearchQuery(query) }, networkUtils)


  suspend fun getCustomSuggestion(query: String) =
    getCustomSearchSuggestion({ customSearchApi.getSuggestions(query) }, networkUtils)

  fun getAlgoliaTrending(handler: HandleAlgoliaCallBack) {
    val completionHandler =
      CompletionHandler { content, error ->
        Logger.d("AggoliaSearchResult", content.toString())
        try {
          val gson = Gson()
          val mainResult: SuggestionMainObject =
            gson.fromJson(content.toString(), SuggestionMainObject::class.java)
          Logger.d(
            "AggoliaSearchResult", mainResult
              .toString()
          )
          handler.handleWebserviceCallBackSuccess(mainResult.hits)

        } catch (e: Exception) {
          e.printStackTrace()
        }


      }
    index.searchAsync(Query(""), completionHandler)
  }

  fun getAlgoliaQuerySearch(query: String, handler: HandleAlgoliaCallBack) {
    val completionHandler =
      CompletionHandler { content, error ->
        try {
          Logger.d("AggoliaSearchResult", content.toString())
          val gson = Gson()
          val mainResult: AlgoliaSearchResult =
            gson.fromJson(content.toString(), AlgoliaSearchResult::class.java)
          Logger.d(
            "AggoliaSearchResult", mainResult
              .toString()
          )
          handler.handleWebserviceCallBackSuccess(mainResult.getAllHits())

        } catch (e: Exception) {
          e.printStackTrace()
        }


      }
    val queries: ArrayList<IndexQuery> = ArrayList()
    val query1 = Query(query).apply {
      clickAnalytics = true
      enablePersonalization = true
      hitsPerPage = 5

    }
    val query2 = Query(query).apply {
      clickAnalytics = true
      enablePersonalization = true
    }
    queries.add(
      IndexQuery(
        ConstantsUtil.ANGOLIA_SUGUESSTION_INDEX, query1
      )
    )
    queries.add(
      IndexQuery(ConstantsUtil.ANGOLIA_PRODUCT_INDEX, query2)
    )
    val strategy: Client.MultipleQueriesStrategy = Client.MultipleQueriesStrategy.NONE;
    client.multipleQueriesAsync(
      queries, strategy, completionHandler
    )
  }


}