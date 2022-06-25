package com.lifepharmacy.application.model.response


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.User

@SuppressLint("ParcelCreator")
@Parcelize
data class VerifyOTPResponse(
  var token: String? = "",
  var user: User = User()
) : Parcelable