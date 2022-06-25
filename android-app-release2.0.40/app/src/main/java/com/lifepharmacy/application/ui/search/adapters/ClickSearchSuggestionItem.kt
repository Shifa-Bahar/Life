package com.lifepharmacy.application.ui.search.adapters

import com.lifepharmacy.application.model.search.SearchCategory
import com.lifepharmacy.application.model.search.agolia.Hits

/**
 * Created by Zahid Ali
 */
interface ClickSearchSuggestionItem {
  fun onClickSearchCategory(item: Hits)
  fun onClickProduct(item: Hits)
}