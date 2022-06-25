package com.lifepharmacy.application.ui.home.viewModel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AddressManager
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.managers.FiltersManager
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.HomeRepository
import com.lifepharmacy.application.managers.OffersManagers
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.home.HomeProductFeedRequest
import com.lifepharmacy.application.model.home.HomeResponseItem
import com.lifepharmacy.application.model.response.RecommendedMainModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.utils.universal.GoogleUtils
import com.lifepharmacy.application.utils.universal.GpsStatusListener
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Created by Zahid Ali
 */
class HomeViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val homeRepository: HomeRepository,
  private val googleUtils: GoogleUtils,
  val addressManager: AddressManager,
  public val filtersManager: FiltersManager,
//     val cartUtil: CartManager,
  val offersManagers: OffersManagers,
  private val gpsStatusListener: GpsStatusListener,
  application: Application
) : BaseViewModel(application) {
  var recommendedMut = MutableLiveData<Result<RecommendedMainModel>>()
  var title = MutableLiveData<String>()
  var id = MutableLiveData<String>()
  fun getGpsListener(): GpsStatusListener {
    return gpsStatusListener
  }


  fun getHomeData() =
    performNwOperation { homeRepository.getHomeData() }

  fun getLandingPageData() =
    performNwOperation { homeRepository.getLandingPage(id.value ?: "") }

  fun getInapmsgRating() =
    performNwOperation { homeRepository.getInapmsgRating() }

  fun getProductDetails(productId: String) =
    performNwOperation { homeRepository.getProductDetails(productId) }

  fun getBlog() =
    performNwOperation { homeRepository.getBlog() }

  fun getHomeFeedProductData(body: HomeProductFeedRequest) =
    performNwOperation { homeRepository.getHomeFeed(body) }




  fun makeHomeProductFeedRequestModel(homeResponseItem: HomeResponseItem): HomeProductFeedRequest {
    return HomeProductFeedRequest(
      orderId = homeResponseItem.orderId,
      modelId = homeResponseItem.modelId,
      modelType = homeResponseItem.modelType
    )
  }

  fun getRecommended() {
//    if (appManager.persistenceManager.isLoggedIn()) {
    homeRepository.getRecommended(object :
      HandleNetworkCallBack<GeneralResponseModel<RecommendedMainModel>> {
      override fun handleWebserviceCallBackSuccess(response: Response<GeneralResponseModel<RecommendedMainModel>>) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
          recommendedMut.value =
            Result(Result.Status.SUCCESS, response.body()?.data, response.body()?.message)
        }

      }

      override fun handleWebserviceCallBackFailure(error: String?) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
          recommendedMut.value = Result(Result.Status.ERROR, null, error)
        }

      }

    })
//    }

  }

  fun getProducts() =
    performNwOperation { homeRepository.getProducts() }


}