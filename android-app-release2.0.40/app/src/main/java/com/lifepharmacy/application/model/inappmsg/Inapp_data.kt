package com.lifepharmacy.application.model.inappmsg

import com.google.gson.annotations.SerializedName
import com.lifepharmacy.application.model.home.Price

data class Inapp_data(

  @SerializedName("image_url") val imageUrl: String,
  @SerializedName("action_button_label") val action_button_label: String,
  @SerializedName("action_button_color") val action_button_color: String,
  @SerializedName("action_button_bg_color") val action_button_bg_color: String,
  @SerializedName("action_key") val action_key: String,
  @SerializedName("action_title") val action_title: String,
  @SerializedName("action_value") val action_value: ArrayList<Action_value>? = ArrayList(),
  @SerializedName("campaign_id") val campaign_id: Int,
  @SerializedName("type") val type: String
)