package com.lifepharmacy.application.managers.analytics

import android.content.Context
import com.algolia.instantsearch.insights.Insights
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.lifepharmacy.application.managers.PersistenceManager
import com.lifepharmacy.application.managers.PusherBeamManager
import com.lifepharmacy.application.managers.StorageManagers
import com.lifepharmacy.application.utils.AlgoliaInsightsUtil
import com.lifepharmacy.application.utils.NetworkUtils
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.rudderstack.android.sdk.core.RudderClient
import com.rudderstack.android.sdk.core.RudderConfig
//import org.json.JSONObject
//import org.json.simple.parser.JSONParser
import javax.inject.Inject


class AnalyticsManagers
@Inject constructor(
  var context: Context,
  var persistenceManager: PersistenceManager,
  var networkUtils: NetworkUtils,
  var storageManagers: StorageManagers,
  val pusherBeamManager: PusherBeamManager
) {

  var rudderClient: RudderClient = RudderClient.getInstance(
    context,
    ConstantsUtil.RUDDER_STACK_KEY,
    RudderConfig.Builder()
      .withDataPlaneUrl(ConstantsUtil.RUDDER_STACK_URL)
      .withTrackLifecycleEvents(true)
      .withRecordScreenViews(false)
      .build()
  )


  init {
    FirebaseApp.initializeApp(context)
    AlgoliaInsightsUtil.setUpInsights(context = context)
    Insights.shared?.userToken = persistenceManager.getLoggedInUser()?.id.toString()
    persistenceManager.getLoggedInUser()?.let { setIdentify(it, rudderClient) }
  }


  val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

}