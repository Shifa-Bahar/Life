package com.lifepharmacy.application.model.search.agolia


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.google.gson.Gson
import com.lifepharmacy.application.model.home.Images
import com.lifepharmacy.application.model.product.BrandImages
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import org.json.JSONObject

@Parcelize
data class Hits(
  @SerializedName("query")
  var query: String? = "",
  @SerializedName("id")
  var id: String? = "",
  @SerializedName("images")
  var images: Images? = Images(),
  @SerializedName("objectID")
  var objectID: String? = "",
  @SerializedName("title")
  var title: String? = "",
  @SerializedName("queryID")
  var queryID: String? = "",
  @SerializedName("categories.name")
  var category: ArrayList<String>? = ArrayList(),
  var singleCategory: String = "",
  var position: Int? = 0,
  var index: String? = "",
) : Parcelable {


  fun getString(): String {
    return if (query.isNullOrEmpty()) {
      title ?: ""
    } else {
      query ?: ""
    }
  }
}