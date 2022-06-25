package com.lifepharmacy.application.model.pcr.pcrrequestmodel


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.pcr.pcrrequestmodel.Items

@SuppressLint("ParcelCreator")
@Parcelize
data class PlacePcrOrderRequest(
  @SerializedName("date")
  var date: String? = "",
  @SerializedName("time")
  var time: String? = "",
  var additional_charges_amount: Int = 0,
  @SerializedName("address_id")
  var addressId: Int = 0,
  @SerializedName("collection_id")
  var collection_id: String? = "",
  var items: ArrayList<Items> = ArrayList(),
  var discount: Double = 0.0,
  @SerializedName("vat")
  var vat: Double? = 0.0,
  var sub_total: Double = 0.0,
  var total: Double = 0.0
) : Parcelable