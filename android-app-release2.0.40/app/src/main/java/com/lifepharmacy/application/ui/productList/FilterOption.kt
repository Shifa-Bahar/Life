package com.lifepharmacy.application.ui.productList

import com.lifepharmacy.application.model.filters.FilterModel

/**
 * Created by Zahid Ali
 */
interface FilterOption {
    fun onItemClick(
        filterModel: FilterModel?,
        position: Int
    )
    fun gridSelectionValue(isSelected:Boolean)
    fun onClickFilters()
}