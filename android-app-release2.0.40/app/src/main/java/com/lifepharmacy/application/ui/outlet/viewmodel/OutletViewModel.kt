package com.lifepharmacy.application.ui.outlet.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.enums.ProductDetailSelectedState
import com.lifepharmacy.application.enums.ScrollingState
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.ui.utils.AppScrollState
import com.lifepharmacy.application.managers.FiltersManager
import com.lifepharmacy.application.managers.OffersManagers
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.category.Section
import com.lifepharmacy.application.model.filters.FilterModel
import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.PlaceOrderRequest
import com.lifepharmacy.application.model.orders.outofstock.OutOfStockRequestBody
import com.lifepharmacy.application.model.outlet.Outletdata
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Address
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Appointment_details
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Order
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Payment_details
import com.lifepharmacy.application.model.pcr.pcrlist.Prices
import com.lifepharmacy.application.model.pcr.pcrlist.Products
import com.lifepharmacy.application.model.pcr.pcrrequestmodel.Items
import com.lifepharmacy.application.model.pcr.pcrrequestmodel.PlacePcrOrderRequest
import com.lifepharmacy.application.model.review.Product
import com.lifepharmacy.application.repository.PcrListingRepository
import com.lifepharmacy.application.repository.ProfileRepository
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.utils.CalculationUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Extensions.doubleDigitDouble
import com.lifepharmacy.application.utils.universal.Logger
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Zahid Ali
 */
class OutletViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: ProfileRepository,
  public val filtersManager: FiltersManager,
  val offersManagers: OffersManagers,
  application: Application, private val scrollState: AppScrollState
) : BaseViewModel(application) {
  var userObjectMut: Outletdata? = null
  var available: Boolean = false
  var titleString: String? = ""
//  var username: String? = ""
//  var date: String = ""
//  var qrdata: String = ""
var photo : String = ""
  var username : String = ""
  var date : String = ""
  var qrdata : String = ""
  var nomemberurl : String = ""
  var linkurl: String = ""

  fun getOutlet() =
    performNwOperation { repository.getOutlet() }


}