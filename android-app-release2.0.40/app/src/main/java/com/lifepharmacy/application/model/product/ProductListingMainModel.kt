package com.lifepharmacy.application.model.product


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.category.Section
import com.lifepharmacy.application.model.category.SelectedCategory
import com.lifepharmacy.application.model.filters.FilterTypeModel

@SuppressLint("ParcelCreator")
@Parcelize
data class ProductListingMainModel(
  @SerializedName("products")
  var products: ArrayList<ProductDetails>? = ArrayList(),
  @SerializedName("siblings_categories")
  var siblingsCategories: ArrayList<Section>? = ArrayList(),
  @SerializedName("child_categories")
  var childCategories: ArrayList<Section>? = ArrayList(),
  @SerializedName("parent_categories")
  var parentCategories: ArrayList<Section>? = ArrayList(),
  @SerializedName("selected_category")
  var selectedCategory: SelectedCategory? = SelectedCategory(),
  @SerializedName("selected_parent_category")
  var selectedParentCategory: SelectedCategory? = SelectedCategory(),
  @SerializedName("search")
  var search: String? = "",
  @SerializedName("available_filters")
  var filters: ArrayList<FilterTypeModel>? = ArrayList()
) : Parcelable