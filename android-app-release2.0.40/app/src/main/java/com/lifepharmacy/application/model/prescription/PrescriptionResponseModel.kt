package com.lifepharmacy.application.model.prescription


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class PrescriptionResponseModel(
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("e_rx_number")
    var eRxNumber: String? = "",
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("insurance_member_id")
    var insuranceMemberId: String? = "",
    @SerializedName("prescription")
    var prescription: List<String>? = listOf(),
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("status_label")
    var statusLabel: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("user_id")
    var userId: Int? = 0
) : Parcelable