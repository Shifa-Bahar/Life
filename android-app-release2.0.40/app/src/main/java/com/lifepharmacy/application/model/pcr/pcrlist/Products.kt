package com.lifepharmacy.application.model.pcr.pcrlist

import com.google.gson.annotations.SerializedName


data class Products(

  @SerializedName("_id") val _id: String,
  @SerializedName("selected_qty") var selected_qty: Int,
  @SerializedName("visibility_btn") var visibility_btn: Boolean,
  @SerializedName("title") val title: String,
  @SerializedName("title_ar") val title_ar: String,
  @SerializedName("description_ar") val description_ar: String,
  @SerializedName("short_description") val short_description: String,
  @SerializedName("short_description_ar") val short_description_ar: String,
  @SerializedName("id") val id: String,
  @SerializedName("slug") val slug: String,
  @SerializedName("inventory") val inventory: Inventory,
  @SerializedName("brand") val brand: String,
  @SerializedName("categories") val categories: String,
  @SerializedName("tags") val tags: String,
  @SerializedName("collections") val collections: ArrayList<Collections> = ArrayList(),
  @SerializedName("prices") val prices: ArrayList<Prices> = ArrayList(),
  @SerializedName("description") val description: String,
  @SerializedName("options") val options: ArrayList<Options> = ArrayList(),
  @SerializedName("seo") val seo: Seo,
  @SerializedName("ranking") val ranking: Int,
  @SerializedName("max_salable_qty") val max_salable_qty: String,
  @SerializedName("active") val active: Boolean,
  @SerializedName("is_taxable") val is_taxable: Boolean,
  @SerializedName("is_instant_disabled") val is_instant_disabled: Boolean,
  @SerializedName("is_express_disabled") val is_express_disabled: Boolean,
  @SerializedName("min_cart_value") val min_cart_value: Int,
  @SerializedName("updated_at") val updated_at: String,
  @SerializedName("created_at") val created_at: String,
  @SerializedName("label") val label: Label,
  @SerializedName("images") val images: Images,
  @SerializedName("availability") val availability: Availability,
  @SerializedName("offers") val offers: Offers? = Offers(),
  @SerializedName("ax_price_without_vat") val ax_price_without_vat: Double,
  @SerializedName("tax_rate") val tax_rate: String,
  @SerializedName("unit") val unit: String,
  @SerializedName("rating") val rating: Int,
  @SerializedName("vat_percentage") val vat_percentage: Double,
  @SerializedName("maximum_salable_qty") val maximum_salable_qty: Int,
  @SerializedName("min_salable_qty") val min_salable_qty: Int = 1
)