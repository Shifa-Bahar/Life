package com.lifepharmacy.application.model.wishlist


import com.google.gson.annotations.SerializedName

data class DeleteWishListBody(
    @SerializedName("product_id")
    var id: String? = ""
)