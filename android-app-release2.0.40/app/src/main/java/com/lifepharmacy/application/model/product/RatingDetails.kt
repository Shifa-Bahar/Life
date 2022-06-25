package com.lifepharmacy.application.model.product


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class RatingDetails(
    @SerializedName("1")
    var x1: Int = 0,
    @SerializedName("2")
    var x2: Int = 0,
    @SerializedName("3")
    var x3: Int = 0,
    @SerializedName("4")
    var x4: Int = 0,
    @SerializedName("5")
    var x5: Int = 0
) : Parcelable