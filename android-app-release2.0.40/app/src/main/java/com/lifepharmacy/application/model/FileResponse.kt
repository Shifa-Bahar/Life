package com.lifepharmacy.application.model


import com.google.gson.annotations.SerializedName

data class FileResponse(
    @SerializedName("file")
    var file: String? = ""
)