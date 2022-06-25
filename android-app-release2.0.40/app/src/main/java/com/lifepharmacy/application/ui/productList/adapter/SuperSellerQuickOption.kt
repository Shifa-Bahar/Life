package com.lifepharmacy.application.ui.productList.adapter

import com.lifepharmacy.application.model.category.Section

/**
 * Created by Zahid Ali
 */
interface SuperSellerQuickOption {
  fun onClickQuickSelection(section: Section?, position: Int?)
  fun onClickSubQuickSelection(section: Section?, position: Int?)
}