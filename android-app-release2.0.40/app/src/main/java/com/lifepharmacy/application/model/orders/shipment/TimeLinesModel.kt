package com.lifepharmacy.application.model.orders.shipment


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeLinesModel(
    @SerializedName("time_lines")
    var timeLines: ArrayList<TimeLine>? = ArrayList()
) : Parcelable