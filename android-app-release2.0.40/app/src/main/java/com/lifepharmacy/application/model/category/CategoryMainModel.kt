package com.lifepharmacy.application.model.category


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.product.BrandImages
import com.lifepharmacy.application.model.product.Seo

@SuppressLint("ParcelCreator")
@Parcelize
data class CategoryMainModel(
  var active: Boolean? = false,
  var children: ArrayList<Children>? = ArrayList(),
  @SerializedName("created_at")
  var createdAt: String? = "",
  var description: String? = "",
  @SerializedName("description_ar")
  var descriptionAr: String? = "",
  var id: String? = "",
  var images: BrandImages? = BrandImages(),
  var name: String? = "",
  @SerializedName("name_ar")
  var nameAr: String? = "",
  @SerializedName("parent_id")
  var parentId: String? = "",
  var sections: ArrayList<Section>? = ArrayList(),
  var seo: Seo? = Seo(),
  @SerializedName("updated_at")
  var updatedAt: String? = ""
) : Parcelable