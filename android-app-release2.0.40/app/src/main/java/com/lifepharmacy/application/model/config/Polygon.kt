package com.lifepharmacy.application.model.config


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng

@SuppressLint("ParcelCreator")
@Parcelize
data class Polygon(
    @SerializedName("coordinates")
    var coordinates: ArrayList<Coordinate>? = ArrayList()
) : Parcelable{
  fun getLaLngArrayList():ArrayList<LatLng>{
    val list = ArrayList<LatLng>()
    for(coordinate in coordinates!!){
      list.add(LatLng(coordinate.lat ?:0.0,coordinate.long?:0.0))
    }
    return list
  }
}