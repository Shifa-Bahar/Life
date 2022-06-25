
package com.lifepharmacy.application.ui.orders.adapters

import com.lifepharmacy.application.model.orders.PrescriptionOrder

/**
 * Created by Zahid Ali
 */
interface ClickPrescriptionOrderItem {
    fun onClickPrescriptionItem(orderPrescription:PrescriptionOrder,position:Int)
}