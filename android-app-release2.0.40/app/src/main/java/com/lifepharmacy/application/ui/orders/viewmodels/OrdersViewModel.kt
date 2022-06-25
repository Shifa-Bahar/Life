package com.lifepharmacy.application.ui.orders.viewmodels

import android.app.Application
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.ImageUploadingType
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.orders.PrescriptionOrder
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.OrderRepository
import com.lifepharmacy.application.utils.universal.InputEditTextValidator

/**
 * Created by Zahid Ali
 */
class OrdersViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: OrderRepository,
  application: Application
) : BaseViewModel(application) {
  var isThereFiles = ObservableField<Boolean>()
  var cardFrontUrl = ObservableField<String>()
  var cardBackUrl = ObservableField<String>()
  var insuranceURl = ObservableField<String>()
  var insuranceBackUrl = ObservableField<String>()
  var selectedOrder = MutableLiveData<PrescriptionOrder>()
  var note: InputEditTextValidator =
    InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      null
    )

  var skip = 0
  var take = 30
  fun getOrders() =
    performNwOperation { repository.getOrders(skip.toString(), take.toString()) }

  fun getPrescriptionOrders() =
    performNwOperation { repository.getPrescriptionOrders(skip.toString(), take.toString()) }


}