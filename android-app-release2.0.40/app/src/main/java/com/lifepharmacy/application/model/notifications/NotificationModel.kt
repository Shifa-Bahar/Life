package com.lifepharmacy.application.model.notifications


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class NotificationModel(
  @SerializedName("content")
  var content: String? = "",
  @SerializedName("created_at")
  var createdAt: String? = "",
  @SerializedName("entity")
  var entity: String? = "",
  @SerializedName("entity_id")
  var entityId: Int? = 0,
  @SerializedName("_id")
  var _id: String? = "",
  @SerializedName("id")
  var id: String? = "",
  @SerializedName("is_read")
  var isRead: Boolean? = false,
  @SerializedName("title")
  var title: String? = "",
  @SerializedName("type")
  var type: String? = "",
  @SerializedName("updated_at")
  var updatedAt: String? = "",
  @SerializedName("user_id")
  var userId: Int? = 0
) : Parcelable