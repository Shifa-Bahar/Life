package com.lifepharmacy.application.model.rewards

data class RewardModel(
  val `data`: List<Data>,
  val message: String,
  val success: Boolean
)