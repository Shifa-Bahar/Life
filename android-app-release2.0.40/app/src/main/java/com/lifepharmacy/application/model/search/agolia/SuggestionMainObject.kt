package com.lifepharmacy.application.model.search.agolia


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class SuggestionMainObject(
  @SerializedName("hits")
  var hits: ArrayList<Hits>? = ArrayList(),
  @SerializedName("queryID")
  var queryID: String? = "",
  @SerializedName("index")
  var index: String? = "",
) : Parcelable {
  fun getAllHits(): ArrayList<Hits> {
    val list = ArrayList<Hits>()
    if (!hits.isNullOrEmpty()) {
      for ((query, category) in hits!!) {
        category?.map {
          Hits(query, singleCategory = it.toString())
        }?.let { list.addAll(it) }
      }
    }


    return list
  }
}