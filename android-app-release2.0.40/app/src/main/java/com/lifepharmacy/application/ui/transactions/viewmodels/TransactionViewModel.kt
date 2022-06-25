package com.lifepharmacy.application.ui.transactions.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.payment.ReturnToCardRequest
import com.lifepharmacy.application.model.payment.TransactionMainModel
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.WalletRepository
import com.lifepharmacy.application.utils.universal.InputEditTextValidator

/**
 * Created by Zahid Ali
 */
class TransactionViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: WalletRepository,
  application: Application
) : BaseViewModel(application) {
  var selectedTransaction = MutableLiveData<TransactionMainModel>()

  lateinit var amount: InputEditTextValidator

  init {
    initializeValidations()
  }

  private fun initializeValidations() {
    amount =
      InputEditTextValidator(
        InputEditTextValidator.InputEditTextValidationsEnum.AMOUNT,
        true,
        null,
        "errorText"
      )
  }

  fun returnToCard() =
    performNwOperation { repository.returnToCard(makeReturnToCardModel()) }

  fun makeReturnToCardModel(): ReturnToCardRequest {
    return ReturnToCardRequest(selectedTransaction.value?.return_order_id?.toDoubleOrNull())
  }

}