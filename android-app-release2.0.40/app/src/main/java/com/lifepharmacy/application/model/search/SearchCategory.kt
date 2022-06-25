package com.lifepharmacy.application.model.search


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class SearchCategory(
    @SerializedName("id")
    var categoryId: String? = "",
    @SerializedName("category_name")
    var categoryName: String? = "",
    @SerializedName("_id")
    var id: Id? = Id()
) : Parcelable