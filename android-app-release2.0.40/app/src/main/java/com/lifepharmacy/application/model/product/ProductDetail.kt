package com.lifepharmacy.application.model.product


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class ProductDetail(
  @SerializedName("delviery_details")
  var delvieryDetails: DelvieryDetails = DelvieryDetails(),
  @SerializedName("frequently_brought_together")
  var frequentlyBroughtTogether: ArrayList<ProductDetails> = ArrayList(),
  @SerializedName("product_details")
  var productDetails: ProductDetails = ProductDetails(),
  @SerializedName("product_rating")
  var productRating: ProductRating = ProductRating(),
  @SerializedName("product_reviews")
  var productReviews: ArrayList<ProductReview> = ArrayList(),
  @SerializedName("related_products")
  var relatedProducts: ArrayList<ProductDetails> = ArrayList(),
  var slider: ArrayList<Slider> = ArrayList()
) : Parcelable