package com.lifepharmacy.application.model.product


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class ProductReview(
  @SerializedName("created_at")
  var createdAt: String = "",
  @SerializedName("review")
  var description: String = "",
  @SerializedName("value")
  var rating: Float = 0.0F,
  var title: String = "",
  @SerializedName("user_details")
  var userDetails: ReviewUserDetails?,
  @SerializedName("is_anonymous")
  var isAnonymouns: Boolean = false,

) : Parcelable
