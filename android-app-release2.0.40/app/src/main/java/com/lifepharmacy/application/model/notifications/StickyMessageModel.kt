package com.lifepharmacy.application.model.notifications


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import com.lifepharmacy.application.utils.DateTimeUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StickyMessageModel(
  @SerializedName("created_at")
  var createdAt: String? = "",
  @SerializedName("data")
  var data: StickyMessageData? = StickyMessageData(),
  @SerializedName("dismissible")
  var dismissible: Boolean? = false,
  @SerializedName("expire_at")
  var expireAt: String? = "",
  @SerializedName("_id")
  var mid: String? = "",
  @SerializedName("id")
  var id: String? = "",
  @SerializedName("is_dismissed")
  var isDismissed: Boolean? = false,
  @SerializedName("priority")
  var priority: Int? = 0,
  @SerializedName("type")
  var type: String? = "",
  @SerializedName("updated_at")
  var updatedAt: String? = "",
  @SerializedName("user_id")
  var userId: Int? = 0,
  @SerializedName("campaign_id")
  var conpaignId: String? = "",
) : Parcelable {
  fun isExpires(): Boolean {
    return DateTimeUtil.getGiveUTCTimeIsBeforeCurrent(expireAt)
  }
}