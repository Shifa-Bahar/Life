package com.lifepharmacy.application.ui.orders.viewmodels

import android.app.Application
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.OrderRepository
import java.io.File

/**
 * Created by Zahid Ali
 */
class ReturnReasonViewModel
@ViewModelInject
constructor(
    val appManager: AppManager,
    private  val repository: OrderRepository,
    application: Application
) : BaseViewModel(application) {


}