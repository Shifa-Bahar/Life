package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName

data class Appointment_details(

  @SerializedName("id") var id: Int? = 0,
  @SerializedName("user_id") var user_id: Int? = 0,
  @SerializedName("sub_order_id") var sub_order_id: Int? = 0,
  @SerializedName("address_id") var address_id: Int? = 0,
  @SerializedName("status") var status: Int? = 0,
  @SerializedName("reports") var reports: String? = "",
  @SerializedName("created_at") var created_at: String? = "",
  @SerializedName("updated_at") var updated_at: String? = "",
  @SerializedName("deleted_at") var deleted_at: String? = "",
  @SerializedName("collection_id") var collection_id: String? = "",
  @SerializedName("start_time") var start_time: String? = "",
  @SerializedName("end_time") var end_time: String? = "",
  @SerializedName("status_label") var status_label: String? = "",
)