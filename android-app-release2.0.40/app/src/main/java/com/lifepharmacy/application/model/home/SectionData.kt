package com.lifepharmacy.application.model.home


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.product.ProductDetails

@SuppressLint("ParcelCreator")
@Parcelize
data class SectionData(
  var date: String? = "",
  @SerializedName("image_url")
  var imageUrl: String? = "",
  var title: String? = "",
  @SerializedName("parent")
  var parent: SectionData?,
  @SerializedName("children")
  var children: ArrayList<SectionData>? = ArrayList(),
  @SerializedName("feed_data")
  var feedData: ArrayList<ProductDetails>? = ArrayList(),
  var active: Boolean = false,
  @SerializedName("created_at")
  var createdAt: String = "",
  var description: String = "",
  @SerializedName("description_ar")
  var descriptionAr: String = "",
  @SerializedName("_id")
  var _id: String = "",
  var id: String = "",
  var images: Images = Images(),
  var name: String = "",
  @SerializedName("name_ar")
  var nameAr: String = "",
  @SerializedName("parent_id")
  var parentId: String = "",
  @SerializedName("updated_at")
  var updatedAt: String = "",
  var prices: ArrayList<Price> = ArrayList(),
  @SerializedName("short_description")
  var shortDescription: String? = "",
  var type: String = "",
  @SerializedName("view_all")
  var viewAll: SectionData?,
  @SerializedName("image_height")
  var imageHeight: String = "1",
  @SerializedName("image_width")
  var imageWidth: String = "1",
  @SerializedName("lowerPriceLimit")
  var lowerPriceLimit: String = "",
  @SerializedName("maxPriceLimit")
  var maxPriceLimit: String = "",
) : Parcelable