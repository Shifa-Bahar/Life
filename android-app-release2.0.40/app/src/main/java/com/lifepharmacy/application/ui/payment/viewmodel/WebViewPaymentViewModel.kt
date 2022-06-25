package com.lifepharmacy.application.ui.payment.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.repository.PaymentRepository

/**
 * Created by Zahid Ali
 */
class WebViewPaymentViewModel @ViewModelInject constructor(
  val appManager: AppManager,
  application: Application,
  val repository: PaymentRepository
) : BaseViewModel(application) {


  //Changing Title of App bar according to state
  var titleOfAppbar: MutableLiveData<String> = MutableLiveData()

  override fun onCleared() {
    super.onCleared()
  }

//  fun getFilters(body: CreateTransactionRequestBody) =
//    performNwOperation { repository.createTransaction(body) }


  fun setTitleAppbar(title: String) {
    titleOfAppbar.value = title
  }

}