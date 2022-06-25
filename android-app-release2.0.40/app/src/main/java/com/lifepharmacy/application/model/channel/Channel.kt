package com.lifepharmacy.application.model.channel


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Channel(
  @SerializedName("utmcampaignid")
  var utmcampaignid: String? = "",
  @SerializedName("utmsource")
  var utmsource: String? = "",
  @SerializedName("channeldate")
var channeldate: Date
) : Parcelable