package com.lifepharmacy.application.model.rewards

import Reward_campaign
import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@SuppressLint("ParcelCreator")
@Parcelize
data class RewardItem(
  @SerializedName("amount")
  val amount: String,
  @SerializedName("created_at")
  val created_at: String,
  @SerializedName("id")
  var id: Int,
  @SerializedName("issued_at_store")
  val issued_at_store: String,
  @SerializedName("phone")
  val phone: String,
  @SerializedName("redeemed_at")
  val redeemed_at: String,
  @SerializedName("redeemed_at_store")
  val redeemed_at_store: String,
  @SerializedName("redeemed_staff")
  val redeemed_staff: String,
  @SerializedName("reward_campaign")
  val reward_campaign: @RawValue RewardCampaign,
  @SerializedName("reward_campaign_id")
  val reward_campaign_id: String,
  @SerializedName("reward_code")
  val reward_code: String,
  @SerializedName("type")
  val type: String,
  @SerializedName("updated_at")
  val updated_at: String,
  @SerializedName("user_id")
  val user_id: String
) : Parcelable