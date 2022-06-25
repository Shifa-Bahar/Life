package com.lifepharmacy.application.model.filters


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData

@SuppressLint("ParcelCreator")
@Parcelize
data class FilterTypeModel(
  var data: ArrayList<FilterModel>? = ArrayList(),
  @SerializedName("input_type")
  var inputType: String = "",
  var title: String = "",
  var type: String = "",
  var individualCount: Int = 0
) : Parcelable {

  fun setSelectedFilterCount() {
    var localCount = 0
    if (data != null && !data.isNullOrEmpty()) {
      for (item in data!!) {
        if (item.isChecked) {
          localCount++
        }
      }
    }
    individualCount = localCount
  }
}