package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName



data class Details (

  @SerializedName("body") val body : Body,
  @SerializedName("header") val header : Header,
  @SerializedName("pg_response") val pg_response : Pg_response,
  @SerializedName("pg_apn_response") val pg_apn_response : Pg_apn_response
)