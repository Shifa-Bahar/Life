package com.lifepharmacy.application.model


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class NotifyRequestModel(
    @SerializedName("product_id")
    var productId: String? = "",
    @SerializedName("user_id")
    var userId: String? = ""
) : Parcelable