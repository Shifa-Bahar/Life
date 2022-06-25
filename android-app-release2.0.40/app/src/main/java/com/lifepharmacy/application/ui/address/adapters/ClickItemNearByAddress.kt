package com.lifepharmacy.application.ui.address.adapters

import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.googleplaces.Result

/**
 * Created by Zahid Ali
 */
interface ClickItemNearByAddress {
  fun onClickNearBy(address: Result, position: Int?)
}