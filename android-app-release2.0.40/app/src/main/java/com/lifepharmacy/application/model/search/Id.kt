package com.lifepharmacy.application.model.search


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Id(
  @SerializedName("category.name")
  var categoryName: String? = ""
) : Parcelable