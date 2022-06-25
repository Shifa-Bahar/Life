package com.lifepharmacy.application.ui.pages.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.SelectedReturnType
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.orders.ReturnOrderModel
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.OrderRepository
import com.lifepharmacy.application.repository.PageRepository
import com.lifepharmacy.application.ui.utils.AppScrollState

/**
 * Created by Zahid Ali
 */
class PageViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: PageRepository,
  application: Application
) : BaseViewModel(application) {

  var slug = MutableLiveData<String>()
  fun getPage() =
    performNwOperation { repository.requestPage(slug = slug.value?:"") }

}