package com.lifepharmacy.application.utils


import android.content.Context
import android.net.ConnectivityManager
import com.lifepharmacy.application.R
import com.lifepharmacy.application.managers.PersistenceManager
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Logger
import okhttp3.Headers
import javax.inject.Inject


class NetworkUtils @Inject constructor(
  val context: Context,
  private val persistenceManager: PersistenceManager
) {

  val isConnectedToInternet: Boolean
    get() {
      var connected = false

      val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      val networkInfo = connectivityManager.activeNetworkInfo
      connected =
        networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
      return connected
    }
  val networkErrorMessage = context.getString(R.string.network_error)

  fun getHeadersInMap(): Map<String, String> {
    val headers = HashMap<String, String>()
    persistenceManager.getCountry().let {
      headers["country"] = it
      headers["language"] = "en"
      persistenceManager.getLat()?.let { it1 ->
        headers["latitude"] = it1
      }
      persistenceManager.getLong()?.let { it1 ->
        headers["latitude"] = it1
      }
      persistenceManager.getFCMToken()?.let { it1 ->
        headers["device_id"] = it1
      }
      if (persistenceManager.getToken() != null) {
        headers["Authorization"] = ConstantsUtil.BEARER + persistenceManager.getToken()!!
      }
    }
    headers["Content-Type"] = "application/json"
    headers["Accept"] = "application/json"
    headers["Connection"] = "close"
    return headers
  }

  fun getHeadersForPDFInMap(): Map<String, String> {
    val headers = HashMap<String, String>()
    persistenceManager.getCountry().let {
      headers["country"] = it
      headers["language"] = "en"
      persistenceManager.getLat()?.let { it1 ->
        headers["latitude"] = it1
      }
      persistenceManager.getLong()?.let { it1 ->
        headers["latitude"] = it1
      }
      persistenceManager.getFCMToken()?.let { it1 ->
        headers["device_id"] = it1
      }
      if (persistenceManager.getToken() != null) {
        headers["Authorization"] = ConstantsUtil.BEARER + persistenceManager.getToken()!!
      }
    }
    headers["Content-Type"] = "application/pdf"
    headers["Accept"] = "application/pdf"
    headers["Connection"] = "close"
    return headers
  }

  companion object {

  }


}