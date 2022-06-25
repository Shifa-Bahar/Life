package com.lifepharmacy.application.model.orders.shipment


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KeyValueModel(
    @SerializedName("key")
    var key: String? = "",
    @SerializedName("value")
    var value: String? = ""
) : Parcelable