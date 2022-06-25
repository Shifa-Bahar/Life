package com.lifepharmacy.application.model.search.custome

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Result(
  @SerializedName("id")
  var id: Id? = Id(),
  @SerializedName("images")
  var images: Images? = Images(),
  @SerializedName("title")
  var title: Title? = Title()
) : Parcelable