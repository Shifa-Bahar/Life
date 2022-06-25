package com.lifepharmacy.application.model.search.agolia


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import com.google.gson.Gson
import com.lifepharmacy.application.model.product.ProductDetails
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import org.json.JSONArray


@Parcelize
data class AlgoliaSearchResult(
  @SerializedName("results")
  var results: ArrayList<SuggestionMainObject>? = ArrayList(),
) : Parcelable {
  fun getAllHits(): ArrayList<Hits> {
    val addHitsAll = ArrayList<Hits>()
    if (!results.isNullOrEmpty()) {
      for (i in 0 until results!!.size) {
        if (!results!![i].hits.isNullOrEmpty()) {
          var hitsAll = results!![i].hits?.mapIndexed { index, hits ->
            Hits(
              query = hits.query,
              queryID = results!![i].queryID,
              objectID = hits.objectID,
              title = hits.title,
              category = hits.category,
              singleCategory = hits.singleCategory,
              position = index,
              index = results!![i].index,
              images = hits.images,
              id = hits.id
            )
          }
          if (hitsAll != null) {
            addHitsAll.addAll(hitsAll)
          }
        }
      }
    }
    return addHitsAll
  }
}