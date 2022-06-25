package com.lifepharmacy.application.model.home


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class HomeLandingInputModel(
  var title: String = "",
  var id: String = "",
) : Parcelable {
}