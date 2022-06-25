package com.lifepharmacy.application.model.vouchers


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class VoucherModel(
    @SerializedName("CARDNUMBER")
    var cARDNUMBER: String? = "",
    @SerializedName("EXPIRYDATE")
    var eXPIRYDATE: String? = "",
    @SerializedName("INSERTED")
    var iNSERTED: String? = "",
    @SerializedName("ISSUEDATETIME")
    var iSSUEDATETIME: String? = "",
    @SerializedName("ISSUESTAFFID")
    var iSSUESTAFFID: String? = "",
    @SerializedName("ISSUEVALUE")
    var iSSUEVALUE: String? = "",
    @SerializedName("MANUALEXPIRY")
    var mANUALEXPIRY: String? = "",
    @SerializedName("RECEIPTID")
    var rECEIPTID: String? = "",
    @SerializedName("REDEEMDATETIME")
    var rEDEEMDATETIME: String? = "",
    @SerializedName("REDEEMSTAFFID")
    var rEDEEMSTAFFID: String? = "",
    @SerializedName("REDEEMVALUE")
    var rEDEEMVALUE: String? = "",
    @SerializedName("SENTTORHINOS")
    var sENTTORHINOS: String? = "",
    @SerializedName("STATUS")
    var sTATUS: String? = "",
    @SerializedName("STORE")
    var sTORE: String? = "",
    @SerializedName("STORENAME")
    var sTORENAME: String? = "",
    @SerializedName("TRANSACTIONID")
    var tRANSACTIONID: String? = "",
    @SerializedName("VALIDFROM")
    var vALIDFROM: String? = ""
) : Parcelable