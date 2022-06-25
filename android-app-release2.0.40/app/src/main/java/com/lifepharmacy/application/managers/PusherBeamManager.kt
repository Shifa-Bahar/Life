package com.lifepharmacy.application.managers

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.messaging.RemoteMessage
import com.lifepharmacy.application.Constants
import com.lifepharmacy.application.utils.PushNavigation
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Logger
import com.pusher.pushnotifications.BeamsCallback
import com.pusher.pushnotifications.PushNotificationReceivedListener
import com.pusher.pushnotifications.PushNotifications
import com.pusher.pushnotifications.PusherCallbackError
import com.pusher.pushnotifications.auth.AuthData
import com.pusher.pushnotifications.auth.AuthDataGetter
import com.pusher.pushnotifications.auth.BeamsTokenProvider
import org.json.JSONObject
import javax.inject.Inject

class PusherBeamManager @Inject constructor(
  var context: Context,
  var persistenceManager: PersistenceManager
) {
  fun initBeam() {
    PushNotifications.start(context, ConstantsUtil.PUSHER_BEAM_KEY)
    PushNotifications.addDeviceInterest("default")
  }

  fun setUser() {
    val tokenProvider = BeamsTokenProvider(
      "${Constants.BASE_URL}api/pusher/beams-auth",
      object : AuthDataGetter {
        override fun getAuthData(): AuthData {
          val headers: HashMap<String, String> = HashMap()
          headers["Authorization"] = ConstantsUtil.BEARER + persistenceManager.getToken()!!
          val query: HashMap<String, String> = HashMap()
          query["user_id"] = persistenceManager.getLoggedInUser()?.id.toString() ?: ""
          return AuthData(
            headers = headers,
            queryParams = query
          )
        }
      }
    )
    try {
      PushNotifications.setUserId(
        persistenceManager.getLoggedInUser()?.id.toString(),
        tokenProvider,
        object : BeamsCallback<Void?, PusherCallbackError?> {

          override fun onFailure(error: PusherCallbackError?) {
            Logger.i("PusherBeams", "Pusher Beams authentication failed: " + error?.message)
          }

          override fun onSuccess(vararg values: Void?) {
            Logger.i("PusherBeams", "Successfully authenticated with Pusher Beams")
          }
        })
    } catch (e: Exception) {
      e.printStackTrace()
    }

  }


}