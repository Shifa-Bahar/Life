package com.lifepharmacy.application.model.wishlist


import com.google.gson.annotations.SerializedName

data class AddWishListRequestBody(
    @SerializedName("product_id")
    var productId: String? = ""
)