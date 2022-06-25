package com.lifepharmacy.application.model.home


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeProductFeedRequest(
    @SerializedName("model_id")
    var modelId: String? = "",
    @SerializedName("model_type")
    var modelType: String? = "",
    @SerializedName("order_id")
    var orderId: Int? = 0
) : Parcelable