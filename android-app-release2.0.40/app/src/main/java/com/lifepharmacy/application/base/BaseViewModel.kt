package com.lifepharmacy.application.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.ui.utils.LoadingState

abstract class BaseViewModel(application: Application) :
  AndroidViewModel(application) {

  var viewModelContext: Application = application
  fun logOut() {
  }
}
