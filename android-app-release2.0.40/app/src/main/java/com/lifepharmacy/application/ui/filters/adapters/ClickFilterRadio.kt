package com.lifepharmacy.application.ui.filters.adapters

import com.lifepharmacy.application.model.filters.FilterModel

/**
 * Created by Zahid Ali
 */
interface ClickFilterRadio {
    fun onClickRadio(filter:FilterModel, type:String,position:Int,parentPosition:Int)
}