package com.lifepharmacy.application.model.home


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.product.GalleryImages

@SuppressLint("ParcelCreator")
@Parcelize
data class Images(
  @SerializedName("featured_image")
  var featuredImage: String = "",
  @SerializedName("other_images")
  var otherImages: ArrayList<String> = ArrayList(),
  @SerializedName("gallery_images")
  var galleryImages: ArrayList<GalleryImages> = ArrayList()
) : Parcelable