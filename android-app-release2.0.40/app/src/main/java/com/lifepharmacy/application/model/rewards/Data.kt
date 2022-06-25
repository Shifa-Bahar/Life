package com.lifepharmacy.application.model.rewards

data class Data(
  val amount: String,
  val created_at: String,
  val id: Int,
  val issued_at_store: Any,
  val phone: String,
  val redeemed_at: Any,
  val redeemed_at_store: Any,
  val redeemed_staff: Any,
  val reward_campaign: RewardCampaign,
  val reward_campaign_id: String,
  val reward_code: String,
  val type: String,
  val updated_at: String,
  val user_id: String
)