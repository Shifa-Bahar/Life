package com.lifepharmacy.application.model.product


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Seo(
  @SerializedName("seo_description")
  var seoDescription: String? = "",
  @SerializedName("seo_description_ar")
  var seoDescriptionAr: String? = "",
  @SerializedName("seo_title")
  var seoTitle: String? = "",
  @SerializedName("seo_title_ar")
  var seoTitleAr: String? = "",
) : Parcelable