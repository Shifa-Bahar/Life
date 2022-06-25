package com.lifepharmacy.application.ui.notifications.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.ProfileRepository

/**
 * Created by Zahid Ali
 */
class NotificationsViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: ProfileRepository,
  application: Application
) : BaseViewModel(application) {
  var skip = 0
  var take = 100
  fun getNotifications() =
    performNwOperation { repository.getNotifications(skip.toString(), take.toString()) }
}