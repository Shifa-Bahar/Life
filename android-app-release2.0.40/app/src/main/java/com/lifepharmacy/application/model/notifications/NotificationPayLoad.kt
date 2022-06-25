package com.lifepharmacy.application.model.notifications


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class NotificationPayLoad(
  var title: String = "",
  var key: String = "",
  var value: String = "",
  var action: NotificationAction = NotificationAction.NON,
) : Parcelable {
  companion object {
    val stringProductListing = "category brand collection offer brands categories collections"
  }
}