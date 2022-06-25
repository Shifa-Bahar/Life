package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Driver(
  @SerializedName("created_at")
  var createdAt: String? = "",
  @SerializedName("device_id")
  var deviceId: String? = "",
  @SerializedName("device_name")
  var deviceName: String? = "",
  @SerializedName("email")
  var email: String? = "",
  @SerializedName("email_verified_at")
  var emailVerifiedAt: String? = "",
  @SerializedName("id")
  var id: Int? = 0,
  @SerializedName("is_customer")
  var isCustomer: Int? = 0,
  @SerializedName("name")
  var name: String? = "John Doe",
  @SerializedName("phone")
  var phone: String? = "",
  @SerializedName("phone_verified_at")
  var phoneVerifiedAt: String? = "",
  @SerializedName("photo")
  var photo: String? = "",
  @SerializedName("updated_at")
  var updatedAt: String? = "",
  @SerializedName("wallet_balance")
  var walletBalance: Int? = 0
) : Parcelable