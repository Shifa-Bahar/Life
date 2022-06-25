package com.lifepharmacy.application.model.product


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Category(
  var active: Boolean = false,
  @SerializedName("created_at")
  var createdAt: String = "",
  var description: String = "",
  @SerializedName("description_ar")
  var descriptionAr: String = "",
  @SerializedName("_id")
  var _id: String = "",
  var id: String = "",
  var images: BrandImages = BrandImages(),
  var name: String = "",
  @SerializedName("name_ar")
  var nameAr: String = "",
  var seo: Seo? = Seo(),
  @SerializedName("updated_at")
  var updatedAt: String = ""
) : Parcelable