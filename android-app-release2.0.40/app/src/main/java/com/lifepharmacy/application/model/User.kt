package com.lifepharmacy.application.model


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import org.json.JSONObject

@SuppressLint("ParcelCreator")
@Parcelize
data class User(
  @SerializedName("created_at")
  var createdAt: String = "",
  @SerializedName("device_id")
  var deviceId: String = "",
  @SerializedName("device_name")
  var deviceName: String = "",
  var email: String? = "",
  @SerializedName("email_verified_at")
  var emailVerifiedAt: String? = "",
  @SerializedName("gender")
  var gender: String? = "",
  @SerializedName("dob")
  var dob: String? = "",
  var id: Int = 0,
  var name: String? = "",
  var phone: String? = "",
  @SerializedName("phone_verified_at")
  var phoneVerifiedAt: String? = "",
  var photo: String? = "",
  @SerializedName("updated_at")
  var updatedAt: String = "",
  @SerializedName("wallet_balance")
  var walletBalance: Double? = 0.0
) : Parcelable {
  fun getShortJsonString(): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("name", name)
    jsonObject.put("email", email)
    jsonObject.put("phone", phone)
    jsonObject.put("gender", gender)
    jsonObject.put("dob", dob)
    jsonObject.put("device_id", deviceId)

    return jsonObject
  }

  fun getHashMap(): HashMap<String, Any> {
    val hashMap = HashMap<String, Any>()
    hashMap["name"] = name ?: ""
    hashMap["email"] = email ?: ""
    hashMap["phone"] = phone ?: ""
    hashMap["gender"] = gender ?: ""
    hashMap["dob"] = dob ?: ""
    hashMap["device_id"] = deviceId ?: ""

    return hashMap
  }
}