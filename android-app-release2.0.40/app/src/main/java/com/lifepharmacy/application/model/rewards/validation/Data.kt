import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2021 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Data(

  @SerializedName("reward_campaign_id")
  val reward_campaign_id: Int,
  @SerializedName("reward_code")
  val reward_code: Int,
  @SerializedName("user_id")
  val user_id: Int,
  @SerializedName("phone")
  val phone: Int,
  @SerializedName("amount")
  val amount: Int,
  @SerializedName("type")
  val type: Int,
  @SerializedName("redeemed_at_store")
  val redeemed_at_store: Int,
  @SerializedName("updated_at")
  val updated_at: String,
  @SerializedName("created_at")
  val created_at: String,
  @SerializedName("id")
  val id: Int,
  @SerializedName("reward_campaign")
  val reward_campaign: Reward_campaign
)