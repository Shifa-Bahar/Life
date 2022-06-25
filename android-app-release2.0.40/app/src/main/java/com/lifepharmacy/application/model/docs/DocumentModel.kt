package com.lifepharmacy.application.model.docs


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.utils.universal.Utils
import okio.Utf8

@SuppressLint("ParcelCreator")
@Parcelize
data class DocumentModel(
    @SerializedName("belongs_to")
    var belongsTo: Int? = 0,
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("file_name")
    var fileName: String? = "",
    @SerializedName("file_type")
    var fileType: String? = "",
    @SerializedName("file_url")
    var fileUrl: String? = "",
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("updated_at")
    var updatedAt: String? = ""
) : Parcelable{
  fun getDocTypForShow():String{
   return Utils.getDocTypeForShow(fileType?:"emirates_id_front")
  }
}