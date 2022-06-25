package com.lifepharmacy.application.model.product


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import android.annotation.SuppressLint
@SuppressLint("ParcelCreator")
@Parcelize
data class Slider(
    var id: String = "",
    @SerializedName("image_url")
    var imageUrl: String = ""
) : Parcelable