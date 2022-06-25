package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.utils.NetworkUtils
import javax.inject.Inject

class PcrRepository
@Inject constructor(
  private val networkUtils: NetworkUtils,
//  private val pcrApi: PcrApi
) :
  BaseRepository() {
//  suspend fun getPcrList() =
//    getResult({ pcrApi.getPcrList() }, networkUtils)

//  suspend fun validateRewards(body: ValidateRewardsRequestBody) =
//    getResult({ rewardsApi.validateRewards(body) }, networkUtils)
//
//  suspend fun validateRewards(coupon_code: String) =
//    getResult({ rewardsApi.validateReward(coupon_code) }, networkUtils)
//
//
//  suspend fun getRewardsDetails(id: Int) =
//    getResult({ rewardsApi.getRewardsListDetail(id) }, networkUtils)

//  suspend fun getRewardsValidation(coupon_code: Int) =
//    getResult({ rewardsApi.validateReward(coupon_code) }, networkUtils)

}
