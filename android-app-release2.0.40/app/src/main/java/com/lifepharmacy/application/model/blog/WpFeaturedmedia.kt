package com.lifepharmacy.application.model.blog


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class WpFeaturedmedia(
    @SerializedName("media_details")
    var mediaDetails: MediaDetails? = MediaDetails(),
    @SerializedName("title")
    var title: Title? = Title()
) : Parcelable