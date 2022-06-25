package com.lifepharmacy.application.model.product


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Collection(
    var id: String = "",
    var name: String = "",
    var slug: String = ""
) : Parcelable