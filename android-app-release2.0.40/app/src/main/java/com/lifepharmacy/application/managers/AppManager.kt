package com.lifepharmacy.application.managers

import android.content.Context
import com.lifepharmacy.application.managers.analytics.AnalyticsManagers
import com.lifepharmacy.application.ui.utils.AppScrollState
import com.lifepharmacy.application.ui.utils.LoadingState
import com.lifepharmacy.application.utils.NetworkUtils
import com.lifepharmacy.application.utils.universal.GoogleUtils

import javax.inject.Inject

class AppManager @Inject constructor(
  val application: Context,
  val persistenceManager: PersistenceManager,
  val googleUtils: GoogleUtils,
  val filtersManager: FiltersManager,
  val offersManagers: OffersManagers,
  val wishListManager: WishListManager,
  val loadingState: LoadingState,
  val scrollState: AppScrollState,
  val storageManagers: StorageManagers,
  val networkUtils: NetworkUtils,
  val pusherManager: PusherManager,
  val analyticsManagers: AnalyticsManagers,
  val eventlistmainManager: EventlistmainManager

) {
  //    var context: Context = application.applicationContext
  var mediaManager: MediaManager = MediaManager(application)
}