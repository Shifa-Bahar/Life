package com.lifepharmacy.application.ui.productList.adapter

import com.lifepharmacy.application.model.category.Section

/**
 * Created by Zahid Ali
 */
interface ClickSubQuickOption {
    fun onClickQuickSelection(section: Section?, position:Int?)
}