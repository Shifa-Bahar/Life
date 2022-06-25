package com.lifepharmacy.application.model.docs


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class DeleteDocRequestBody(
    @SerializedName("file_id")
    var fileId: Int? = 0
) : Parcelable