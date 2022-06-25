package com.lifepharmacy.application.ui.filters.adapters

import com.lifepharmacy.application.model.filters.FilterTypeModel

/**
 * Created by Zahid Ali
 */
interface ClickFilterType {
    fun onClickFilterType(filterType:FilterTypeModel, position:Int)
}