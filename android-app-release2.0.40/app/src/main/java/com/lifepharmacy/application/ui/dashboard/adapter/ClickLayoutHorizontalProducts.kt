package com.lifepharmacy.application.ui.dashboard.adapter

import com.lifepharmacy.application.model.home.HomeResponseItem

/**
 * Created by Zahid Ali
 */
interface ClickLayoutHorizontalProducts {
  fun onClickViewAll(id: String, title: String, type: String)
  fun onLoadSectionItems(homeResponseItem: HomeResponseItem)
}