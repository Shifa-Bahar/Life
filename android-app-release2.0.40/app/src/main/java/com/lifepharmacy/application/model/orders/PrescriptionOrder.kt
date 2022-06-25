package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.address.AddressModel

@SuppressLint("ParcelCreator")
@Parcelize
data class PrescriptionOrder(
  @SerializedName("address")
    var address: AddressModel? = AddressModel(),
  @SerializedName("user")
  var user: User? = User(),
  @SerializedName("address_id")
    var addressId: Int? = 0,
  @SerializedName("created_at")
    var createdAt: String? = "",
  @SerializedName("e_rx_number")
    var eRxNumber: String? = "",
  @SerializedName("emirates_id_back")
    var emiratesIdBack: String? = "",
  @SerializedName("emirates_id_front")
    var emiratesIdFront: String? = "",
  @SerializedName("id")
    var id: Int? = 0,
  @SerializedName("insurance_card_back")
    var insuranceCardBack: String? = "",
  @SerializedName("insurance_card_front")
    var insuranceCardFront: String? = "",
  @SerializedName("insurance_member_id")
    var insuranceMemberId: String? = "",
  @SerializedName("items")
    var items: ArrayList<String>? = ArrayList(),
  @SerializedName("notes")
    var notes: String? = "",
  @SerializedName("prescription")
    var prescription: ArrayList<String>? = ArrayList(),
  @SerializedName("status")
    var status: Int? = 0,
  @SerializedName("status_label")
    var statusLabel: String? = "",
  @SerializedName("updated_at")
    var updatedAt: String? = "",
  @SerializedName("user_id")
    var userId: Int? = 0
) : Parcelable