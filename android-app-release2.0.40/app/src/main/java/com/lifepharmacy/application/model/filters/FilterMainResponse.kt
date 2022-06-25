package com.lifepharmacy.application.model.filters


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class FilterMainResponse(
    var filters: ArrayList<FilterTypeModel>? = ArrayList()
) : Parcelable