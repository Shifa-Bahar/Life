package com.lifepharmacy.application.network

import android.content.Context
import com.lifepharmacy.application.managers.PersistenceManager
import com.lifepharmacy.application.managers.StorageManagers
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

class SearchAuthInterceptor @Inject constructor(
  private val storageManagers: StorageManagers
) : Interceptor {
  @Throws(IOException::class)
  override fun intercept(chain: Chain): Response {
    val headers = Builder()
//    Logger.d("Authorization", storageManagers.config.searchAuthKey ?: "")
//    headers.add("build", com.lifepharmacy.application.BuildConfig.VERSION_CODE.toString())
//    headers.add("version", com.lifepharmacy.application.BuildConfig.VERSION_NAME)
    headers.add("Authorization", storageManagers.config.searchAuthKey ?: "")
//    headers.add("source", "android")
//    headers.add("Content-Type", "application/json")
//    headers.add("Accept", "application/json")
//    headers.add("Connection", "close")
    var request = chain.request()
    request = request.newBuilder()
      .headers(headers.build())
      .build()

    return chain.proceed(request)
  }
}
