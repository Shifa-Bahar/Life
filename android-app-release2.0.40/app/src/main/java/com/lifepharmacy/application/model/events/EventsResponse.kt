package com.lifepharmacy.application.model.events


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.User

@SuppressLint("ParcelCreator")
@Parcelize
data class EventsResponse(
  var token: String? = ""
) : Parcelable