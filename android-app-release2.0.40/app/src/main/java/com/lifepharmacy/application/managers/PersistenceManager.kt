package com.lifepharmacy.application.managers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.model.CartAll
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.WishListAll
import com.lifepharmacy.application.model.address.AddressMainModel
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.cart.DeliveryInstructions
import com.lifepharmacy.application.model.cart.OffersAll
import com.lifepharmacy.application.model.category.RootCategoryMainModel
import com.lifepharmacy.application.model.channel.Channel
import com.lifepharmacy.application.model.channel.ChannelBase
import com.lifepharmacy.application.model.config.Config
import com.lifepharmacy.application.model.home.HomeMainModel
import com.lifepharmacy.application.model.inappmsg.Inappmsgrating
import com.lifepharmacy.application.model.pcr.pcrdatetime.Booking_slots
import com.lifepharmacy.application.model.pcr.pcrlist.Products
import com.lifepharmacy.application.model.vouchers.VoucherMainResponse
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.utils.CalculationUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.onesignal.OneSignal
import java.util.*
import javax.inject.Inject

class PersistenceManager @Inject constructor(val application: Context) {
  //  val recentsearch: MutableList<String> = ArrayList()
  //  var order: OrderResponseModel? = OrderResponseModel()
  // var utmcampaignvalue: String? = ""
  // var utmsourcevalue: String? = ""
  // var utmdate: Date? = null
  var wishlistselected: Boolean = false
  var ordercancel: Boolean = false
  var Inappmsgrating: Inappmsgrating? = null
  var recentsearch: ArrayList<String?> = ArrayList()
  var recentsearchget: ArrayList<String?> = ArrayList()
  var pcritemselected = ArrayList<Products>()
  var addressesglobal: ArrayList<AddressModel>? = ArrayList()
  var datepcrglobal: String = ""
  var timepcrglobal: String = ""
  var additional_charges_amount: Int = 0
  var completedate: String = ""
  var completetime: String = ""
  var streetaddress: String = ""
  var collection_id: String? = ""
  var areaaddress: String = ""
  var cityaddress: String = ""
  var phoneaddress: String = ""
  var appointmentinfo: String = ""
  var suborderidraing: String = ""
  var ckopk: String? = ""
  var subordernumber: String = ""
  var bookingslots = ArrayList<Booking_slots>()
  var deliveryins = ArrayList<DeliveryInstructions>()
  var addressid: Int? = 0
  var defaultpg: Int? = 0
  var cardpg: Int? = 0
  var selectedqtyglobal: Int? = 0
  var calprice: Double? = null
  var totalamount: Double = 0.0
  var totalwithoutadditonal: Double = 0.0
  var subtotal: Double = 0.0
  var discounttotal: Double = 0.0
  var vatper: Double = 0.0
  var vattotal: Double = 0.0
  var offerPrice: Double = 0.0
  lateinit var pcrpaymenttypeselected: PaymentType
//  var channel: ArrayList<Channel?> = ArrayList()

  fun channelUpdate(ChannelID: String?, ChannelSource: String?, ChannelDate: Date) {
    Log.e(TAG, "channelUpdate:$ChannelID,$ChannelSource,$ChannelDate")
    var channelsArr = getChannel().channelarray
    if (!channelsArr.isNullOrEmpty()) {
      channelsArr!!.forEach { channel ->
        if (channel?.utmcampaignid == ChannelID) {
          Log.e(TAG, "channelUpdate: same id ${channel?.utmcampaignid}")
          //ignore
        } else {
          var currdate = getCurrentDate()
          val mDifference = kotlin.math.abs(currdate.time - ChannelDate.time)
          val mDifferenceDates =
            mDifference / (24 * 60 * 60 * 1000) // Converting milli seconds to dates
          Log.e(TAG, "channelUpdate:$mDifferenceDates mDifferenceDates ")
          if (mDifferenceDates.toDouble().toInt() > 30) {
            channelsArr.remove(channel)
          }
//          channelsArr.add(channel)
          var item: ArrayList<Channel> = ArrayList()
          var channelobj = Channel(ChannelID,ChannelSource,ChannelDate)
          item.add(channelobj)
          savechannelToPref(item)
//          val mExampleList: ArrayList<ExampleItemRecyclerview> = ArrayList()
//          val Ticket = ExampleItemRecyclerview(ticket, redattore, targa, dataA, materiale, dataC)
//          mExampleList.add(Ticket)
//          var channelobj = Channel(ChannelID,ChannelSource,ChannelDate)

        }
      }
//      savechannelToPref(channelsArr)

      //Save array
//      saveChannel(channelsArr)
//      val gson = Gson()
//      val jsonStr = gson.toJson(channelsArr)
//      preference.edit().putString(ConstantsUtil.SH_CHANNEL, jsonStr).apply()
    } else {
      var item: ArrayList<Channel> = ArrayList()
      var channelobj = Channel(ChannelID,ChannelSource,ChannelDate)
      item.add(channelobj)
      savechannelToPref(item)
      Log.e(TAG, "channelUpdate: nullcase $item")
//      channelsArr!!.forEach { channel ->
//      var channelobj = Channel(ChannelID,ChannelSource,ChannelDate)
//      channelsArr.add(channelobj)
//      }
    }
  }

  fun savechannelToPref(channelsArr: ArrayList<Channel>) {
    Log.e(TAG, "savechannelToPref: $channelsArr")
    val channelList = channelsArr?.let { ChannelBase(it) }
    if (channelList != null) {
      saveChannel(channelList)
    }
  }

  fun saveChannel(item: ChannelBase) {
    Log.e(TAG, "saveChannel: $item")
    val gson = Gson()
    val jsonStr = gson.toJson(item)
    preference.edit().putString(ConstantsUtil.SH_CHANNEL, jsonStr).apply()
  }

  fun getChannel(): ChannelBase {
    val gson = Gson()
    return gson.fromJson(
      preference.getString(ConstantsUtil.SH_CHANNEL, "{}"),
      ChannelBase::class.java
    )
  }

  fun getCurrentDate(): Date {
    return Calendar.getInstance().time
  }

//  fun getCurrentDate(): Date {
//    val sdf = Date("dd-MM-yyyy")
//    return sdf
//  }

  fun updateBottomPriceValue() {
    var offerPricelocal: Double = 0.0
    var pricevalue: Double
    var vat_total: Double = 0.0
    var subtotallocal: Double = 0.0
    var discount_total: Double = 0.0
    var vat_per: Double = 0.0
    pcritemselected!!.forEach { product ->
      var rp = product.prices[0].price.regularPrice
      Log.e(TAG, "updateBottomPriceValue: selected qty ${product.selected_qty}")
      if (product.offers != null) {
        discount_total += rp * (product.offers?.value!!).div(100).times(product.selected_qty)
      }
      subtotallocal += rp.times(product.selected_qty)
      if (product.offers != null) {
        vat_total += ((rp - (rp * (product.offers?.value!!).div(100)))
          .times(product.vat_percentage).div(100)).times(product.selected_qty)
      } else {
        var t = rp.times(product.vat_percentage).div(100)
        vat_total += (rp.times(product.vat_percentage).div(100)).times(product.selected_qty)
      }

      if (product.offers != null && product.offers.type == "flat_percentage_discount") {
        var percentagePrice = rp * (product.offers.value!!) / 100
        var discountedPrice = rp - percentagePrice

        if (product != null) {
          var amountAfterVat = CalculationUtil.getPcrPriceAfterVATOGive(discountedPrice, product)
          pricevalue = amountAfterVat
        } else {
          pricevalue = discountedPrice
        }
      } else {
        if (product != null) {
          var amountAfterVat = CalculationUtil.getPcrPriceAfterVATOGive(rp, product)
          pricevalue = amountAfterVat
        } else {
          pricevalue = rp
        }
      }
      offerPricelocal += pricevalue.times(product.selected_qty)
    }
    offerPrice = offerPricelocal.currencyFormat().toDouble()
    subtotal = subtotallocal
    discounttotal = discount_total
    vattotal = vat_total
    totalwithoutadditonal = (subtotallocal - discount_total + vat_total)
//    totalamount = (subtotallocal - discount_total + vat_total + additional_charges_amount)

  }

  fun addProductfirst(product: Products) {
    pcritemselected?.add(product)

    //  pcritemselected!!.forEach { product ->
    if (product.min_salable_qty == null || product.min_salable_qty == 0) {
      product.selected_qty = 1
    } else {
      product.selected_qty = product.min_salable_qty
    }
    updateBottomPriceValue()
    // }
  }

  fun onPcrAddbtnClicked(position: Int, rootCategory: Products) {

    pcritemselected!!.forEach { product ->
      if (product.id.equals(rootCategory.id)) {
        if (product.selected_qty < product.maximum_salable_qty) {
          product.selected_qty += 1
        } else {
//          Toast.makeText(requireContext(), "MAXIMUM QUANTITY REACHED", Toast.LENGTH_SHORT)
        }
      } else {
//        Log.e(DocTypSelectionBottomSheet.TAG, "onPcrAddbtnClicked: error")
      }
    }
    updateBottomPriceValue()
//    PcrAdapter.setItemSelected(position)
  }

  fun onPcrMinusbtnClicked(position: Int, rootCategory: Products) {

    var product = (pcritemselected.first { it.id == rootCategory.id })
    if (product != null) {

      if (rootCategory.min_salable_qty == null || rootCategory.min_salable_qty == 0) {

        if (product.selected_qty == 1) {
          pcritemselected.remove(product)
        } else {
          product.selected_qty -= 1
        }
      } else {
        if (product.selected_qty == rootCategory.min_salable_qty) {
          pcritemselected.remove(product)
        } else {
          product.selected_qty -= 1
        }
        updateBottomPriceValue()
      }
    }
  }

  fun calculatevaluesproduct(product: Products, regprice: Double): String {
    var pricevaluecal: String

    if (product.offers != null && product.offers.type == "flat_percentage_discount") {
      var percentagePrice = (regprice * product.offers.value!!) / 100
      var discountedPrice = regprice - percentagePrice

      if (product != null) {
        var amountAfterVat = CalculationUtil.getPcrPriceAfterVATOGive(discountedPrice, product)
        pricevaluecal = amountAfterVat.currencyFormat()
      } else {
        pricevaluecal = discountedPrice.currencyFormat()
      }
    } else {
      if (product != null) {
        var amountAfterVat = CalculationUtil.getPcrPriceAfterVATOGive(regprice, product)
        pricevaluecal = amountAfterVat.currencyFormat()
      } else {
        pricevaluecal = regprice.currencyFormat()
      }
    }
    return pricevaluecal
  }

  fun calculateregularwithvat(product: Products, regprice: Double): String {
    var pricevaluereg: String

    if (product.offers != null && product.offers.type == "flat_percentage_discount") {
      if (product != null) {
        val amountAfterVat = CalculationUtil.getPcrPriceAfterVATOGive(regprice, product)
        pricevaluereg = amountAfterVat.currencyFormat()
      } else {
        pricevaluereg = regprice.currencyFormat()
      }
//      holder.binding?.tvCur?.visibility = View.VISIBLE
    } else {
//      holder.binding?.tvCur?.visibility = View.GONE
      pricevaluereg = regprice.currencyFormat()
    }
    return pricevaluereg
  }

  var preference: SharedPreferences =
    application.getSharedPreferences(application.packageName, Context.MODE_PRIVATE)

  fun isLoggedIn(): Boolean {
    return preference.contains(ConstantsUtil.SH_USER)
  }

  fun isReviewBoxOpened(): Boolean {
    return preference.getBoolean(ConstantsUtil.SH_REVIEW_BOX, false)
  }

  fun setReviewBoxStatus(reviewBox: Boolean) {
    preference.edit().putBoolean(ConstantsUtil.SH_REVIEW_BOX, reviewBox).apply()
  }

//  fun saveFCMToken(token: String) {
//    preference.edit().putString(ConstantsUtil.SH_FCM_TOKEN, token).apply()
//  }

  fun getFCMToken(): String? {
    return preference.getString(ConstantsUtil.SH_FCM_TOKEN, "")
  }

  fun saveToken(token: String) {
    preference.edit().putString(ConstantsUtil.SH_AUTH_TOKEN, token).apply()
  }

  fun saveRatingid(token: String) {
    preference.edit().putString(ConstantsUtil.SH_RATING_ID, token).apply()
  }

  fun saveCampaignid(campaignid: Int) {
    preference.edit().putInt(ConstantsUtil.SH_CAMPAIGN_ID, campaignid).apply()
  }

  fun getCampianid(): Int? {
    return preference.getInt(ConstantsUtil.SH_CAMPAIGN_ID, 0)
  }

  //header campain id getCompaignId saveCompianID
  fun getCompaignId(): String? {
    return preference.getString(ConstantsUtil.SH_COMPAIN_ID, "")
  }

  fun saveCompianID(compaingId: String) {
    preference.edit().putString(ConstantsUtil.SH_COMPAIN_ID, compaingId).apply()
  }


  fun setState(boolean: Boolean) {
    preference.edit().putBoolean(ConstantsUtil.IS_MAIN_ACTIVITY_IN_FOREGROUND, boolean).commit()
  }

  fun isInForeground(): Boolean {
    return preference.getBoolean(ConstantsUtil.IS_MAIN_ACTIVITY_IN_FOREGROUND, false)
  }

  fun getUUID(): String? {
    return preference.getString(ConstantsUtil.SH_UUID, "")
  }

  fun saveUUID(uuid: String) {
    preference.edit().putString(ConstantsUtil.SH_UUID, uuid).apply()
  }

  fun isThereUUID(): Boolean {
    return preference.contains(ConstantsUtil.SH_UUID)
  }


  fun getToken(): String? {
    return preference.getString(ConstantsUtil.SH_AUTH_TOKEN, null)
  }

  fun getRatingid(): String? {
    return preference.getString(ConstantsUtil.SH_RATING_ID, null)
  }


  fun saveLoggedInUser(user: User) {
    OneSignal.setExternalUserId("${user.id}")
    val gson = Gson()
    val jsonStr = gson.toJson(user)
    preference.edit().putString(ConstantsUtil.SH_USER, jsonStr).apply()
  }

  fun getLoggedInUser(): User? {
    val gson = Gson()
    return gson.fromJson(preference.getString(ConstantsUtil.SH_USER, null), User::class.java)
  }

  fun saveCartList(cart: CartAll) {
    val gson = Gson()
    val jsonStr = gson.toJson(cart)
    preference.edit().putString(ConstantsUtil.SH_CART, jsonStr).apply()
  }

  private fun isThereCartItems(): Boolean {
    return preference.contains(ConstantsUtil.SH_OFFERS_CART)
  }

  fun getCartList(): CartAll? {
    val gson = Gson()
    return gson.fromJson(preference.getString(ConstantsUtil.SH_CART, null), CartAll::class.java)
  }

  fun saveOffersCartList(cart: OffersAll) {
    val gson = Gson()
    val jsonStr = gson.toJson(cart)
    preference.edit().putString(ConstantsUtil.SH_OFFERS_CART, jsonStr).apply()
  }

  //  fun saverecent(item: String) {
////    val gson = Gson()
////    val jsonStr = gson.toJson(item)
////    preference.edit().putString(ConstantsUtil.SH_RECENT_SEARCH, jsonStr).apply()
//
//    preference.edit()
//      .putString(ConstantsUtil.SH_RECENT_SEARCH, item ?: "").apply()
//
//  }

  fun setArrayPrefs(arrayName: String, array: ArrayList<String?>, mContext: Context) {
    val prefs = mContext.getSharedPreferences("preferencename", 0)
    val editor = prefs.edit()
    editor.putInt(arrayName + "_size", array.size)
    for (i in 0 until array.size) editor.putString(arrayName + "_" + i, array[i])
    editor.apply()
  }
//  open fun setArrayPrefs(arrayName: String, array: ArrayList<String?>, mContext: Context) {
////    val prefs = mContext.getSharedPreferences("preferencename", 0)
////    val editor = prefs.edit()
//  preference.edit().putInt(arrayName + "_size", array.size)
////    preference.edit().putString(ConstantsUtil.SH_RECENT_SEARCH, arrayName)
//    for (i in 0 until array.size)
//      preference.edit().putString(arrayName + "_" + i, array[i])
//    preference.edit().apply()
//  }

  fun getArrayPrefs(arrayName: String, mContext: Context): ArrayList<String?>? {
    val prefs = mContext.getSharedPreferences("preferencename", 0)
    val size = prefs.getInt(arrayName + "_size", 0)
    val array: ArrayList<String?> = ArrayList(size)
    for (i in 0 until size) array.add(prefs.getString(arrayName + "_" + i, null))
    return array
  }
//  open fun getArrayPrefs(arrayName: String, mContext: Context): ArrayList<String?>? {
////    val prefs = mContext.getSharedPreferences("preferencename", 0)
//    val size = preference.getInt(arrayName + "_size", 0)
//    val array: ArrayList<String?> = ArrayList(size)
//    for (i in 0 until size) array.add(preference.getString(arrayName + "_" + i, null))
//    return array
//  }

  fun saverecent(recentsearch: String) {
    Log.e(TAG, "saverecent: $recentsearch")
    preference.edit().putString(ConstantsUtil.SH_RECENT_SEARCH, recentsearch).apply()
  }

  fun getrecent(): String? {
    return preference.getString(ConstantsUtil.SH_RECENT_SEARCH, null)
  }

  fun getOffersCartList(): OffersAll? {
    val gson = Gson()
    return gson.fromJson(
      preference.getString(ConstantsUtil.SH_OFFERS_CART, null),
      OffersAll::class.java
    )
  }

  //  fun getrecent(): Hitsarray? {
//
//    val preference: SharedPreferences =
//      context.applicationContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
//    val compainID = preference.getString(
//      ConstantsUtil.SH_COMPAIN_ID,
//      null
//    )
////    val gson = Gson()
////    return gson.fromJson(
////      preference.getString(ConstantsUtil.SH_RECENT_SEARCH, null),
////      Hitsarray::class.java
////    )
//  }


  fun saveWishList(cart: WishListAll) {
    val gson = Gson()
    val jsonStr = gson.toJson(cart)
    preference.edit().putString(ConstantsUtil.SH_WISH_LIST, jsonStr).apply()
  }

  fun getWishList(): WishListAll? {
    val gson = Gson()
    return gson.fromJson(
      preference.getString(ConstantsUtil.SH_WISH_LIST, null),
      WishListAll::class.java
    )
  }

  fun saveLatLong(latLng: LatLng) {
    preference.edit().putString(ConstantsUtil.SH_LAT, latLng.latitude.toString()).apply()
    preference.edit().putString(ConstantsUtil.SH_LONG, latLng.longitude.toString()).apply()
  }

  fun getLatLong(): String? {
    return preference.getString(
      ConstantsUtil.SH_LAT,
      "0.0"
    ) + "," + preference.getString(ConstantsUtil.SH_LONG, "0.0")
  }

  fun getLat(): String? {
    return preference.getString(
      ConstantsUtil.SH_LAT,
      "0.0"
    )
  }

  fun getLong(): String? {
    return preference.getString(
      ConstantsUtil.SH_LONG,
      "0.0"
    )
  }

  fun saveCountry(country: String?) {
    preference.edit().putString(ConstantsUtil.SH_Country, country ?: "ae").apply()
  }

  fun getCountry(): String {
    return preference.getString(ConstantsUtil.SH_Country, "ae").toString()
  }

  fun saveCartID(cartID: String) {
    preference.edit().putString(ConstantsUtil.SH_CART_ID, cartID).apply()
  }

  fun getCartID(): String {
    return preference.getString(ConstantsUtil.SH_CART_ID, "0").toString()
  }

  fun isThereCart(): Boolean {
    return preference.contains(ConstantsUtil.SH_CART_ID)
  }

  fun saveLastPayment(payment: String) {
    preference.edit().putString(ConstantsUtil.SH_LAST_PAYMENT, payment).apply()
  }

  fun getLastPayment(): String {
    return preference.getString(ConstantsUtil.SH_LAST_PAYMENT, "cash").toString()
  }

  fun isPaymentMethod(): Boolean {
    return preference.contains(ConstantsUtil.SH_LAST_PAYMENT)
  }

  fun saveCurrency(currency: String) {
    preference.edit().putString(ConstantsUtil.SH_CURRENCY, currency).apply()
  }

  fun getCurrency(): String {
    return preference.getString(ConstantsUtil.SH_CURRENCY, "AED") + ""
  }

  fun clearPrefs() {
    preference.edit().clear().apply()
  }

  fun clearUserData() {
    preference.edit().remove(ConstantsUtil.SH_USER).commit()
    preference.edit().remove(ConstantsUtil.SH_AUTH_TOKEN).commit()
  }

  fun clearCartID() {
    if (isThereCart()) {
      preference.edit().remove(ConstantsUtil.SH_CART_ID).commit()
    }
    if (isThereCartItems()) {
      preference.edit().remove(ConstantsUtil.SH_OFFERS_CART).commit()
    }
  }

  fun saveAddressList(address: AddressMainModel) {
    val gson = Gson()
    val jsonStr = gson.toJson(address)
    preference.edit().putString(ConstantsUtil.SH_ADDRESS, jsonStr).apply()
  }

  fun getAddressList(): AddressMainModel? {
    val gson = Gson()
    return gson.fromJson(
      preference.getString(ConstantsUtil.SH_ADDRESS, null),
      AddressMainModel::class.java
    )
  }

  fun saveLang(country: String) {
    preference.edit().putString(ConstantsUtil.SH_LANGUAGE, country).apply()
  }

  fun getLang(): String? {
    return preference.getString(ConstantsUtil.SH_LANGUAGE, null)
  }

  fun saveConfig(config: Config) {
    val gson = Gson()
    val jsonStr = gson.toJson(config)
    preference.edit().putString(ConstantsUtil.SH_CONFIG, jsonStr).apply()
  }

  fun getConfig(): Config? {
    val gson = Gson()
    return gson.fromJson(
      preference.getString(ConstantsUtil.SH_CONFIG, null),
      Config::class.java
    )
  }

  fun saveVouchers(data: VoucherMainResponse) {
    val gson = Gson()
    val jsonStr = gson.toJson(data)
    preference.edit().putString(ConstantsUtil.SH_VOUCHER, jsonStr).apply()
  }

  fun getVouchers(): VoucherMainResponse? {
    val gson = Gson()
    return gson.fromJson(
      preference.getString(ConstantsUtil.SH_VOUCHER, null),
      VoucherMainResponse::class.java
    )
  }

  fun saveHomeData(data: HomeMainModel) {
    val gson = Gson()
    val jsonStr = gson.toJson(data)
    preference.edit().putString(ConstantsUtil.SH_HOME, jsonStr).apply()
  }

  fun getHomeData(): HomeMainModel? {
    val gson = Gson()
    return gson.fromJson(
      preference.getString(ConstantsUtil.SH_HOME, null),
      HomeMainModel::class.java
    )
  }

  fun saveCategoryRoot(data: RootCategoryMainModel) {
    val gson = Gson()
    val jsonStr = gson.toJson(data)
    preference.edit().putString(ConstantsUtil.SH_ROOT_CAT, jsonStr).apply()
  }

  fun getCategoryRoot(): RootCategoryMainModel? {
    val gson = Gson()
    return gson.fromJson(
      preference.getString(ConstantsUtil.SH_ROOT_CAT, null),
      RootCategoryMainModel::class.java
    )
  }

  fun saveLottieURL(country: String) {
    preference.edit().putString(ConstantsUtil.SH_LOTTIE_URL, country).apply()
  }

  fun getLottieUrl(): String? {
    return preference.getString(ConstantsUtil.SH_LOTTIE_URL, "")
  }

  fun saveCurrentFileToken(country: String) {
    preference.edit().putString(ConstantsUtil.SH_LOTTIE_FILE_TOKE, country).apply()
  }

  fun getCurrentFileToken(): String? {
    return preference.getString(ConstantsUtil.SH_LOTTIE_FILE_TOKE, "")
  }


  fun saveAskPermissionOpenDateTime(dateTime: String?) {
    preference.edit().putString(ConstantsUtil.SH_ASK_PERMISSION_DATE_TIME, dateTime).apply()
  }

  fun getAskPermissionDateTime(): String? {
    return preference.getString(ConstantsUtil.SH_ASK_PERMISSION_DATE_TIME, "")
  }


}