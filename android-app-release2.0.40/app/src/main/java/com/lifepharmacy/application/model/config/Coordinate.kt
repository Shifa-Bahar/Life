package com.lifepharmacy.application.model.config


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Coordinate(
    @SerializedName("lat")
    var lat: Double? = 0.0,
    @SerializedName("long")
    var long: Double? = 0.0
) : Parcelable