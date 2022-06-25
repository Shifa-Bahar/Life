package com.lifepharmacy.application.ui.wallet.viewmodels

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.WalletRepository
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog.Companion.TAG
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import com.lifepharmacy.application.utils.universal.InputEditTextValidator
import java.util.*

/**
 * Created by Zahid Ali
 */
class TopViewModel
@ViewModelInject
constructor(
  private val appManager: AppManager,
  private val repository: WalletRepository,
  application: Application
) : BaseViewModel(application) {


  lateinit var amount: InputEditTextValidator
  var amountMut = MutableLiveData<String>()
  var cardMainModel = CardMainModel()
  var viaNewCard = false
  var card_pg: Int? = 1
  var isSavecard: Boolean = true
  var selectedPaymentType = MutableLiveData<PaymentType>()
  var token: String = ""

  init {
    initializeValidations()
  }
  //need to test and uncomment
//  fun topUp(body: TransactionModel) =
//    if (appManager.persistenceManager.defaultpg == 2) {
//      performNwOperation { repository.createTransactionNew(body) }
//    } else {
//      performNwOperation { repository.createTransaction(body) }
//    }

  fun topUp(body: TransactionModel) =
    if (selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "new_for_wallet" && appManager.persistenceManager.defaultpg == 2) {
      Log.e(TAG, "topUp: 1 W+NP ${selectedPaymentType.value}")
      performNwOperation { repository.createTransactionNew(body) }
    } else if ((selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "new_for_wallet") && appManager.persistenceManager.defaultpg == 1) {
      Log.e(TAG, "topUp: 2 W+NP${selectedPaymentType.value}")
      performNwOperation { repository.createTransaction(body) }
    } else if ((selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "wallet_with_less") && card_pg == 1) {
      Log.e(TAG, "topUp: 3 W+C1 ${selectedPaymentType.value}")
      performNwOperation { repository.createTransaction(body) }
    } else if ((selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "wallet_with_less") && card_pg == 2) {
      Log.e(TAG, "topUp: 4 W+C2 ${selectedPaymentType.value}")
      performNwOperation { repository.createTransactionNew(body) }
    } else if ((appManager.persistenceManager.defaultpg == 1) && (selectedPaymentType.value?.name?.toLowerCase(
        Locale.ROOT
      ) == "new")
    ) {
      Log.e(TAG, "topUp: 5 ${selectedPaymentType.value}")
      performNwOperation { repository.createTransaction(body) }
    } else if ((appManager.persistenceManager.defaultpg == 2) && (selectedPaymentType.value?.name?.toLowerCase(
        Locale.ROOT
      ) == "new")
    ) {
      Log.e(TAG, "topUp: 6 ${selectedPaymentType.value}")
      performNwOperation { repository.createTransactionNew(body) }
    } else if ((selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "card") && card_pg == 1) {
      Log.e(TAG, "topUp: 7 T+C1 ${selectedPaymentType.value}")
      performNwOperation { repository.createTransaction(body) }
    } else if ((selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "card") && card_pg == 2) {
      Log.e(TAG, "topUp: 8 T+C2 ${selectedPaymentType.value}")
      performNwOperation { repository.createTransactionNew(body) }
    } else {
      Log.e(TAG, "topUp: 9 ${selectedPaymentType.value}")
      performNwOperation { repository.createTransaction(body) }
    }
//  selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "wallet_with_less" &&

  fun getTransactionModer(): TransactionModel {
    val transactionModel =
      TransactionModel()
    transactionModel.type = "charge"
    transactionModel.purpose = "wallet_recharge"
    if (viaNewCard) {
      transactionModel.CardType = "new"
      if (appManager.persistenceManager.defaultpg == 2 && token != ""
      ) {
        transactionModel.token = token
        transactionModel.save_card = isSavecard
      }
    } else {
      transactionModel.CardType = "saved"
      transactionModel.card_id = cardMainModel.id

    }
    transactionModel.method = "card"
    transactionModel.amount = amount.getValue().stringToNullSafeDouble()
    return transactionModel
  }

  private fun initializeValidations() {
    amount = InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.AMOUNT,
      true,
      object : InputEditTextValidator.InputEditTextValidatorCallBack {
        override fun onValueChange(validator: InputEditTextValidator?) {
          if (validator != null) {
            amountMut.value = validator.getValue()
          }
        }
      },
      viewModelContext.getString(R.string.amount_error)
    )
  }
}