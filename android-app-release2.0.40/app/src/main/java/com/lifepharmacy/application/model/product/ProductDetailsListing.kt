package com.lifepharmacy.application.model.product


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.home.Price

@SuppressLint("ParcelCreator")
@Parcelize
data class ProductDetailsListing(
//    var active: Boolean = false,
//    var brand: Brand = Brand(),
//    var category: Category = Category(),
//    var collections: List<Collection> = listOf(),
    var description: String = "",
    @SerializedName("description_ar")
    var descriptionAr:String = "",
    var id: String = "",
    var images: com.lifepharmacy.application.model.home.Images = com.lifepharmacy.application.model.home.Images(),
    var inventory: Inventory = Inventory(),
//    var options: ArrayList<Option> = ArrayList(),
    var prices: ArrayList<Price> = ArrayList(),
    @SerializedName("short_description")
    var shortDescription: String = "",
    @SerializedName("short_description_ar")
    var shortDescriptionAr: String = "",
//    var tags: List<Tag> = listOf(),
    var title: String = "",
    @SerializedName("title_ar")
    var titleAr: String = "",
//    @SerializedName("updated_at")
//    var updatedAt: String = ""
) : Parcelable