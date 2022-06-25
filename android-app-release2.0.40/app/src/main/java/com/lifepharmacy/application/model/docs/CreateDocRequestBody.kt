package com.lifepharmacy.application.model.docs


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class CreateDocRequestBody(
    @SerializedName("file_name")
    var fileName: String? = "",
    @SerializedName("file_type")
    var fileType: String? = "",
    @SerializedName("file_url")
    var fileUrl: String? = ""
) : Parcelable