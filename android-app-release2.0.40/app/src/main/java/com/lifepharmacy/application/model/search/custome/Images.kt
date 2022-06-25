package com.lifepharmacy.application.model.search.custome


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
data class Images(
  @SerializedName("raw")
  var raw: String? = ""
) : Parcelable {
  fun getJsonToImageUrl(): String? {
    return try {
      val json = JSONObject(raw)
      val url = json.getString("featured_image")
      url
    } catch (e: Exception) {
      ""
    }

  }
}