
package com.lifepharmacy.application.ui.search.adapters

import com.lifepharmacy.application.model.search.SearchCategory

/**
 * Created by Zahid Ali
 */
interface ClickSearchCategoryItem {
    fun onClickSearchCategory(item: SearchCategory,term:String)
}