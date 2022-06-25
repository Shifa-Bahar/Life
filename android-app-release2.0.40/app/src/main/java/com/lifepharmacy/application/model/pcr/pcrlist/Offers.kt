package com.lifepharmacy.application.model.pcr.pcrlist


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
class Offers(
  @SerializedName("end_at")
  var endAt: String? = "",
  @SerializedName("id")
  var id: String? = "",
  @SerializedName("start_at")
  var startAt: String? = "",
  @SerializedName("type")
  var type: String? = "",
  @SerializedName("value")
  var value: Double? = 0.0,
  @SerializedName("xValue")
  var xValue: Int? = 0,
  @SerializedName("yValue")
  var yValue: Int? = 0,
) : Parcelable {

  fun getTypeEnum():OffersType{
    return when (type) {
      "bxgy" -> {
        OffersType.BXGY
      }
      "flat_percentage_discount" -> {
        OffersType.FLAT
      }
      "free_gift" -> {
        OffersType.FREE_GIFT
      }
      else -> {
        OffersType.NON
      }
    }
  }
}
enum class OffersType{
  NON,
  BXGY,
  FLAT,
  FREE_GIFT

}