package com.lifepharmacy.application.utils

/**
 * Created by Zahid Ali
 */
object URLs {


  const val DEEPLINK_BASE_URL = ""

  const val INST_LINK = "https://www.instagram.com/lifepharmacyme/"
  const val FACEBOOK_LINK = "https://www.facebook.com/lifepharmacyme"
  const val TWITTER_LINK = "https://twitter.com/lifepharmacyme"
  const val LINKEDIN_LINK = "https://www.linkedin.com/company/lifepharmacy/"


//  const val BASE_URL = "https://lifedev.healthlineme.com/"
//  const val BASE_URL = "https://prodapp.lifepharmacy.com/"


//  const val BASE_URL = "https://lifedev.healthlineme.com/"

  //  const val BASE_URL = "http://20.196.2.150/api/"
  private const val API = "api/"
  const val CART = "${API}carts/"
  private const val AUTH = "${API}auth/"
  private const val USER = "${API}user/"
  private const val WISH_LIST = "${USER}wish-list/"
  const val CHECK_TOKEN = "${AUTH}check-token"
  const val CONFIG_FILE = "files/config.json"
  const val REQUEST_OTP = "${AUTH}request-otp?"

  const val CONFIG = "${API}config"
  const val AVAILABLE_SLOTS = "${API}available-slots"
  const val NEW_AVAILABLE_SLOTS = "${API}available-new-slots"

  const val VERIFY_OTP = "${AUTH}verify-otp"
  const val UPDATE_USER = "${API}update-user"
  const val UPDATE_USER_AND_SEND_OTP = "${AUTH}update-missing-info"
  const val HOME = "${API}home-screen"
  const val LANDING_PAGE = "${API}pages/"
  const val INAPPMSG = "${API}app/home-page"
  const val HOME_PRODUCTS_FEED = "${API}get-product-feed"
  const val RECOMMEND_PRODUCTS = "${USER}recommended-for-you"
  const val USER_MESSAGES = "${USER}messages"
  const val USER_MESSAGES_CHANGE_STATUS = "${USER}messages/update-status"
  const val TRENDING_SEARCH = "${API}trending-search/"
  const val PRODUCTS = "${API}products"
  const val PRODUCTS_REVIEWS = "${API}products"
  const val NOTIFY_PRODUCT = "${API}notify-request/create"
  const val CATEGORIES = "${API}categories"
  const val ROOT_CATEGORY = "${API}categories/root"
  const val CATEGORY_BY_ID = "${API}categories/get-category-by-id"
  const val FILTERS = "${API}products/filters"
  const val SAVE_ADDRESS = "${USER}save-address"
  const val GET_ADDRESS = "${USER}addresses"
  const val DELETE_ADDRESS = "${USER}delete-address"
  const val CREATE_ORDER = "${API}orders/create"
  const val CREATE_OUT_OF_STOCK_ORDER = "${API}orders/create-outofstock-order"
  const val CREATE_TRANSACTION = "${API}transactions/create"
  const val CREATE_TRANSACTION_NEW = "${API}transactions/create-new"
  const val RETURN_TO_CARD = "${API}order-returns/refund-to-card"
  const val ORDERS_LIST = "${API}orders"
  const val SUB_ORDER_DETAIL = "${API}orders/sub-order"
  const val PRESCRIPTION_ORDERS_LIST = "${API}prescription-requests"
  const val UPLOAD_FILE = "${API}upload-file"
  const val VOUCHERS_LIST = "${USER}gift-vouchers"
  const val REWARD_LIST = "${API}rewards"
  const val REWARD_LIST_DETAIL = "${API}rewards/"
  const val REWARDS_VALIDATE = "${API}rewards/validate"
  const val PCR_LIST = "${API}appointments"
  const val PCR_LIST_DATE = "${API}appointments"
  const val PCR_ADDRESS_LIST = "${API}user/addresses"
  const val PCR_CREATE_ORDER = "${API}appointments"
  const val CREATE_PCR_TRANSACTION = "${API}transactions/create"
  const val PCR_APPOINMENT_LIST = "${API}appointments"
  const val PCR_APPOINMENT_DETAILS = "${API}appointments/details/"

  const val OUTLET_DETAILS = "${API}outlets/details"


  const val EVENTS_API = "https://mp.lifepharmacy.com/api/event"


  const val ADD_TO_WISH_LIST = "${WISH_LIST}add/"
  const val DELETE_FROM_WISH_LIST = "${WISH_LIST}delete"
  const val GET_WISH_LIST = WISH_LIST

  const val USER_CARDS = "${USER}saved-cards"
  const val DELETE_CARD = "${USER}delete-card"
  const val TRANSACTIONS = "${API}transactions"
  const val SEARCH = "${API}search"

  //CART

  const val CREATE_CART = "${CART}create"
  const val UPDATE_CART = "${CART}update"
  const val COUPON = "${API}coupons/validate "

  //RETURNS
  const val RETURN_ORDER = "${API}order-returns/create"
  const val RETURN_ORDER_LIST = "${API}order-returns"
  const val BUY_IT_AGAIN = "${API}orders/buy-again"

  //PRESCRIPTION
  const val CREATE_PRESCRIPTION = "${API}prescription-requests/create"


  ///LIF PAY
  const val LIFE_PAY_BASE_URL = "https://lifepay.lifepharmacy.com/"
  const val CREATE_LIFE_PAY_TRANSACTION = "/api/transaction/create"

  //Blog
  const val BLOG_VIEW_ALL = "https://blog.lifepharmacy.com/category/all/"
  const val BLOG_BASE_URL = "https://www.lifepharmacy.com/"
  const val BLOG_LISTING = "/wp-json/wp/v2/posts?_embed&per_page=5&order=desc"


  //RATING
  const val RATING_SHIPMENT = "${API}ratings/shipment"
  const val RATING_SUBORDER = "${API}ratings/sub-order"
  const val RATING_PRODUCT = "${API}ratings/product"
  const val RATING_ALL = "${API}ratings/all"

  const val PAGE = "${API}cms/page"

  const val NOTIFICATION = "${API}user/notifications"

  //Docs
  const val GET_DOCS = "${API}files?skip=0&take=100"
  const val CREATE_DOC = "${API}files/create"
  const val DELETE_DOC = "${API}files/delete"


  const val INVOICE = "api/orders/sub-order/####/invoice"


  const val GOOGLE_MAP_BASE_URL = "https://maps.googleapis.com/maps/api/"
  const val GOOGLE_NEAR_BY_PLACES = "directions/driving-car"


  //CUSTOME_SEARCH
  const val SEARCH_SUGESTIONS = "products-query-suggestions/search"
  const val SEARCH_QUERY = "products/search"

}