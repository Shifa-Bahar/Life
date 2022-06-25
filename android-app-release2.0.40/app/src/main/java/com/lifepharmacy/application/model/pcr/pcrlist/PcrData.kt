package com.lifepharmacy.application.model.pcr.pcrlist


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.lifepharmacy.application.model.filters.FilterTypeModel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@SuppressLint("ParcelCreator")
@Parcelize
data class PcrData(

  @SerializedName("_id")
  val _id: String,
  @SerializedName("id")
  val id: String,
  @SerializedName("name")
  val name: String,
  @SerializedName("slug")
  val slug: String,
  @SerializedName("name_ar")
  val name_ar: String,
  @SerializedName("active")
  val active: Boolean,
  @SerializedName("images")
  val images: @RawValue PcrImages,
  @SerializedName("seo")
  val seo: @RawValue Seo,
  @SerializedName("parent_id")
  val parent_id: String,
  @SerializedName("updated_at")
  val updated_at: String,
  @SerializedName("created_at")
  val created_at: String,
  @SerializedName("products")
  var products: @RawValue ArrayList<Products>? = ArrayList()
//  @SerializedName("products")
//  val products: List<Products>
) : Parcelable