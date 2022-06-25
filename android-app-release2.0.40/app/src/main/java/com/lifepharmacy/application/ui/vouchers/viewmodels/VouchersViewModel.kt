package com.lifepharmacy.application.ui.vouchers.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.vouchers.VoucherMainResponse
import com.lifepharmacy.application.model.vouchers.VoucherModel
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.VouchersRepository

/**
 * Created by Zahid Ali
 */
class VouchersViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: VouchersRepository,
  application: Application
) : BaseViewModel(application) {
  var skip = 0
  var take = 20

  var selectedVoucher = MutableLiveData<VoucherModel>()
  var termsAndConditions = MutableLiveData<String>()
  var voucherMainResponseMut = MutableLiveData<VoucherMainResponse>()


  fun getVouchers() =
    performNwOperation { repository.getVouchers(skip.toString(), take.toString()) }

//  fun getUserById() = performNwAndSaveOperation(
//  databaseQuery = { userDao.getUser() }, networkCall = {
//    userRepo.fetchUserDetail()
//  }, saveCallResult = { userDao.insertUser(it) })
}