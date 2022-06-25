package com.lifepharmacy.application.model.home


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class FeedData(
    @SerializedName("_id")
    var _id: String = "",
    var id: String = "",
    var images: Images = Images(),
    var prices: ArrayList<Price>? = ArrayList(),
    @SerializedName("short_description")
    var shortDescription: String = "",
    var title: String = ""
) : Parcelable