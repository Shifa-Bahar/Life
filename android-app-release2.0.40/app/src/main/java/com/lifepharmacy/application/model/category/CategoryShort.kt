package com.lifepharmacy.application.model.category


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryShort(
  @SerializedName("id")
  var id: String? = "",
  @SerializedName("name")
  var name: String? = "",
  @SerializedName("slug")
  var slug: String? = ""
) : Parcelable