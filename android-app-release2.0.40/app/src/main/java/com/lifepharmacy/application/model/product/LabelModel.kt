package com.lifepharmacy.application.model.product


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class LabelModel(
    @SerializedName("color_code")
    var colorCode: String = "",
    @SerializedName("icon_type")
    var iconType: Int = 0,
    @SerializedName("label_text")
    var labelText: String = ""
) : Parcelable