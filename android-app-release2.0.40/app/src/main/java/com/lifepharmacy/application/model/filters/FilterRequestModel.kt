package com.lifepharmacy.application.model.filters


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class FilterRequestModel(
    var condition: String? = "",
    var key: String? = "",
    var value: String? = "",
) : Parcelable