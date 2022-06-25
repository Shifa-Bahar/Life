package com.lifepharmacy.application.ui.rating

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.enums.RatingFragmentState
import com.lifepharmacy.application.model.orders.SubOrderItem
import com.lifepharmacy.application.repository.OrderRepository

/**
 * Created by Zahid Ali
 */
class RatingViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: OrderRepository,
  application: Application
) : BaseViewModel(application) {
  var ratingState = MutableLiveData<RatingFragmentState>()
  var selectedSubOrderItem = MutableLiveData<SubOrderItem>()
  var mainRatingTag = MutableLiveData<String>()
  var showProducts = MutableLiveData<Boolean>()
  var isDriverRated = MutableLiveData<Boolean>()
  var isReviewDriver = MutableLiveData<Boolean>()

  var selectedTagPositions = ArrayList<Int>()

  var subOrderId: String? = ""
  var ratingMain: Float? = 1.0F


  fun getSubOrderDetail() =
    performNwOperation { repository.getSubOrderDetail(subOrderId ?: "") }

}
