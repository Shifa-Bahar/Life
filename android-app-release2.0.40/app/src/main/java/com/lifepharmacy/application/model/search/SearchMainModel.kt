package com.lifepharmacy.application.model.search


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.product.ProductDetails

@SuppressLint("ParcelCreator")
@Parcelize
data class SearchMainModel(
  @SerializedName("query_id ")
  var queryId: String? = "",
  @SerializedName("search_term")
  var searchTerm: String? = "",
  @SerializedName("products")
  var products: ArrayList<ProductDetails>? = ArrayList(),
  @SerializedName("categories")
  var categories: ArrayList<SearchCategory>? = ArrayList(),
) : Parcelable