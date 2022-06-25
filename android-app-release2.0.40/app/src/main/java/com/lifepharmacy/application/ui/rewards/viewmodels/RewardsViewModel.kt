package com.lifepharmacy.application.ui.rewards.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.rewards.RewardCampaign
import com.lifepharmacy.application.model.rewards.RewardItem
import com.lifepharmacy.application.model.rewards.ValidateRewardsRequestBody
import com.lifepharmacy.application.model.vouchers.VoucherMainResponse
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.RewardsRepository
import com.lifepharmacy.application.utils.universal.InputEditTextValidator

class RewardsViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: RewardsRepository,
  application: Application
) : BaseViewModel(application) {

  var id: Int? = null

  var rewardText: InputEditTextValidator =
    InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      null
    )
  var selectedVoucher = MutableLiveData<RewardItem>()
  var termsAndConditions = MutableLiveData<String>()
  var reward_code = MutableLiveData<String>()
  var imageBanner = MutableLiveData<RewardCampaign>()
//  var imageBanner = MutableLiveData<String>()
  var endDate = MutableLiveData<String>()
  var voucherMainResponseMut = MutableLiveData<VoucherMainResponse>()
  private fun makeCouponValidateBody(): ValidateRewardsRequestBody {
    return ValidateRewardsRequestBody(
      // cartId = appManager.persistenceManager.getCartID().stringToNullSafeInt(),
      rewardCode = rewardText.getValue(),
      //cartTotal = offerTotal.value ?: 0.0
    )
  }

  fun getRewards() =
    performNwOperation { repository.getRewards() }

  fun validateRewards() =
    performNwOperation { repository.validateRewards(rewardText.getValue()) }

  fun getRewardsDetails() =
    performNwOperation { repository.getRewardsDetails(id!!) }

//  fun getUserById() = performNwAndSaveOperation(
//  databaseQuery = { userDao.getUser() }, networkCall = {
//    userRepo.fetchUserDetail()
//  }, saveCallResult = { userDao.insertUser(it) })

  fun resetInput() {
    rewardText.setValue("")
  }
}