package com.lifepharmacy.application.model.product


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.appsflyer.AFInAppEventParameterName
import com.lifepharmacy.application.enums.ShipmentType
import com.lifepharmacy.application.model.category.CategoryShort
import com.lifepharmacy.application.model.home.Price
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lifepharmacy.application.model.home.PriceX
import com.lifepharmacy.application.utils.CalculationUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeInt
import org.json.JSONObject
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@SuppressLint("ParcelCreator")
@Parcelize
data class ProductDetails(
  var active: Boolean = false,
  var brand: Brand? = Brand(),
//    var category: Category = Category(),
//    var collections: List<Collection> = listOf(),
  var description: String = "",
  @SerializedName("description_ar")
  var descriptionAr: String = "",

  @SerializedName("_id")
  var mid: String = "",

  var id: String = "",
  var images: com.lifepharmacy.application.model.home.Images = com.lifepharmacy.application.model.home.Images(),
  var inventory: Inventory? = Inventory(),
//    var options: ArrayList<Option> = ArrayList(),
  var prices: ArrayList<Price>? = ArrayList(),
  @SerializedName("short_description")
  var shortDescription: String? = "",
  @SerializedName("short_description_ar")
  var shortDescriptionAr: String = "",
//    var tags: List<Tag> = listOf(),
  var title: String = "",
  @SerializedName("title_ar")
  var titleAr: String? = "",
  @SerializedName("slug")
  var slug: String? = "",
  @SerializedName("rating")
  var rating: String? = "",
  @SerializedName("availability")
  var availability: Availability? = Availability(),
  @SerializedName("label")
  var label: LabelModel? = LabelModel(),
  @SerializedName("is_taxable")
  var isTaxable: Boolean? = true,
  @SerializedName("offers")
  var offers: Offers? = Offers(),
  @SerializedName("vat_percentage")
  var vatPercentage: Double? = 0.0,
  @SerializedName("maximum_salable_qty")
  var maximumSalable: Int? = 0,
  var vatAmount: Double? = 0.0,
  var totalAfterVat: Double? = 0.0,
  @SerializedName("categories")
  var categories: ArrayList<CategoryShort>? = ArrayList(),

  @SerializedName("unitprice")
  var unitPrice: Double? = null,
  @SerializedName("vat_p")
  var vatPercentageOutOfStock: Double? = null,
//    @SerializedName("updated_at")
//    var updatedAt: String = ""
) : Parcelable {


  fun getRatingInInt(): Double {
    return rating?.stringToNullSafeDouble() ?: 0.0
  }

  fun getShortJsonString(context: Context, position: Int = 1, qty: Int? = null): JSONObject {
    val preference: SharedPreferences =
      context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val compainID = preference.getString(
      ConstantsUtil.SH_COMPAIN_ID,
      null
    )
    val hashMap = HashMap<String, String>()

    val jsonObject = JSONObject()
    jsonObject.put("product_id", id)
    jsonObject.put("_id", mid)
    jsonObject.put("sku", inventory?.sku ?: "")
    jsonObject.put("category", categories?.firstOrNull()?.name ?: "")
    jsonObject.put("name", title ?: "")
    jsonObject.put("brand", brand?.name ?: "")
    jsonObject.put("price", getRegularAmountVAT(context)?.currencyFormat() ?: "")
    jsonObject.put("quantity", qty.toString())
    jsonObject.put("url", "")
    jsonObject.put("currency", getRelativeCurrency(context))
    jsonObject.put("value", getOfferAmount(context).currencyFormat())
    jsonObject.put("image_url", "")
    jsonObject.put("coupon", compainID ?: "")
    jsonObject.put("position", position.toString())


//    hashMap["product_id"] = id
//    hashMap["_id"] = mid
//    hashMap["sku"] = inventory?.sku ?: ""
//    hashMap["category"] = categories?.firstOrNull()?.name ?: ""
//    hashMap["name"] = title ?: ""
//    hashMap["brand"] = brand?.name ?: ""
//    hashMap["price"] = getRegularAmountVAT(context)?.currencyFormat() ?: ""
//    hashMap["quantity"] = qty.toString()
//    hashMap["url"] = ""
//    hashMap["currency"] = getRelativeCurrency(context)
//    hashMap["value"] = getOfferAmount(context).currencyFormat()
//    hashMap["image_url"] = ""
//    hashMap["coupon"] = compainID ?: ""
//    hashMap["position"] = position.toString()
//    val gson = Gson()
//    return gson.toJson(hashMap)
    return jsonObject
  }

  fun getShortHashMap(context: Context, position: Int = 1, qty: Int? = null): HashMap<String, Any> {
    val preference: SharedPreferences =
      context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val compainID = preference.getString(
      ConstantsUtil.SH_COMPAIN_ID,
      null
    )
    val hashMap = HashMap<String, Any>()

    hashMap["product_id"] = id
    hashMap["_id"] = mid
    hashMap["sku"] = inventory?.sku ?: ""
    hashMap["category"] = categories?.firstOrNull()?.name ?: ""
    hashMap["name"] = title ?: ""
    hashMap["brand"] = brand?.name ?: ""
    hashMap["price"] = getRegularAmountVAT(context)?.currencyFormat() ?: ""
    hashMap["quantity"] = qty.toString()
    hashMap["url"] = ""
    hashMap["currency"] = getRelativeCurrency(context)
    hashMap["value"] = getOfferAmount(context).currencyFormat()
    hashMap["image_url"] = ""
    hashMap["coupon"] = compainID ?: ""
    hashMap["position"] = position.toString()
    return hashMap
  }

  fun getShortHashForAppFlyMap(
    context: Context,
    position: Int = 1,
    qty: Int? = null,
    userId: String = ""
  ): HashMap<String, Any> {
    val preference: SharedPreferences =
      context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val compainID = preference.getString(
      ConstantsUtil.SH_COMPAIN_ID,
      null
    )
    val brands = categories?.map {
      it.name
    }
    val eventValue = HashMap<String, Any>();
    eventValue[AFInAppEventParameterName.PRICE] =
      getRegularAmountVAT(context)?.currencyFormat() ?: ""
    eventValue[AFInAppEventParameterName.CONTENT_ID] = mid
    eventValue[AFInAppEventParameterName.CONTENT_TYPE] =
      "${brands?.joinToString(separator = ",")}}";
    eventValue[AFInAppEventParameterName.QUANTITY] = qty.toString()

    return eventValue
  }

  fun stockAvailability(qty: Int = 0): ShipmentType {
    return if (availability != null) {
      availability!!.getAvailability(qty)
    } else {
      ShipmentType.OUT_OF_STOCK
    }
  }

  fun getOfferAmount(context: Context): Double {
    val offerAmount =
      CalculationUtil.getAmountAfterDiscount(
        this,
        this.getDiscountPercentageValue(),
        context = context
      )
    return offerAmount
  }

  fun getRelativePrice(context: Context): Price? {
    val preference: SharedPreferences =
      context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val code = preference.getString(
      ConstantsUtil.SH_Country,
      null
    )
    var price =
      prices?.firstOrNull { price -> price.countryCode == code }

    if (price == null) {
      price = if (!prices.isNullOrEmpty()) prices?.firstOrNull()
      else {
        Price(
          countryCode = "ae",
          currency = "AED",
          price = PriceX(
            0.0, 0.0
          )
        )
      }
    }
    price?.let {
      preference.edit().putString(ConstantsUtil.SH_CURRENCY, price.currency).apply()
    }
    return price
  }

  fun getRelativeCurrency(context: Context): String {
    val preference: SharedPreferences =
      context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val code = preference.getString(
      ConstantsUtil.SH_Country,
      null
    )
    var price = prices?.firstOrNull { price -> price.countryCode == code }
    if (price == null) {
      if (!prices.isNullOrEmpty()) {
        price = prices?.first()
      }
    }
    return price?.currency ?: "AED"
  }

  fun getRegularPriceWithoutVAT(context: Context): Double? {
    val price = getRelativePrice(context)
    return (price?.price?.regularPrice?.minus(getRegularAmountVAT(context) ?: 0.0))
  }

  fun getDiscountPercentageValue(): Double {
    return if (offers?.type == "flat_percentage_discount") {
      offers?.value?.toDouble() ?: 0.0
    } else {
      0.0
    }
  }

  fun getDiscountedAmount(context: Context): Double? {
    var amount = 0.0
    amount = if (offers != null && offers?.value != null) {
      getRegularPriceWithoutVAT(context)?.times(offers?.value?.toDouble()?.div(100.0) ?: 1.0) ?: 0.0
    } else {
      0.0
//      val price = getRelativePrice(context)
//      price?.price?.regularPrice?:0.0
    }
    return amount
  }

  fun getOfferPriceWithoutVAT(context: Context): Double? {
    val price = getRelativePrice(context)

//    val withoutVat = (price?.price?.regularPrice?.minus(getRegularAmountVAT(context)?:0.0))
    val withoutVat = getRegularPriceWithoutVAT(context)
//    val discountWithoutVat = withoutVat?.times(offers?.value?.toDouble()?.div(100.0)!!)
    val discountWithoutVat = getDiscountedAmount(context)
    val offerWithoutVat = withoutVat?.minus(discountWithoutVat!!)
    return offerWithoutVat
  }

  fun getCalculatedDiscountPrice(context: Context): Double? {
    val price = getRelativePrice(context)

//    val withoutVat = (price?.price?.regularPrice?.minus(getRegularAmountVAT(context)?:0.0))
    val withoutVat = getRegularPriceWithoutVAT(context)
    val discountWithoutVat = withoutVat?.times(offers?.value?.toDouble()?.div(100.0)!!)
//    val offerWithoutVat = withoutVat?.minus(discountWithoutVat!!)
    val offerWithoutVat = getOfferPriceWithoutVAT(context)
    val offerWitVAT = offerWithoutVat?.plus(((offerWithoutVat * vatPercentage!!) / 100))
//    val offerAmountPerAmount = ((regularPriceWithoutVat?:0.0)*(offers?.value?.toDouble() ?: 0.0)).div(100)
//    val offerAmountWithOutVat = regularPriceWithoutVat?.minus(offerAmountPerAmount)
//    val offerAmountWithVAT = offerAmountWithOutVat?.plus(getVATAmount(offerAmountPerAmount)?:0.0)

//    Logger.d("RegularPice",price?.price?.regularPrice!!.toString())
//    Logger.d("Value",offers?.value?.toDouble()!!.toString())
//    Logger.d("PercentagePrice",((price?.price.regularPrice * offers?.value?.toDouble()!!).div(100)).toString())
//    val percentagePrice =
//      ((price?.price?.regularPrice ?: 0.0) * (offers?.value?.toDouble() ?: 0.0)).div(100)
//    return price?.price?.regularPrice?.minus(percentagePrice)
    return offerWitVAT
  }

  fun getVATAmount(amount: Double): Double? {
    if (vatPercentage == null) {
      vatPercentage = 0.0
    }
//    vatAmount = price?.price?.regularPrice?.times((vatPercentage?.div(100)!!))
    vatAmount = (amount.times(vatPercentage!!)).div((100 + vatPercentage!!))
    return vatAmount
  }

  fun getRegularAmountVAT(context: Context): Double? {
    val price = getRelativePrice(context)
    if (vatPercentage == null) {
      vatPercentage = 0.0
    }
//    vatAmount = price?.price?.regularPrice?.times((vatPercentage?.div(100)!!))
//    vatAmount = (price?.price?.regularPrice?.times(vatPercentage!!))?.div((100 + vatPercentage!!))
    return (price?.price?.regularPrice?.times(vatPercentage!!))?.div((100 + vatPercentage!!))
  }

  fun getAmountAfterAddingVAT(context: Context): Double? {
//    getVATAmount(context)
    val price = getRelativePrice(context)
    totalAfterVat = price?.price?.regularPrice?.plus(vatAmount ?: 0.0)
    return totalAfterVat
  }

  fun getAvailableQTy(): Int {
    var availableQTY: Int = 0
    val expressQTY: Int =
      (availability?.express?.getQTY() ?: 0) + (availability?.standard?.getQTY() ?: 0)
//    if (availability?.standard?.getQTY() ?: 0 > 0) {
//      availableQTY = availability?.standard?.getQTY() ?: 0
//    }
//    if (availability?.express?.getQTY() ?: 0 > 0 ) {
//      availableQTY = availability?.express?.getQTY() ?: 0
//    }
//    if (availability?.instant?.getQTY() ?: 0 > 0 && (availability?.express?.getQTY() ?: 0) < (availability?.instant?.getQTY() ?: 0)) {
//      availableQTY = availability?.instant?.getQTY() ?: 0
//    }

    availableQTY =
      if (availability?.instant?.getQTY() ?: 0 > 0 && (expressQTY) < (availability?.instant?.getQTY()
          ?: 0)
      ) {
        availability?.instant?.getQTY() ?: 0
      } else {
        expressQTY
      }
    return if (availableQTY < maximumSalable ?: 0) {
      if (availableQTY == 0) {
        0
      } else {
        availableQTY
      }
    } else {
      maximumSalable ?: 0
    }
  }

}