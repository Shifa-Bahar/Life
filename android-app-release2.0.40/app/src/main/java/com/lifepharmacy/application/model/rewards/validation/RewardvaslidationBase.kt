import com.google.gson.annotations.SerializedName
import com.lifepharmacy.application.model.pcr.pcrlist.PcrData

data class Json4Kotlin_Base(

  @SerializedName("success")
  val success: Boolean,
  @SerializedName("message")
  val message: String,
  @SerializedName("data")
  val data: PcrData
)