package com.lifepharmacy.application.model.address


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class AddressMainModel(
  var addresses: ArrayList<AddressModel>? = ArrayList()
) : Parcelable