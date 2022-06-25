package com.lifepharmacy.application.model.config


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.lifepharmacy.application.Constants
import com.lifepharmacy.application.model.review.RatingTages

@SuppressLint("ParcelCreator")
@Parcelize
data class Config(
  @SerializedName("COD_CHARGES")
  var cODCHARGES: Double? = 5.0,
  @SerializedName("EXPRESS_DELIVERY_FEE")
  var eXPRESSDELIVERYFEE: Double? = 10.0,
  @SerializedName("INSTANT_DELIVERY_FEE")
  var iNSTANTDELIVERYFEE: Double? = 5.0,
  @SerializedName("INSTANT_TIME")
  var iNSTANTTIME: Double? = 30.0,
  @SerializedName("MINIMUM_ORDER")
  var mINIMUMORDER: Double? = 1.0,
  @SerializedName("ORDERING_ENABLED")
  var oRDERINGENABLED: Boolean? = false,
  @SerializedName("IS_OUTLET_ENABLED")
  var outletENABLED: Boolean? = false,
  @SerializedName("SAMEDAY_DELIVERY_START_TIME")
  var sameDayStartTime: Double? = 0.0,
  @SerializedName("SAMEDAY_DELIVERY_END_TIME")
  var sameDayEndTime: Double? = 14.0,
  @SerializedName("SAMEDAY_DELIVERY_ZONE")
  var sameDayDeliveryZone: SameDayDeliveryZone? = SameDayDeliveryZone(),
  @SerializedName("SPLASH_FILE_LINK")
  var splashFileLinke: String? = "",
  @SerializedName("DEFAULT_PG")
  var DEFAULT_PG: Int = 0,
  @SerializedName("CKO_PK")
  var CKO_PK: String? = "",
  @SerializedName("ARABIC_ENABLED")
  var arabicEnabled: Boolean? = false,
  @SerializedName("INSTANT_INFO")
  var instantInfo: String? = "",
  @SerializedName("EXPRESS_INFO")
  var expressInfo: String? = "",
  @SerializedName("CONTACT_PHONE ")
  var contactPhone: String? = "97145610000",
  @SerializedName("WALLET_TOPUP_ENABLED")
  var isPopupEnabled: Boolean? = false,
  @SerializedName("IS_INSTANT_DEFAULT")
  var inInstantIsDefault: Boolean? = false,
  @SerializedName("IS_COD_ENABLED")
  var isCodeEnabled: Boolean? = false,
  @SerializedName("IS_CARD_ENABLED")
  var isCardEnabled: Boolean? = false,
  @SerializedName("IS_WALLET_ENABLED")
  var isWalletEnabled: Boolean? = false,
  @SerializedName("HOME_TITLES")
  var homeTiles: HomeTiles? = HomeTiles(),
  @SerializedName("REVIEW_TAGS")
  var reviewTags: ArrayList<RatingTages>? = ArrayList(),
  @SerializedName("shimmer")
  var shimmer: Boolean? = true,
  @SerializedName("MIN_ANDROID_VERSION")
  var minimumAndroidVersion: Double? = 1.0,
  @SerializedName("MINIMUM_ORDER_VALUE")
  var minimumOrderValue: Double? = 0.0,
  @SerializedName("ACTIVE_SLOTS")
  var activeSlots: ArrayList<DeliverySlot>? = ArrayList(),
  @SerializedName("RETURN_POLICY_TEXT")
  var refundPolicyText: String? = null,
  @SerializedName("NO_COMMENT_LABEL ")
  var noCommentLable: String? = "No Comment",
  @SerializedName("BACK_ORDER_TITLE")
  var backOrder: String? = "Pre-Order",
  @SerializedName("PRESCRIPTION_BANNER")
  var prescriptionBanner: String? = "",
  @SerializedName("ACTIVE_SEARCH_ENGINE")
  var activeEngine: String? = "algolia",
  @SerializedName("CUSTOM_SEARCH_ENDPOINT")
  var customBaseURL: String? = Constants.BASE_URL,
  @SerializedName("SEARCH_AUTH_KEY")
  var searchAuthKey: String? = "123456",
) : Parcelable