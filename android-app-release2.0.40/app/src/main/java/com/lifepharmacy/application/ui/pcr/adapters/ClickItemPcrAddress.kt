package com.lifepharmacy.application.ui.pcr.adapters

import com.lifepharmacy.application.model.address.AddressModel

/**
 * Created by Zahid Ali
 */
interface ClickItemPcrAddress {
    fun onClickAddress(address:AddressModel,position: Int?)
    fun onClickDeleteAddress(address:AddressModel,position: Int?)
    fun onClickEditAddress(address:AddressModel,position: Int?)
}