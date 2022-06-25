package com.lifepharmacy.application.model.notifications


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessagesMainModel(
  @SerializedName("in_app_messages")
  var inAppMessages: ArrayList<InAppNotificationMainModel>? = ArrayList(),
  @SerializedName("sticky_messages")
  var stickyMessages: ArrayList<StickyMessageModel>? = ArrayList()
) : Parcelable