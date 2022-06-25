package com.lifepharmacy.application.model.orders.shipment


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class LogisticCompanyModel(
    @SerializedName("name")
    var name: String? = ""
) : Parcelable