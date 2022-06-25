package com.lifepharmacy.application.model.product


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import android.annotation.SuppressLint
import com.lifepharmacy.application.model.home.Images
import com.lifepharmacy.application.model.home.Price

@SuppressLint("ParcelCreator")
@Parcelize
data class FrequentlyBroughtTogether(
  var active: Boolean = false,
  var brand: Brand = Brand(),
  var category: Category = Category(),
  var collections: List<Collection> = listOf(),
  @SerializedName("created_at")
  var createdAt: String = "",
  var description: String = "",
  @SerializedName("description_ar")
  var descriptionAr: String = "",
  @SerializedName("_id")
  var _id: String = "",
  var id: String = "",
  var images: Images = Images(),
  var inventory: Inventory = Inventory(),
  var options: List<Option> = listOf(),
  var prices: List<Price> = listOf(),
  var seo: Seo? = Seo(),
  @SerializedName("short_description")
  var shortDescription: String = "",
  @SerializedName("short_description_ar")
  var shortDescriptionAr: String = "",
  var tags: List<Tag> = listOf(),
  var title: String = "",
  @SerializedName("title_ar")
  var titleAr: String = "",
  @SerializedName("updated_at")
  var updatedAt: String = ""
) : Parcelable