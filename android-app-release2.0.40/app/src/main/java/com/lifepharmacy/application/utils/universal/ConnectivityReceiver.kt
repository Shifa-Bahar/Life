package com.lifepharmacy.application.utils.universal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import com.lifepharmacy.application.model.ConnectionModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityReceiver @Inject constructor(private val context: Context) :
  LiveData<ConnectionModel?>()
{
  override fun onActive() {
    super.onActive()
    val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    context.registerReceiver(networkReceiver, filter)
  }

  override fun onInactive() {
    super.onInactive()
    context.unregisterReceiver(networkReceiver)
  }

  private val networkReceiver: BroadcastReceiver =
    object : BroadcastReceiver() {
      override fun onReceive(
        context: Context,
        intent: Intent
      ) {
        try {
          if (intent.extras != null) {
            val connectivityManager =
              context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            val isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
            if (isConnected) {
              when (activeNetwork!!.type) {
                ConnectivityManager.TYPE_WIFI -> postValue(
                  ConnectionModel(
                    0,
                    true
                  )
                )
                ConnectivityManager.TYPE_MOBILE -> postValue(
                  ConnectionModel(
                    1,
                    true
                  )
                )
                ConnectivityManager.TYPE_VPN -> postValue(
                  ConnectionModel(
                    2,
                    false
                  )
                )
              }
            } else {
              postValue(ConnectionModel(0, false))
            }
          }
        } catch (e: Exception) {
          Logger.e(
            "%s Catched Error! Not able to connect to Internet!",
            TAG
          )
        }
      }
    }

  companion object {
    private const val TAG = "ConnectivityReceiver"
  }

}