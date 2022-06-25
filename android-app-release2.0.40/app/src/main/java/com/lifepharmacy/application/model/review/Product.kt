package com.lifepharmacy.application.model.review


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
  @SerializedName("feedback")
  var feedback: String? = "",
  @SerializedName("product_id")
  var productId: String? = "",
  @SerializedName("rating")
  var rating: Float? = 0F,
  @SerializedName("is_anonymous")
  var isAnonymous: Boolean? = false,
) : Parcelable