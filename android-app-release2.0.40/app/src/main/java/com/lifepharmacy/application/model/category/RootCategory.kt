package com.lifepharmacy.application.model.category


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class RootCategory(
  @SerializedName("_id")
  var _id: String? = "",
  @SerializedName("id")
  var id: String? = "",
  @SerializedName("name")
  var name: String? = "",
  @SerializedName("slug")
  var slug: String? = "",
  @SerializedName("imageUrl")
  var imageUrl: String? = ""
) : Parcelable