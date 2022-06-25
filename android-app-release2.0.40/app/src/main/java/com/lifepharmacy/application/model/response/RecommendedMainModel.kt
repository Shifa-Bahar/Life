package com.lifepharmacy.application.model.response


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.product.ProductDetails

@SuppressLint("ParcelCreator")
@Parcelize
data class RecommendedMainModel(
  @SerializedName("recommended_for_you")
  var products: ArrayList<ProductDetails>? = ArrayList(),
  @SerializedName("trending")
  var trendings: ArrayList<String>? = ArrayList(),
) : Parcelable