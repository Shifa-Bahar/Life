package com.lifepharmacy.application.model.pcr.pcrrequestmodel
import com.google.gson.annotations.SerializedName



data class PcrTransactioncreate (

  @SerializedName("date") val date : String,
  @SerializedName("time") val time : String,
  @SerializedName("additional_charges_amount") val additional_charges_amount : Int,
  @SerializedName("address_id") val address_id : Int,
  @SerializedName("items") val items : ArrayList<Items>,
  @SerializedName("discount") val discount : Int,
  @SerializedName("vat") val vat : Int,
  @SerializedName("sub_total") val sub_total : Int,
  @SerializedName("total") val total : Int
)