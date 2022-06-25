package com.lifepharmacy.application.ui.pcr.adapters

import com.lifepharmacy.application.model.category.CategoryMainModel
import com.lifepharmacy.application.model.category.RootCategory
import com.lifepharmacy.application.model.pcr.pcrdatetime.Booking_slots

/**
 * Created by Zahid Ali
 */
interface ClickPcrDateTime {
  fun onClickPcrDateTime(position: Int, rootCategory: Booking_slots)
}