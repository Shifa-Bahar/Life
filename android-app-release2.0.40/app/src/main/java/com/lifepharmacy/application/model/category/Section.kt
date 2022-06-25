package com.lifepharmacy.application.model.category


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.home.SectionData
import com.lifepharmacy.application.model.product.BrandImages

@SuppressLint("ParcelCreator")
@Parcelize
data class Section(
  @SerializedName("is_enabled")
  var isEnabled: Boolean? = false,
  @SerializedName("order_id")
  var orderId: Int? = 0,
  @SerializedName("section_data_array")
  var sectionDataArray: ArrayList<SectionData>? = ArrayList(),
  @SerializedName("section_title")
  var sectionTitle: String? = "",
  @SerializedName("section_type")
  var sectionType: String? = "",
  @SerializedName("show_section_title")
  var showSectionTitle: Boolean? = false,
  @SerializedName("is_selected")
  var isSelected: Boolean? = false,
  @SerializedName("_id")
  var _id: String? = "",
  var id: String? = "",
  var name: String? = "",
  var images: BrandImages? = BrandImages(),
) : Parcelable