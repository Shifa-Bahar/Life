package com.lifepharmacy.application.ui.transactions.adapters

import com.lifepharmacy.application.model.payment.TransactionMainModel

/**
 * Created by Zahid Ali
 */
interface ClickTransaction {
    fun onClickTransaction(transactionModel: TransactionMainModel)
}