package com.lifepharmacy.application.ui.search.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.SearchState
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.managers.FiltersManager
import com.lifepharmacy.application.model.search.agolia.Hits
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.SearchRepository
import com.lifepharmacy.application.utils.universal.GpsStatusListener
import com.lifepharmacy.application.model.home.Images
import com.lifepharmacy.application.model.response.RecommendedMainModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.utils.HandleAlgoliaCallBack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Created by Zahid Ali
 */
class SearchViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  application: Application,
  var repository: SearchRepository,
  var filerManager: FiltersManager,
  val gpsStatusListener: GpsStatusListener
) : BaseViewModel(application) {
  var skip = 0
  var take = 30
  var searchState = MutableLiveData<SearchState>()
  var suggestions = MutableLiveData<ArrayList<Hits>?>()
  var trendings = MutableLiveData<ArrayList<Hits>?>()
  var isSearchEmpty = MutableLiveData<Boolean>()


  fun searchQuery(string: String) =
    performNwOperation { repository.searchQuery(skip.toString(), take.toString(), string) }


  fun getSuggestions(query: String) {
    if (appManager.storageManagers.config.activeEngine != "algolia") {
      //testing
      val hits = ArrayList<Hits>()
      var hitsSuggestions: ArrayList<Hits>
      var hitProducts: ArrayList<Hits>
      CoroutineScope(Dispatchers.IO).launch {
        val responseStatus = repository.getCustomSuggestion(query = query)
        if (responseStatus.status == Result.Status.SUCCESS) {
          val data = responseStatus.data
          data?.let {
            hitsSuggestions = it.results?.map { it ->
              Hits(
                query = it.query?.raw,
                index = "products_query_suggestions"
              )
            } as ArrayList<Hits>
//            hits.addAll(hitsSuggestions)
            var newArr = hitsSuggestions.take(5)
            hits.addAll(newArr)
            val productResponse = repository.getCustomQueryProducts(query = query)
            if (responseStatus.status == Result.Status.SUCCESS) {
              val dataProducts = productResponse.data
              dataProducts?.let {
                hitProducts = it.results?.map { product ->
                  Hits(
                    title = product.title?.raw ?: "",
                    images = Images(featuredImage = product.images?.getJsonToImageUrl() ?: ""),
                    id = product.id?.raw,
                    index = "products"
                  )
                } as ArrayList<Hits>
                hits.addAll(hitProducts)
                CoroutineScope(Dispatchers.Main.immediate).launch {
                  suggestions.value = hits
                  searchState.value = SearchState.SUGGESTIONS
                }

              }

            }
          }
        }
      }
//
//      repository.getAlgoliaQuerySearch(query, object : HandleAlgoliaCallBack {
//        override fun handleWebserviceCallBackSuccess(hits: ArrayList<Hits>?) {
//          searchState.value = SearchState.SUGGESTIONS
//          suggestions.value = hits
//        }
//
//      })
    } else {
      repository.getAlgoliaQuerySearch(query, object : HandleAlgoliaCallBack {
        override fun handleWebserviceCallBackSuccess(hits: ArrayList<Hits>?) {
          searchState.value = SearchState.SUGGESTIONS
          suggestions.value = hits
        }

      })
    }
  }

  private fun setTrendingFromMain(recomendations: RecommendedMainModel) {
    val hits = recomendations.trendings?.map { it ->
      Hits(
        query = it
      )
    }
    trendings.value = hits as ArrayList<Hits>
    searchState.value = SearchState.RECOMMENDED
  }

  fun setTrending(recomendations: RecommendedMainModel? = null) {
    if (appManager.storageManagers.config.activeEngine != "algolia") {
      CoroutineScope(Dispatchers.IO).launch {
        val responseStatus = repository.getCustomTrending()
        if (responseStatus.status == Result.Status.SUCCESS) {
          val data = responseStatus.data
          data?.let {
            val hits = it.results?.map { it ->
              Hits(
                query = it.query?.raw,
                index = "products_query_suggestions"
              )
            }
            CoroutineScope(Dispatchers.Main.immediate).launch {
              trendings.value = hits as ArrayList<Hits>
              searchState.value = SearchState.RECOMMENDED
            }

          }
        }
      }
//      setTrendingFromMain(recomendations)
    } else {
      repository.getAlgoliaTrending(object : HandleAlgoliaCallBack {
        override fun handleWebserviceCallBackSuccess(hits: ArrayList<Hits>?) {
          searchState.value = SearchState.RECOMMENDED
          trendings.value = hits
        }

      })
    }
  }
}
