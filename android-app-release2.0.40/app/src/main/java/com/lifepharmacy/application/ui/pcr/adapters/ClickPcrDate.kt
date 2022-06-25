package com.lifepharmacy.application.ui.pcr.adapters


import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.pcr.pcrdatetime.BookingData
import com.lifepharmacy.application.model.pcr.pcrdatetime.Booking_slots
import com.lifepharmacy.application.model.pcr.pcrdatetime.Slots


/**
 * Created by Zahid Ali
 */
interface ClickPcrDate {
  fun onClickPcrDateTime(position: Int, rootCategory: Booking_slots)
  fun onClickPcrTime(position: Int, rootCategory: Slots)
  fun onClickPcrAddress(position: Int, rootCategory: AddressModel)
  fun onClickAddNew()
  fun onProductClicked(productDetails: BookingData, position: Int)
  fun onPcrProductClicked(productDetails: BookingData, position: Int)
  fun onClickAddProduct(productDetails: BookingData, position: Int)
  fun onClickMinus(productDetails: BookingData, position: Int)
  fun onClickPlus(productDetails: BookingData, position: Int)
  fun onClickWishList(productDetails: BookingData, position: Int)
  fun onClickNotify(productDetails: BookingData, position: Int)
  fun onClickOrderOutOfStock(productDetails: BookingData, position: Int)
  fun onClickcrPayment()
}