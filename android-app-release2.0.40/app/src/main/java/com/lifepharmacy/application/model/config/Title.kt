package com.lifepharmacy.application.model.config


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Title(
    @SerializedName("text")
    var text: String? = ""
) : Parcelable