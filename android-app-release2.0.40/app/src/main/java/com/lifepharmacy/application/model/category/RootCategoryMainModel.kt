package com.lifepharmacy.application.model.category


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class RootCategoryMainModel(
    @SerializedName("rootCategory")
    var rootCategory: ArrayList<RootCategory>? = ArrayList()
) : Parcelable