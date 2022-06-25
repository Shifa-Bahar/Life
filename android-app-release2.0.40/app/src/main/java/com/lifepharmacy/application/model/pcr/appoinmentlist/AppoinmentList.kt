/* 
Copyright (c) 2022 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */
package com.lifepharmacy.application.model.pcr.appoinmentlist

import com.google.gson.annotations.SerializedName

data class AppoinmentList (

  @SerializedName("id") val id : Int,
  @SerializedName("user_id") val user_id : Int,
  @SerializedName("sub_order_id") val sub_order_id : Int,
  @SerializedName("address_id") val address_id : Int,
  @SerializedName("status") val status : Int,
  @SerializedName("reports") val reports : String,
  @SerializedName("created_at") val created_at : String,
  @SerializedName("updated_at") val updated_at : String,
  @SerializedName("deleted_at") val deleted_at : String,
  @SerializedName("collection_id") val collection_id : String,
  @SerializedName("start_time") val start_time : String,
  @SerializedName("end_time") val end_time : String,
  @SerializedName("collection") val collection : Collection,
  @SerializedName("total") val total : Double,
  @SerializedName("lab_test_status") val lab_test_status : String,
  @SerializedName("status_label") val status_label : String
)

