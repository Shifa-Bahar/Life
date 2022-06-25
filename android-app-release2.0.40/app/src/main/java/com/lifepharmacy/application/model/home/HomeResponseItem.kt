package com.lifepharmacy.application.model.home


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class HomeResponseItem(
  @SerializedName("is_enabled")
  var isEnabled: Boolean? = false,
  @SerializedName("order_id")
  var orderId: Int? = 0,
  @SerializedName("section_data_array")
  var sectionDataList: ArrayList<SectionData>? = ArrayList(),
  @SerializedName("section_data_object")
  var sectionDataObject: SectionData?,
  @SerializedName("section_title")
  var sectionTitle: String? = "",
  @SerializedName("section_type")
  var sectionType: String? = "",
  @SerializedName("show_section_title")
  var showSectionTitle: Boolean? = false,
  @SerializedName("background_color")
  var backgroundColor: String? = "",
  @SerializedName("hasChanged")
  var hasChanged: Boolean? = false,
  @SerializedName("model_type")
  var modelType: String? = "",
  @SerializedName("model_id")
  var modelId: String? = "",
  @SerializedName("dynamic_grid_row")
  var numberOfRows: String? = "",
  @SerializedName("dynamic_grid_column")
  var numberOfColumns: String? = "",
  @SerializedName("item_height")
  var itemheight: String? = "",
  @SerializedName("item_width")
  var item_weight: String? = "",
  @SerializedName("is_vertical")
  var isVertical: Boolean? = false,
) : Parcelable