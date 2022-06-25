package com.lifepharmacy.application.model.config


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class SameDayDeliveryZone(
  @SerializedName("polygons")
  var polygons: ArrayList<Polygon>? = ArrayList()
) : Parcelable