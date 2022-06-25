package com.lifepharmacy.application.ui.wallet.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.ScrollingState
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.payment.CardDeleteBody
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.model.payment.TransactionMainModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.WalletRepository
import com.lifepharmacy.application.ui.utils.AppScrollState
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import retrofit2.Response
import kotlin.collections.ArrayList

/**
 * Created by Zahid Ali
 */
class WalletViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: WalletRepository,
  private val scrollState: AppScrollState,
  application: Application
) : BaseViewModel(application) {

  var cardsDataMut = MutableLiveData<Result<ArrayList<CardMainModel>>>()
  var isThereAnyCard = MutableLiveData<Boolean>()
  var isThereAnyTransactions = MutableLiveData<Boolean>()
  var selectedCard: CardMainModel? = null
  var card_pg: Int? = 1
  var selectedTransaction: TransactionMainModel? = null
  var skip = 0
  var take = 10
  fun getScrollStateRepo(): AppScrollState {
    return scrollState
  }

  init {
    requestCards()
  }

  fun getScrollStateMut(): MutableLiveData<ScrollingState> {
    return scrollState.getScrollingState()
  }

  fun requestCards() {
    if (appManager.persistenceManager.isLoggedIn()) {
      repository.getCards(object :
        HandleNetworkCallBack<GeneralResponseModel<ArrayList<CardMainModel>>> {
        override fun handleWebserviceCallBackSuccess(response: Response<GeneralResponseModel<ArrayList<CardMainModel>>>) {
          cardsDataMut.value =
            Result(Result.Status.SUCCESS, response.body()?.data, response.body()?.message)
        }

        override fun handleWebserviceCallBackFailure(error: String?) {
          cardsDataMut.value = Result(Result.Status.ERROR, null, error)
        }

      })
    }

  }


  fun deleteCard(body: CardDeleteBody) =
    performNwOperation { repository.deleteCard(body) }

  fun getTransactions() =
    performNwOperation { repository.getTransactions(skip.toString(), take.toString()) }

  fun makeCardDeleteBody(id: Int): CardDeleteBody {
    return CardDeleteBody(id)
  }
}