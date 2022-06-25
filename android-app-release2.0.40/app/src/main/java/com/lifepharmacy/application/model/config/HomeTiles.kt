package com.lifepharmacy.application.model.config


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeTiles(
    @SerializedName("background-color")
    var backgroundColor: String? = "#365FC9",
    @SerializedName("text-color")
    var textColor: String? = "#FFFFFF",
    @SerializedName("titles")
    var titles: ArrayList<Title>? = ArrayList()
) : Parcelable