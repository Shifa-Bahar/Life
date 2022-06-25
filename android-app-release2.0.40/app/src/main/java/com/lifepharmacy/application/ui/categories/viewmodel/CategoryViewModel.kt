package com.lifepharmacy.application.ui.categories.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.category.RootCategory
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.CategoryRepository
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import retrofit2.Response

/**
 * Created by Zahid Ali
 */
class CategoryViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: CategoryRepository,
  application: Application
) : BaseViewModel(application) {
  var collapesItems = ArrayList<Int>()
  var categoryMut = MutableLiveData<Result<ArrayList<RootCategory>>>()
//
//  fun getMainCategories() =
//    performNwOperation { repository.getRootCategories() }

  fun getSubCategories(id: String) =
    performNwOperation { repository.getSubSubCategories(id) }

  fun requestRootCategories() {
    repository.getRootCategories(object :
      HandleNetworkCallBack<GeneralResponseModel<ArrayList<RootCategory>>> {
      override fun handleWebserviceCallBackSuccess(response: Response<GeneralResponseModel<ArrayList<RootCategory>>>) {
        categoryMut.value =
          Result(Result.Status.SUCCESS, response.body()?.data, response.body()?.message)
      }

      override fun handleWebserviceCallBackFailure(error: String?) {
        categoryMut.value = Result(Result.Status.ERROR, null, error)
      }

    })


  }

  fun setColapsedItems(item: Int) {
    if (collapesItems.contains(item)) {
      collapesItems.remove(item)
    } else {
      collapesItems.add(item)
    }
  }

  fun setFirstColapsedItems(item: Int) {
    if (collapesItems.contains(item)) {
      collapesItems.remove(item)
    } else {
      collapesItems.add(item)
    }
  }

  fun setAllColasped(int: Int) {
    for (i in 0..int) {
      setFirstColapsedItems(i)
    }
  }
}