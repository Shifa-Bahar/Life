package com.lifepharmacy.application.ui.categories.adapters

import com.lifepharmacy.application.model.category.CategoryMainModel
import com.lifepharmacy.application.model.category.RootCategory

/**
 * Created by Zahid Ali
 */
interface ClickCategory {
  fun onClickCategory(position: Int, rootCategory: RootCategory)
}