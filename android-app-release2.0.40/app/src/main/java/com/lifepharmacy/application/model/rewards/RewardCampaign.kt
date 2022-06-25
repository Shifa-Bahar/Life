package com.lifepharmacy.application.model.rewards

//data class RewardCampaign(
//  val amount: String,
//  val banner: String,
//  val compensated_by: String,
//  val created_at: String,
//  val created_by: Int,
//  val description: String,
//  val end_at: String,
//  val icon: String,
//  val id: Int,
//  val start_at: String,
//  val status: Int,
//  val terms: String,
//  val title: String,
//  val type: Int,
//  val updated_at: String
//)

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class RewardCampaign(
  @SerializedName("id")
  val id: Int,
  @SerializedName("title")
  val title: String,
  @SerializedName("description")
  val description: String,
  @SerializedName("banner")
  val banner: String,
  @SerializedName("icon")
  val icon: String,
  @SerializedName("status")
  val status: Int,
  @SerializedName("start_at")
  val start_at: String,
  @SerializedName("end_at")
  val end_at: String,
  @SerializedName("created_by")
  val created_by: Int,
  @SerializedName("terms")
  val terms: String,
  @SerializedName("compensated_by")
  val compensated_by: String,
  @SerializedName("amount")
  val amount: Double,
  @SerializedName("type")
  val type: Int,
  @SerializedName("created_at")
  val created_at: String,
  @SerializedName("updated_at")
  val updated_at: String,
  @SerializedName("type_label")
  val type_label: String
)