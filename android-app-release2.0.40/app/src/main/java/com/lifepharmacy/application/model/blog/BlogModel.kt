package com.lifepharmacy.application.model.blog


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class BlogModel(
    @SerializedName("_embedded")
    var embedded: Embedded? = Embedded(),
    @SerializedName("link")
    var link: String? = "",
    @SerializedName("date")
    var date: String? = "",
    @SerializedName("title")
    var title: Title? = Title()
) : Parcelable