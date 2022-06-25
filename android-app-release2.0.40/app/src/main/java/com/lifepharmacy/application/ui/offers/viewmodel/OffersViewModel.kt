package com.lifepharmacy.application.ui.offers.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.ScrollingState
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.managers.OffersManagers
import com.lifepharmacy.application.model.filters.FilterMainRequest
import com.lifepharmacy.application.model.filters.FilterRequestModel
import com.lifepharmacy.application.repository.ProductListRepository
import com.lifepharmacy.application.ui.utils.AppScrollState

/**
 * Created by Zahid Ali
 */
class OffersViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: ProductListRepository,
  val offersManagers: OffersManagers,
  application: Application, private val scrollState: AppScrollState
) : BaseViewModel(application) {
  var skip = 0
  var take = 30
  var searchQuery: String? = ""
  fun getScrollStateRepo(): AppScrollState {
    return scrollState
  }

  fun getScrollStateMut(): MutableLiveData<ScrollingState> {
    return scrollState.getScrollingState()
  }


  fun getProducts() =
    performNwOperation {
      repository.getProducts(
        skip.toString(),
        take.toString(),
        getAppliedFilter()
      )
    }

  private fun getAppliedFilter(): FilterMainRequest {
    var appliedFilterMain = FilterMainRequest()
    appliedFilterMain.filters?.add(FilterRequestModel(key = "offer",value = offersManagers.selectedOffer.value?.offers?.id.toString(),condition = "="))
    appliedFilterMain.search = searchQuery
    return appliedFilterMain
  }
}