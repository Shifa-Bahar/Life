package com.lifepharmacy.application.model.search.custome


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Meta(
  @SerializedName("request_id")
  var requestId: String? = ""
) : Parcelable