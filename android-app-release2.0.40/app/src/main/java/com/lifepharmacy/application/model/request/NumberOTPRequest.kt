package com.lifepharmacy.application.model.request


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class NumberOTPRequest(
  var phone: String? = "",
  var email: String? = "",
  var name: String? = "",
  var smsChannel: String? = ""
) : Parcelable