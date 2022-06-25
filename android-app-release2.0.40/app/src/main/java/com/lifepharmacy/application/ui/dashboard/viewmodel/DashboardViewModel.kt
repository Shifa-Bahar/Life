package com.lifepharmacy.application.ui.dashboard.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.AddressChanged
import com.lifepharmacy.application.enums.PopupClicked
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.managers.CartManager
import com.lifepharmacy.application.managers.OffersManagers
import com.lifepharmacy.application.model.cart.CartItemRequestModel
import com.lifepharmacy.application.model.cart.CreateCartRequest
import com.lifepharmacy.application.model.notifications.*
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.network.performNwOperationWithoutReturn
import com.lifepharmacy.application.repository.HomeRepository

/**
 * Created by Zahid Ali
 */
class DashboardViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val dashboardRepository: HomeRepository,
  val offersManagers: OffersManagers,
  application: Application
) : BaseViewModel(application) {

  var isExpanded = MutableLiveData<Boolean>()
  var showStickyMessages = MutableLiveData<Boolean>()
  var isMainView = MutableLiveData<Boolean>()
  var openReviewBox = MutableLiveData<Boolean>()
  var notificationModel = MutableLiveData<InAppNotificationMainModel>()
  var notificationInAppList = ArrayList<InAppNotificationMainModel>()
  var buttonNavigation = MutableLiveData<InAppNotificationDataModel>()
  var notificationPayLoad = MutableLiveData<NotificationPayLoad>()
  var lastSelectBottomSheetIndex = 0
  var selectedFragmentIndexMut = MutableLiveData<Int>()
  var messageCount = MutableLiveData<Int>()
  var isThereSupportMessage = MutableLiveData<Boolean>()
  var selectedFragmentIndex = 0
  var popupClicked = MutableLiveData<PopupClicked>()
  var inAppPopup = MutableLiveData<ArrayList<InAppNotificationMainModel>>()
  var inAppSticky = MutableLiveData<ArrayList<StickyMessageModel>>()
  fun getCartDetails() =
    performNwOperation { dashboardRepository.getCartDetails(appManager.persistenceManager.getCartID()) }

  fun updateCart() =
    performNwOperation { dashboardRepository.updateCart(makeCreateCartModel()) }

  fun createCart() =
    performNwOperation { dashboardRepository.createCart(makeCreateCartModel()) }

  fun getUserMessages() =
    performNwOperation { dashboardRepository.getUserMessages() }


  fun changeStatusOfMessage(status: String, id: String) =
    performNwOperationWithoutReturn(
      {
        dashboardRepository.changeUserMessageStatus(
          makeUpdateUserMessageRequestBody(
            status = status,
            id = id
          )
        )
      },
      viewModelScope
    )

  private fun makeUpdateUserMessageRequestBody(
    status: String,
    id: String
  ): ChangeStatusRequestBody {
    return ChangeStatusRequestBody(
      messageId = id,
      action = status
    )
  }


  private fun makeCreateCartModel(): CreateCartRequest {
    var createCartRequest = CreateCartRequest()
    var items = ArrayList<CartItemRequestModel>()
    offersManagers.getAllProductsWithQTY().let { it ->
      for (item in it) {
        items.add(CartItemRequestModel(id = item.productDetails.id, qty = item.qty))
      }
    }
    if (appManager.persistenceManager.isThereCart()) {
      createCartRequest.cartID = appManager.persistenceManager.getCartID()
    }
    if (appManager.persistenceManager.isLoggedIn()) {
      createCartRequest.userID = appManager.persistenceManager.getLoggedInUser()?.id.toString()
    } else {
      createCartRequest.userID = appManager.persistenceManager.getFCMToken()
    }
    createCartRequest.items = items
    return createCartRequest
  }

  fun addInAppNotificationQue(notificationPayLoad: InAppNotificationMainModel) {
    val existNotification = notificationInAppList.firstOrNull {
      it.messageId == notificationPayLoad.messageId
    }

    if (existNotification == null) {
      notificationInAppList.add(notificationPayLoad)
    }
  }

  fun filterUserMessagesTypes(messages: MessagesMainModel) {
    if (notificationInAppList.isNullOrEmpty()) {
      messages.inAppMessages?.let {
        val filteredArray =
          messages.inAppMessages?.filter { item -> item.isDismissed == false && !item.isExpires() }
        filteredArray?.let {
          notificationInAppList.addAll(it)
        }
      }
    }
    if (inAppSticky.value.isNullOrEmpty()) {
      messages.stickyMessages?.let {
        val filteredArray =
          messages.stickyMessages?.filter { item -> item.isDismissed == false && !item.isExpires() }
        filteredArray?.let {
          inAppSticky.value = it as ArrayList<StickyMessageModel>
        }

      }
    }
  }

  fun removeInAppPopupQueItem() {
//    notificationInAppList.removeFirst()
    notificationInAppList.clear()
  }

  fun removeStickyMessage(position: Int) {
    inAppSticky.value?.let {
      it.removeAt(position)
      inAppSticky.value = it
    }
  }

  fun checkIfItsMainView(destination: Int) {
    when (destination) {
      R.id.home -> {
        isMainView.value = true
      }
      R.id.categoriesFragment -> {
        isMainView.value = true
      }
      R.id.cartFragment -> {
        isMainView.value = true
      }
      R.id.choosePrescriptionFragment -> {
        isMainView.value = true
      }
      R.id.profileFragment -> {
        isMainView.value = true
      }
      else -> {
        isMainView.value = false
      }
    }
  }

  fun messageReceived() {
    var tem = messageCount.value ?: 0
    tem += 1
    messageCount.value = tem
    isThereSupportMessage.value = true

  }

  fun messageOpened() {
    val tem = 0
    messageCount.value = tem
    isThereSupportMessage.value = false
  }
}