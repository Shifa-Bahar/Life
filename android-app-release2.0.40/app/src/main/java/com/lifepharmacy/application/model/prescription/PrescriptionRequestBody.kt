package com.lifepharmacy.application.model.prescription


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class PrescriptionRequestBody(
//    @SerializedName("e_rx_number")
//    var eRxNumber: String? = "",
//    @SerializedName("insurance_member_id")
//    var insuranceMemberId: String? = "",
    @SerializedName("prescription")
    var prescription: ArrayList<String>? = ArrayList(),
    @SerializedName("emirates_id_front")
    var emirateIdFront: String? = "",
    @SerializedName("emirates_id_back")
    var emirateIdBack: String? = "",
    @SerializedName("insurance_card_front")
    var insuranceCardFront: String? = "",
    @SerializedName("insurance_card_back")
    var insuranceCardBack: String? = "",
    @SerializedName("notes")
    var notes: String? = "",
    @SerializedName("address_id")
    var addressId: String? = "",
) : Parcelable