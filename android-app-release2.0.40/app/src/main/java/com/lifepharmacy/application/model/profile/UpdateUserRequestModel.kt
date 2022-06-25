package com.lifepharmacy.application.model.profile


import com.google.gson.annotations.SerializedName

data class UpdateUserRequestModel(
    @SerializedName("email")
    var email: String? ="",
    @SerializedName("name")
    var name: String? ="",
    @SerializedName("dob")
    var dob: String? ="",
    @SerializedName("gender")
    var gender: String? ="",
    @SerializedName("photo")
    var photo: String? =""
)