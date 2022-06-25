package com.lifepharmacy.application.ui.search.adapters

import com.lifepharmacy.application.model.search.agolia.Hits

/**
 * Created by Zahid Ali
 */
interface ClickTrendsSearchItem {
  fun onClickTrend(string: Hits)
}