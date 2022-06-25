package com.lifepharmacy.application.model.vouchers


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class VoucherMainResponse(
    @SerializedName("terms_conditions")
    var termsConditions: String? = "",
    @SerializedName("vouchers")
    var vouchers: ArrayList<VoucherModel>? = ArrayList()
) : Parcelable