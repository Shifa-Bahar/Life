package com.lifepharmacy.application.model.address


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class AddressModel(
  @SerializedName("additional_info")
  var additionalInfo: String? = "",
  var area: String? = "",
  var building: String? = "",
  var city: String? = "",
  var country: String? = "",
  @SerializedName("created_at")
  var createdAt: String? = "",
  @SerializedName("flat_number")
  var flatNumber: String? = "",
  @SerializedName("google_address")
  var googleAddress: String? = "",
  var id: Int? = null,
  var latitude: String? = "0.0",
  var longitude: String? = "0.0",
  var name: String? = "",
  var phone: String? = "",
  var state: String? = "",
  @SerializedName("street_address")
  var streetAddress: String? = "",
  @SerializedName("suitable_timing")
  var suitableTiming: String? = "",
  var type: String? = "",
  @SerializedName("updated_at")
  var updatedAt: String? = "",
  @SerializedName("user_id")
  var userId: Int? = null,
  @SerializedName("address_id")
  var addressId: Int? = null
) : Parcelable {
  fun isAddressValid(): Boolean {
    return latitude != null && latitude ?: 0.0 != 0.0 && longitude != null && longitude ?: 0.0 != 0.0
  }

}