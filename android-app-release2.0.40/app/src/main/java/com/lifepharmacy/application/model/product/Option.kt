package com.lifepharmacy.application.model.product


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Option(
    var key: String = "",
    var order: Int = 0,
    var value: String = ""
) : Parcelable