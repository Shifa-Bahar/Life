package com.lifepharmacy.application.network

import android.content.Context
import android.util.Log
import com.lifepharmacy.application.managers.PersistenceManager
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.utils.NetworkUtils
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Logger
import com.lifepharmacy.application.utils.universal.Utils
import com.onesignal.OneSignal
import okhttp3.Cache
import okhttp3.Headers.Builder
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import kotlin.jvm.Throws

class AuthInterceptor @Inject constructor(
  private val appContext: Context,
  private val persistenceManager: PersistenceManager,
  private val networkUtils: NetworkUtils,
) : Interceptor {
  @Throws(IOException::class)
  override fun intercept(chain: Chain): Response {

    val cacheSize = (58 * 1024 * 1024).toLong()
    val myCache = Cache(appContext.cacheDir, cacheSize)
    val headers = Builder()
    persistenceManager.getCountry().let {
      headers.add("country", it)
      Logger.d("country", it)
      headers.add("language", "en")

    }
    persistenceManager.getLat()?.let { it1 ->
      headers.add("latitude", it1)
      Logger.d("latitude", it1)
    }
    persistenceManager.getLong()?.let { it1 ->
      headers.add("longitude", it1)
      Logger.d("longitude", it1)
    }
    if (persistenceManager.isThereUUID()) {
      persistenceManager.getUUID()?.let { headers.add("uuid", it) }
    } else {
      persistenceManager.saveUUID(Utils.getRandomUUID())
      persistenceManager.getUUID()?.let { headers.add("uuid", it) }
    }

    persistenceManager.getCompaignId()?.let {
      headers.add("campaignId", it)
    }

//    persistenceManager.getFCMToken()?.let { it1 ->
//      if (it1.isNullOrEmpty()) {
//        else {
//          headers.add("device_id", "")
//        }
//      } else {
//        headers.add("device_id", it1)
//      }
//    }
    val device = OneSignal.getDeviceState()
    val userId = device?.userId
    if (userId != null) {
      headers.add("device_id", userId)
    }
    //      persistenceManager.getLatLong()?.let { it1 -> headers.add("userLocation", it1) }
    if (persistenceManager.getToken() != null) {
      headers.add("Authorization", ConstantsUtil.BEARER + persistenceManager.getToken()!!)
      Logger.d("Authorization", ConstantsUtil.BEARER + persistenceManager.getToken()!!)
    }
    headers.add("build", com.lifepharmacy.application.BuildConfig.VERSION_CODE.toString())
    headers.add("version", com.lifepharmacy.application.BuildConfig.VERSION_NAME)
    headers.add("source", "android")
//    if (networkUtils.isConnectedToInternet){
//      headers.add("Cache-Control", "public, max-age=" + 5)
//    }else{
//      headers.add("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
//
//    }
    headers.add("Content-Type", "application/json")
    headers.add("Accept", "application/json")
    headers.add("Connection", "close")
    var request = chain.request()
    request = request.newBuilder()
      .headers(headers.build())
      .build()

    return chain.proceed(request)
  }
}
