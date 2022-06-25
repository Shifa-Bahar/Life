package com.lifepharmacy.application.model.address


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class AddressDeleteRequest(
    @SerializedName("address_id")
    var addressId: Int = 0
) : Parcelable