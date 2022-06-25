package com.lifepharmacy.application.utils

import android.content.Context
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.event.EventObjects
import com.lifepharmacy.application.utils.universal.ConstantsUtil

object AlgoliaInsightsUtil {


  fun setUpInsights(context: Context) {
    val configuration = Insights.Configuration(
      connectTimeoutInMilliseconds = 5000,
      readTimeoutInMilliseconds = 5000,

      )
    Insights.register(
      context,
      ConstantsUtil.ANGOLIA_ID,
      ConstantsUtil.ANGOLIA_KEY,
      ConstantsUtil.ANGOLIA_SUGUESSTION_INDEX,
      configuration
    )
  }

  fun clickAfterSearch(queryId: String) {
    Insights.shared?.clickedAfterSearch(
      eventName = "your_event_name",
      queryId = queryId,
      objectIDs = EventObjects.IDs("objectID1", "objectID2"),
      positions = listOf(17, 19)
    )
  }

  fun convertAfterSearch(queryId: String) {
    Insights.shared?.convertedAfterSearch(
      eventName = "your_event_name",
      queryId = queryId,
      EventObjects.IDs("objectID1", "objectID2")

    )
  }

}