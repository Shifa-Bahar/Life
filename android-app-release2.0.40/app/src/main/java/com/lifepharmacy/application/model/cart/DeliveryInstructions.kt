package com.lifepharmacy.application.model.cart


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeliveryInstructions(

  @SerializedName("icon_unselected")
  var icon_unselected : String? = "",
  @SerializedName("icon_selected")
  var icon_selected : String? = "",
  @SerializedName("value")
  var value : Int,
  @SerializedName("instruction")
  var instruction: String? = ""
) : Parcelable