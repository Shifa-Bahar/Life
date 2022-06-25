package com.lifepharmacy.application.model.payment


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class CardDeleteBody(
    @SerializedName("card_id")
    var cardId: Int? = 0
) : Parcelable