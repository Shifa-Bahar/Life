package com.lifepharmacy.application.model.payment


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class CardMainModel(
  @SerializedName("card_data")
  var cardData: CardData? = CardData(),
  @SerializedName("id")
  var id: Int? = 0,
  @SerializedName("pg")
  var pg: Int? = 0,
  @SerializedName("user_id")
  var userId: Int? = 0
) : Parcelable