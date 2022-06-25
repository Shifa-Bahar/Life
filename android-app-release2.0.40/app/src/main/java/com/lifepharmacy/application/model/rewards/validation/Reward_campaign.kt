import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Reward_campaign(
  @SerializedName("id")
  val id: Int,
  @SerializedName("title")
  val title: String,
  @SerializedName("description")
  val description: String,
  @SerializedName("banner")
  val banner: String,
  @SerializedName("icon")
  val icon: String,
  @SerializedName("status")
  val status: Int,
  @SerializedName("start_at")
  val start_at: String,
  @SerializedName("end_at")
  val end_at: String,
  @SerializedName("created_by")
  val created_by: Int,
  @SerializedName("terms")
  val terms: String,
  @SerializedName("compensated_by")
  val compensated_by: String,
  @SerializedName("amount")
  val amount: Double,
  @SerializedName("type")
  val type: Int,
  @SerializedName("created_at")
  val created_at: String,
  @SerializedName("updated_at")
  val updated_at: String,
  @SerializedName("type_label")
  val type_label: String
)