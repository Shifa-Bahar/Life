package com.lifepharmacy.application.ui.buyitagain.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.NotifyRequestModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.OrderRepository
import com.lifepharmacy.application.repository.ProductListRepository
import com.lifepharmacy.application.repository.ProductRepository

/**
 * Created by Zahid Ali
 */
class BuyAgainViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: OrderRepository,
  private val productRepository: ProductRepository,
  application: Application
) : BaseViewModel(application) {

  fun getProducts() =
    performNwOperation { repository.getBuyAgainProducts() }

}