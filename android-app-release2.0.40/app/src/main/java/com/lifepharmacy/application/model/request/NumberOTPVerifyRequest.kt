package com.lifepharmacy.application.model.request


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class NumberOTPVerifyRequest(
  var code: Int = 0,
  @SerializedName("device_id")
  var deviceId: String = "",
  @SerializedName("device_name")
  var deviceName: String = "",
  var phone: String = "",
  var email: String = "",

  ) : Parcelable