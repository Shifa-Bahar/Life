package com.lifepharmacy.application.ui.splash.viewmodel

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.managers.FiltersManager
import com.lifepharmacy.application.managers.StorageManagers
import com.lifepharmacy.application.model.filters.FilterMainResponse
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.SplashRepository
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.utils.universal.GpsStatusListener
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import retrofit2.Response

/**
 * Created by Zahid Ali
 */
class SplashViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  application: Application,
  private val repository: SplashRepository,
  val filtersManager: FiltersManager,
  public val gpsStatusListener: GpsStatusListener,
  val storageManagers: StorageManagers
) : BaseViewModel(application) {

//  fun getFilters() =
//    performNwOperation { repository.getFilters() }

  fun getFilters() {
    repository.getFilters(object :
      HandleNetworkCallBack<GeneralResponseModel<FilterMainResponse>> {
      override fun handleWebserviceCallBackSuccess(response: Response<GeneralResponseModel<FilterMainResponse>>) {
        response.body()?.data?.filters?.let { it1 ->
          filtersManager.updateAllFilters(
            it1
          )
        }
      }

      override fun handleWebserviceCallBackFailure(error: String?) {

      }

    })
  }

  fun checkToken() =
    performNwOperation { repository.checkToken() }

}
