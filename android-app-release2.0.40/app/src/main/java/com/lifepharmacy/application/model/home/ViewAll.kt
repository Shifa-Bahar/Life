package com.lifepharmacy.application.model.home


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ViewAll(
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("type")
    var type: String? = ""
) : Parcelable