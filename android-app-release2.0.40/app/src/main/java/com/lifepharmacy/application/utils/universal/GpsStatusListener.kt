package com.lifepharmacy.application.utils.universal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import androidx.lifecycle.LiveData
import com.lifepharmacy.application.R

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Zahid Ali
 */
/**
 * Listens to Gps (location service) which is highly important for tracking to work and then
 * responds with appropriate state specified in {@link GpsStatus}
 */

@Singleton
class GpsStatusListener @Inject constructor(private val context: Context) : LiveData<GpsStatus>() {

  private val gpsSwitchStateReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = checkGpsAndReact()
  }

  override fun onInactive() = unregisterReceiver()

  override fun onActive() {
    registerReceiver()
    checkGpsAndReact()
  }

  private fun checkGpsAndReact() = if (isLocationEnabled()) {
    postValue(GpsStatus.Enabled())
  } else {
    postValue(GpsStatus.Disabled())
  }

  private fun isLocationEnabled() =
    context.getSystemService(LocationManager::class.java)!!
      .isProviderEnabled(LocationManager.GPS_PROVIDER)

  /**
   * Broadcast receiver to listen the Location button toggle state in Android.
   */
  private fun registerReceiver() = context.registerReceiver(
    gpsSwitchStateReceiver,
    IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
  )

  private fun unregisterReceiver() = context.unregisterReceiver(gpsSwitchStateReceiver)
}

sealed class GpsStatus {
  data class Enabled(val message: Int = R.string.gps_status_enabled) : GpsStatus()
  data class Disabled(val message: Int = R.string.gps_status_disabled) : GpsStatus()
}
