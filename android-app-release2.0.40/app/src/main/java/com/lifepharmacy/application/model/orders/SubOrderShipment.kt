package com.lifepharmacy.application.model.orders


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.orders.shipment.LogisticCompanyModel

@SuppressLint("ParcelCreator")
@Parcelize
data class SubOrderShipment(
  @SerializedName("amount")
  var amount: Double = 0.0,
  @SerializedName("assigned_to")
  var assignedTo: Int? = 0,
  @SerializedName("created_at")
  var createdAt: String? = "",
  @SerializedName("created_by")
  var createdBy: Int? = 0,
  @SerializedName("delivery_proof")
  var deliveryProof: String? = "",
  @SerializedName("driver")
  var driver: Driver? = Driver(),
  @SerializedName("dropoff")
  var dropoff: Int? = 0,
  @SerializedName("id")
  var id: Int? = 0,
  @SerializedName("payment_status")
  var paymentStatus: Int? = 0,
  @SerializedName("pickup")
  var pickup: Int? = 0,
  @SerializedName("settlement_id")
  var settlementId: String? = "",
  @SerializedName("signature")
  var signature: String? = "",
  @SerializedName("status")
  var status: Int? = 0,
  @SerializedName("status_label")
  var statusLabel: String? = "",
  @SerializedName("sub_order_id")
  var subOrderId: Int? = 0,
  @SerializedName("updated_at")
  var updatedAt: String? = "",
  @SerializedName("pickup_address")
  var pickUpAddress: AddressModel? = AddressModel(),
  @SerializedName("dropoff_address")
  var dropOffAddress: AddressModel? = AddressModel(),
  @SerializedName("logistic_company")
  var logisticCompany: LogisticCompanyModel? = LogisticCompanyModel(),
  @SerializedName("rating")
  var rating: Rating? = Rating(),
) : Parcelable