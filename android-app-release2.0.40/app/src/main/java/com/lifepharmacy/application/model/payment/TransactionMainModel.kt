package com.lifepharmacy.application.model.payment


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.model.category.Children
import com.lifepharmacy.application.model.orders.OrderResponseModel
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
data class TransactionMainModel(
  @SerializedName("id")
  var id: Int? = 0,
  @SerializedName("order_id")
  var orderId: Int? = 0,
  @SerializedName("status")
  var status: Int? = 0,
  @SerializedName("type")
  var type: String? = "",
  @SerializedName("return_order_id")
  var return_order_id: String? = "",
  @SerializedName("purpose")
  var purpose: String? = "",
  @SerializedName("amount")
  var amount: Double? = 0.0,
  @SerializedName("order")
  var order: OrderResponseModel? = OrderResponseModel(),
//  @SerializedName("details")
//  var details: ArrayList<DetailsTransaction>? = ArrayList(),
//  var details: DetailsTransaction? = DetailsTransaction(),
  @SerializedName("created_at")
  var createdAt: String? = "",
  @SerializedName("updated_at")
  var updateAt: String? = "",
  @SerializedName("method")
  var method: String? = "",
) : Parcelable{
  fun getMethodShowAble():String?{
    return method?.capitalize(Locale.ROOT)
  }
}