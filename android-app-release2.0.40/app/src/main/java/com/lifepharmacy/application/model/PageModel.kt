package com.lifepharmacy.application.model


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class PageModel(
  @SerializedName("content")
  var content: String? = "",
  @SerializedName("created_at")
  var createdAt: String? = "",
  @SerializedName("slug")
  var slug: String? = "",
  @SerializedName("title")
  var title: String? = "",
  @SerializedName("type")
  var type: String? = "",
  @SerializedName("updated_at")
  var updatedAt: String? = ""
) : Parcelable