package com.lifepharmacy.application.utils.universal

import android.Manifest.permission
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.model.filters.FilterModel
import com.lifepharmacy.application.model.home.HomeLandingInputModel
import com.lifepharmacy.application.model.notifications.NotificationPayLoad

object ConstantsUtil {

  const val BASE_URL_WEB = "https://lifepharmacy.com/"

  const val RUDDER_STACK_KEY = "1xoUy8L00sy5JJPITHLvUTTRtPk"
  const val APP_FLYER_KEY = "sxHR7PvkJ7EGJvNbViqh2U"
  const val RUDDER_STACK_URL = "http://life-analytics.lifepharmacy.com"


  const val ONE_SIGNAL_KEY = "d715ddf6-0e27-45ad-9c2a-2752ab821d3d"
//  const val PUSHER_BEAM_KEY = "c10bea37-d060-4c85-9cf0-a2fdafd3b6ce"
  const val PUSHER_BEAM_KEY = "e630f139-5373-45c4-979c-1381c481e391"
  const val GOOGLE_MAP_KEY = "AIzaSyBNCqI5mGMvpMfbjYjalutw6ohOs68apy8"

  const val SEGMENT_KEY = "1RCFLrUYb2MryikFGzuM8QDZzd1CeWXw"


  const val ANGOLIA_KEY = "6ee1276b6e16f408055dc2fa12b9a709"
  const val ANGOLIA_ID = "G3Z1KAIX76"
  const val ANGOLIA_SUGUESSTION_INDEX = "products_query_suggestions"
  const val ANGOLIA_PRODUCT_INDEX = "products"

  //  const val GOOGLE_MAP_KEY = "AIzaSyDHDZVEK612woA4m3MN9XrK8VSX5tCTwQY"
  const val LIVE_CHAT_LICENSE = "12828660"
  const val CHAT_WINDOW_GROUP = "0"

//  //ZEN-DESK
//  const val ZEN_URL = "https://lifepharmacy.zendesk.com"
//  const val ZEN_DESK_APP_ID = "3d646a72dda6f227cc18eec3c62a3e1dd48def1274910f23"
//  const val ZEN_DESK_CLIENT_ID = "mobile_sdk_client_a1b8094f49ae24fa4425"
//  const val ZEN_DESK_CHAT_ACCOUNT_KEY = "VkkmQAhxWCoIPtX4uWiUzPW6bWrg6Fyo"
//  const val ZEN_DESK_CHAT_ACCOUNT_KEY = "64ef0b08-9b55-49bd-85cc-fe73f8723bb7"

  const val MINIMUM_ORDER = 100.0
  const val INSTANT_DELIVERY_FEE = 5.0
  const val EXPRESS_DELIVERY_FEE = 10.0
  const val INSTANT_TIME = 30.0
  const val COD_CHARGES = 5.0
  const val MAX_ITEM = 10

  const val SH_CART = "Cart"
  const val SH_OFFERS_CART = "Offers_Cart"
  const val SH_WISH_LIST = "WishList"
  const val SH_CHANNEL = "Channel"
  const val SH_RECENT_SEARCH = "Recent"
  const val SH_ADDRESS = "Address"
  const val SH_CONFIG = "Config"
  const val SH_VOUCHER = "Vouchers"
  const val SH_HOME = "Home"
  const val SH_ROOT_CAT = "RootCategory"
  const val SH_LAT = "lat"
  const val SH_LONG = "long"
  const val SH_Country = "country"
  const val SH_CART_ID = "cart_id"
  const val SH_CURRENCY = "currency"
  const val SH_REVIEW_BOX = "review_box"
  const val SH_USER = "User"
  const val SH_LANGUAGE = "Language"
  const val SH_LOTTIE_URL = "lottie_url"
  const val SH_LOTTIE_FILE_TOKE = "lottie_file_token"
  const val SH_ASK_PERMISSION_DATE_TIME = "ask_permission_date_time"
  const val SH_FCM_TOKEN = "FCM_TOKEN"
  const val SH_COMPAIN_ID = "COMPAIN_ID"
  const val IS_MAIN_ACTIVITY_IN_FOREGROUND = "main_activity_foreground"
  const val SH_UUID = "uuid"
  const val SH_AUTH_TOKEN = "AUTH_TOKEN"
  const val SH_CAMPAIGN_ID = "CAMPAIGN_ID"
  const val SH_RATING_ID = "RATING_ID"
  const val SH_LAST_PAYMENT = "last_payment"
  const val APP_NAME = "com.togo.user"
  const val PARENT_FOLDER_PATH = "com.togo.user"
  const val NameTAG = "name"
  const val BEARER = "Bearer "
  const val PAYMENT_ACTIVITY_REQUEST_CODE = 105
  const val NETWORK_ERROR = "Please Check your Network"
  const val GOOGLE_LOGIN_CODE = 1901

  const val CAMERA_PIC = 101
  const val GALLERY_PIC = 102
  const val CROP_IMAGE = 103
  const val PDF_INTENT = 104


  const val TESTING_NETWORK_DELAY = 1000L

  const val DEFAULT_CAMERA_ZOOM = 15f


  ///PUSHER
  const val PUSHER_KEY = "128cf8166f3168804ec4"
  const val PUSHER_CLUSTER = "ap2"
  const val USER_PRIVATE_CHANNEL = ""
  const val ORDER_CHANNEL = "order-updates"
  const val IN_APP_MESSAGES_CHANNEL = "n-app-messages"
  const val NEW_ORDER_BROADCAST = "new-order_received"
  const val IN_APP_POPUP = "in_app_popup"
  const val HOME_NAV = "home_nave"
  const val HOME_FG_NAV = "home_fg_nave"
  const val STORE_CHANNEL_KEY = "orders_"
  const val STORE_EVENT = "App\\Events\\OrderConfirmed"
  const val USER_CHANNEL_KEY = "user_"
  const val COMMON_CHANNEL = "common_channel"
  const val IN_APP_MESSAGE_RECEIVED_EVENT = "App\\Events\\InAppMessageReceived"

  //  var orderIdFromNotification = MutableLiveData<String>()
  var notificationPayLoad = MutableLiveData<NotificationPayLoad>()
//  var gotoOrderListing = MutableLiveData<FilterModel>()
//  var gotoLandingPage = MutableLiveData<HomeLandingInputModel>()
//  var gotoProductPage = MutableLiveData<String>()


  //    for version greater than or equal to Android.M as foreground service permission is not required and gets permission with location permission
  val RequiredPermissions = arrayOf<String>(
    permission.INTERNET
//        permission.ACCESS_FINE_LOCATION,
//        permission.READ_EXTERNAL_STORAGE,
//        permission.WRITE_EXTERNAL_STORAGE,
//        permission.CAMERA,
//        permission.READ_CONTACTS,
//        permission.ACCESS_COARSE_LOCATION
  )

  //    for version greater than or equal to Android.P as foreground service permission is required separately and gets permission with FOREGROUND_SERVICE
  @RequiresApi(Build.VERSION_CODES.P)
  val RequiredPermissionsAboveP = arrayOf(
    permission.INTERNET,
//        permission.ACCESS_FINE_LOCATION,
//        permission.READ_EXTERNAL_STORAGE,
//        permission.WRITE_EXTERNAL_STORAGE,
//        permission.CAMERA,
//        permission.READ_CONTACTS,
//        permission.ACCESS_COARSE_LOCATION,
    permission.FOREGROUND_SERVICE
  )

  @RequiresApi(Build.VERSION_CODES.Q)
  val RequiredPermissionsAboveQ = arrayOf(
    permission.INTERNET,
//        permission.ACCESS_FINE_LOCATION,
//        permission.READ_EXTERNAL_STORAGE,
//        permission.WRITE_EXTERNAL_STORAGE,
//        permission.CAMERA,
//        permission.ACCESS_COARSE_LOCATION,
    permission.FOREGROUND_SERVICE,
//        permission.READ_CONTACTS,
    permission.ACCESS_BACKGROUND_LOCATION
  )
  const val PERMISSION_REQUEST_CODE = 200

  const val PERMISSION_PICTURE_REQUEST_CODE = 201
  const val PERMISSION_CONTACTS_REQUEST_CODE = 202
  const val PERMISSION_LOCATIONS_REQUEST_CODE = 203

  const val PERMISSION_READ_SMS = 204
  const val PERMISSION_CALL_NUMBER = 205
  const val PERMISSION_NUMBER_REQUEST_COD = 206
  fun validateUrl(url: String): Boolean {
    return Patterns.WEB_URL.matcher(url).matches()
  }

  val RequiredPermissionsPicture = arrayOf(
    permission.READ_EXTERNAL_STORAGE,
    permission.WRITE_EXTERNAL_STORAGE,
    permission.CAMERA
  )

  val RequiredPermissionsStorage = arrayOf(
    permission.READ_EXTERNAL_STORAGE,
    permission.WRITE_EXTERNAL_STORAGE
  )
  val RequiredPermissionsPictureX: List<String> = listOf(
    permission.READ_EXTERNAL_STORAGE,
    permission.WRITE_EXTERNAL_STORAGE,
    permission.CAMERA
  )

  val RequiredPermissionsContacts = arrayOf(
    permission.READ_CONTACTS
  )

  val requiredPermissionsCallNumber = arrayOf(
    permission.CALL_PHONE
  )

  val requestPermissionReadSMS = arrayOf(
    permission.READ_SMS
  )

  @RequiresApi(Build.VERSION_CODES.O)
  val requestPermissionReadGetDeviceNumber = arrayOf(
    permission.READ_SMS,
    permission.READ_PHONE_NUMBERS,
    permission.READ_PHONE_STATE
  )
  private val requestPermissionReadGetDeviceNumberBelowO = arrayOf(
    permission.READ_SMS,
    permission.READ_PHONE_STATE
  )
  val RequiredPermissionsLocations = arrayOf(
    permission.ACCESS_FINE_LOCATION,
    permission.ACCESS_COARSE_LOCATION
  )
  val RequiredPermissionsLocationsX: List<String> = listOf(
    permission.ACCESS_FINE_LOCATION,
    permission.ACCESS_COARSE_LOCATION
  )

  @RequiresApi(Build.VERSION_CODES.P)
  val RequiredPermissionsLocationsAboveP = arrayOf(
    permission.ACCESS_FINE_LOCATION,
    permission.ACCESS_COARSE_LOCATION,

    )

  @RequiresApi(Build.VERSION_CODES.P)
  val RequiredPermissionsLocationsAbovePX: List<String> = listOf(
    permission.ACCESS_FINE_LOCATION,
    permission.ACCESS_COARSE_LOCATION,
  )

  @RequiresApi(Build.VERSION_CODES.Q)
  val RequiredPermissionsLocationsAboveQ = arrayOf(
    permission.ACCESS_FINE_LOCATION,
    permission.ACCESS_COARSE_LOCATION,
    permission.FOREGROUND_SERVICE,

    )

  @RequiresApi(Build.VERSION_CODES.Q)
  val RequiredPermissionsLocationsAboveQX: List<String> = listOf(
    permission.ACCESS_FINE_LOCATION,
    permission.ACCESS_COARSE_LOCATION,
    permission.FOREGROUND_SERVICE,

    )

  fun getReadPhoneNumber(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      requestPermissionReadGetDeviceNumber
    } else {
      requestPermissionReadGetDeviceNumberBelowO
    }
  }

  fun getLocationList(): List<String> {
    lateinit var permissionX: List<String>
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      permissionX =
        RequiredPermissionsLocationsAbovePX
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      permissionX =
        RequiredPermissionsLocationsAboveQX
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      permissionX =
        RequiredPermissionsLocationsX
    }
    return permissionX
  }

  fun getLocationListNormal(): Array<String> {
    lateinit var permissionX: Array<String>
    when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
        permissionX =
          RequiredPermissionsLocationsAboveP
      }
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
        permissionX =
          RequiredPermissionsLocationsAboveQ
      }
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
        permissionX =
          RequiredPermissionsLocations
      }
    }
    return permissionX
  }
}