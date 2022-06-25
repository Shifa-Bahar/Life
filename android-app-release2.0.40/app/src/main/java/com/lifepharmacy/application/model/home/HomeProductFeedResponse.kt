package com.lifepharmacy.application.model.home


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import com.lifepharmacy.application.model.product.ProductDetails
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeProductFeedResponse(
    @SerializedName("feed_data")
    var feedData: ArrayList<ProductDetails>? = ArrayList(),
    @SerializedName("order_id")
    var orderId: Int? = 0,
    @SerializedName("view_all")
    var viewAll: ViewAll? = ViewAll()
) : Parcelable