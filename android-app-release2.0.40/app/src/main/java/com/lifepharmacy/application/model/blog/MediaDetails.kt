package com.lifepharmacy.application.model.blog


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class MediaDetails(
    @SerializedName("sizes")
    var sizes: Sizes? = Sizes()
) : Parcelable