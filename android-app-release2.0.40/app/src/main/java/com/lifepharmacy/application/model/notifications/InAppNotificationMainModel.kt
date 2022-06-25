package com.lifepharmacy.application.model.notifications


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import com.lifepharmacy.application.utils.DateTimeUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InAppNotificationMainModel(
  @SerializedName("background_color")
  var backgroundColor: String? = "",
  @SerializedName("data")
  var `data`: ArrayList<InAppNotificationDataModel>? = ArrayList(),
  @SerializedName("dismissible")
  var dismissible: Boolean? = false,
  @SerializedName("message_id")
  var messageId: String? = "",
  @SerializedName("is_dismissed")
  var isDismissed: Boolean? = false,
  @SerializedName("type")
  var mode: String? = "",
  @SerializedName("expire_at")
  var expiredAt: String? = "",
  @SerializedName("_id")
  var mid: String? = "",
  @SerializedName("id")
  var id: String? = "",
  @SerializedName("user_id")
  var userId: String? = "",
  @SerializedName("campaign_id")
  var conpaignId: String? = "",
) : Parcelable {
  fun isExpires(): Boolean {
    return DateTimeUtil.getGiveUTCTimeIsBeforeCurrent(expiredAt)
  }
}