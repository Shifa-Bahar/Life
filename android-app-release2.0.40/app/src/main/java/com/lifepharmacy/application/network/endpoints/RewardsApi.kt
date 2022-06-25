package com.lifepharmacy.application.network.endpoints

import ValidateReward
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.rewards.RewardItem
import com.lifepharmacy.application.model.rewards.ValidateRewardsRequestBody
import com.lifepharmacy.application.utils.URLs
import retrofit2.Response
import retrofit2.http.*

interface RewardsApi {

//  @GET(URLs.REWARD_LIST)
//  suspend fun getReswardsList(): Response<RewardModel>

//  @GET(URLs.REWARD_LIST)
//  fun getRewardsList(): Call<GeneralResponseModel<ArrayList<RewardModel>>>


  @GET(URLs.REWARD_LIST)
  suspend fun getRewardsList(): Response<GeneralResponseModel<ArrayList<RewardItem>>>

  @POST(URLs.REWARDS_VALIDATE)
  suspend fun validateReward(@Query("coupon_code") coupon_code: String): Response<GeneralResponseModel<RewardItem>>


  @GET(URLs.REWARD_LIST_DETAIL + "{id}")
  suspend fun getRewardsListDetail(@Path("id") id: Int): Response<GeneralResponseModel<RewardItem>>

//  @POST(URLs.REWARDS_VALIDATE)
//  suspend fun validateRewards(@Body body: ValidateRewardsRequestBody): Response<GeneralResponseModel<RewardItem>>
}
