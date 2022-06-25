package com.lifepharmacy.application.model.search.custome


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomSuggestions(
  @SerializedName("meta")
  var meta: Meta? = Meta(),
  @SerializedName("results")
  var results: ArrayList<SuggestionQueryRaw>? = ArrayList()
) : Parcelable