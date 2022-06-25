package com.lifepharmacy.application.ui.dashboard.adapter

import com.lifepharmacy.application.model.home.SectionData

/**
 * Created by Zahid Ali
 */
interface ClickHomeSubItem {
    fun onClickHomeSubItem(title:String?,id:String?,type:String?,sectionData: SectionData?)
}