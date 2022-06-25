package com.lifepharmacy.application.model.category


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.product.BrandImages

@SuppressLint("ParcelCreator")
@Parcelize
data class SelectedCategory(
  @SerializedName("id")
    var id: String? = "",
  @SerializedName("parent_id")
    var parentId: String? = "",
  @SerializedName("images")
    var images: BrandImages? = BrandImages(),
) : Parcelable