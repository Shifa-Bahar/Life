package com.lifepharmacy.application.ui.returned.viewmdols

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.SelectedReturnType
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.orders.ReturnOrderModel
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.OrderRepository
import com.lifepharmacy.application.ui.utils.AppScrollState

/**
 * Created by Zahid Ali
 */
class ReturnOrdersViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: OrderRepository,
  application: Application,val scrollState: AppScrollState
) : BaseViewModel(application) {
  var skip = 0
  var take = 20
  var selectedOrderType = MutableLiveData<SelectedReturnType>()
  var selectReturnedOrderMut = MutableLiveData<ReturnOrderModel>()

  fun getShipments() =
    performNwOperation { repository.getShipment() }

  fun getReturnedRequestedOrderList() =
    performNwOperation { repository.getReturnOrderList(skip.toString(), take = take.toString(),status = "0") }


  fun getReturnedApprovedOrderList() =
    performNwOperation { repository.getReturnOrderList(skip.toString(), take = take.toString(),status = "1") }

}