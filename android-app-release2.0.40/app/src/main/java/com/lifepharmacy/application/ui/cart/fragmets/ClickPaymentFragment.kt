package com.lifepharmacy.application.ui.cart.fragmets

import com.lifepharmacy.application.model.cart.DeliveryInstructions
import com.lifepharmacy.application.model.pcr.pcrdatetime.Slots

/**
 * Created by Shifa
 */
interface ClickPaymentFragment {
  fun onClickProceed()
  fun onClickPaymentpage()
  fun onClickGotoOrders()
  fun onClickContinueShopping()
  fun onClickInstantDetails()
  fun onClickExpressDetails()
  fun onClickGiftDetails()
  fun onClickDeliveryInstant()
  fun onClickDeliveryExpress()
  fun onClickLater()
  fun onClickDeliveryIns(position: Int, delivertIns: DeliveryInstructions,add:Boolean)
}