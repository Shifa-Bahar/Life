package com.lifepharmacy.application.model.product

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class ProductRating(
    var rating: String = "",
    @SerializedName("rating_details")
    var ratingDetails: RatingDetails = RatingDetails(),
    @SerializedName("number_of_reviews")
    var numberOfReviews: Int = 0,
) : Parcelable