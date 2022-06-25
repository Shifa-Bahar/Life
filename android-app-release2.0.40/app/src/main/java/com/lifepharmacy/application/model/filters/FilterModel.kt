package com.lifepharmacy.application.model.filters


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.product.BrandImages

@SuppressLint("ParcelCreator")
@Parcelize
data class FilterModel(
  var condition: String? = "",
  @SerializedName("_id")
  var _id: String? = "",
  var id: String? = "",
  var images: BrandImages? = BrandImages(),
  var key: String? = "",
  var name: String? = "",
  var title: String? = "",
  var value: String? = "",
  var type: String? = "",
  var inputType: String? = "",
  var isChecked: Boolean = false
) : Parcelable