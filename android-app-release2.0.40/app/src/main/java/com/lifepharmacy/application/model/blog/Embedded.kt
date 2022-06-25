package com.lifepharmacy.application.model.blog


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Embedded(
    @SerializedName("wp:featuredmedia")
    var wpFeaturedmedia: ArrayList<WpFeaturedmedia>? = ArrayList()
) : Parcelable