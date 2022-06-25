package com.lifepharmacy.application.model.notifications


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Button(
  @SerializedName("backgroundColor")
  var backgroundColor: String? = "",
  @SerializedName("id")
  var id: String? = "",
  @SerializedName("textColor")
  var textColor: String? = "",
  @SerializedName("title")
  var title: String? = "",
  @SerializedName("type")
  var type: String? = "",
  @SerializedName("heading")
  var heading: String? = ""
) : Parcelable