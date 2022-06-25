package com.lifepharmacy.application.ui.address.adapters

import com.lifepharmacy.application.model.address.AddressModel

/**
 * Created by Zahid Ali
 */
interface ClickItemAddress {
    fun onClickAddress(address:AddressModel,position: Int?)
    fun onClickDeleteAddress(address:AddressModel,position: Int?)
    fun onClickEditAddress(address:AddressModel,position: Int?)
}