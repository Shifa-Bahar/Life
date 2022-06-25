import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@SuppressLint("ParcelCreator")
data class ValidateReward(
  @SerializedName("reward_campaign_id")
  val reward_campaign_id: String,
  @SerializedName("reward_code")
  val reward_code: String,
  @SerializedName("user_id")
  val user_id: String,
  @SerializedName("phone")
  val phone: String,
  @SerializedName("amount")
  val amount: String,
  @SerializedName("type")
  val type: String,
  @SerializedName("redeemed_at_store")
  val redeemed_at_store: String,
  @SerializedName("updated_at")
  val updated_at: String,
  @SerializedName("created_at")
  val created_at: String,
  @SerializedName("id")
  val id: Int,
  @SerializedName("reward_campaign")
  val reward_campaign: @RawValue Reward_campaign
)