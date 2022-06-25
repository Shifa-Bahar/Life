package com.lifepharmacy.application.model.home


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.product.Availability
import com.lifepharmacy.application.model.product.Inventory
import com.lifepharmacy.application.model.product.Offers
import com.lifepharmacy.application.utils.universal.ConstantsUtil

@SuppressLint("ParcelCreator")
@Parcelize
data class ProductDetailsForListing(
  var active: Boolean = false,
//    var category: Category = Category(),
//    var collections: List<Collection> = listOf(),
  var description: String = "",
  @SerializedName("description_ar")
  var descriptionAr: String = "",
  var id: String = "",
  var images: com.lifepharmacy.application.model.home.Images = com.lifepharmacy.application.model.home.Images(),
  var inventory: Inventory? = Inventory(),
//    var options: ArrayList<Option> = ArrayList(),
  var prices: ArrayList<Price> = ArrayList(),
  @SerializedName("short_description")
  var shortDescription: String = "",
  @SerializedName("short_description_ar")
  var shortDescriptionAr: String = "",
//    var tags: List<Tag> = listOf(),
  var title: String = "",
  @SerializedName("title_ar")
  var titleAr: String? = "",
  @SerializedName("rating")
  var rating: String? = "",
  @SerializedName("availability")
  var availability: Availability? = Availability(),
  @SerializedName("is_taxable")
  var isTaxable: Boolean? = true,
  @SerializedName("offers")
  var offers: Offers? = Offers(),
//    @SerializedName("updated_at")
//    var updatedAt: String = ""
) : Parcelable{
  fun getRelativePrice(context: Context): Price? {
    val preference: SharedPreferences =
      context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val code = preference.getString(
      ConstantsUtil.SH_Country,
      null
    )
    var price = prices.firstOrNull { price -> price.countryCode == code }
    price?.let {
      preference.edit().putString(ConstantsUtil.SH_CURRENCY, price.currency).apply()
    }
    return price
  }
  fun getCalculatedDiscountPrice(context: Context): Double? {
    val price = getRelativePrice(context)
//    Logger.d("RegularPice",price?.price?.regularPrice!!.toString())
//    Logger.d("Value",offers?.value?.toDouble()!!.toString())
//    Logger.d("PercentagePrice",((price?.price.regularPrice * offers?.value?.toDouble()!!).div(100)).toString())
    val percentagePrice = ((price?.price?.regularPrice?:0.0) * (offers?.value?.toDouble()?:0.0)).div(100)
    return price?.price?.regularPrice?.minus(percentagePrice)
  }
}