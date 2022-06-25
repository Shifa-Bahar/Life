package com.lifepharmacy.application.ui.pcr.fragments


import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentPcrDateBinding
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.PcrDateScreenOpen
import com.lifepharmacy.application.managers.analytics.PcrListScreenOpen
import com.lifepharmacy.application.model.address.AddressMainModel
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.pcr.pcrdatetime.BookingData
import com.lifepharmacy.application.model.pcr.pcrdatetime.Booking_slots
import com.lifepharmacy.application.model.pcr.pcrdatetime.Slots
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressSelectionActivity
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.address.adapters.AddressAdapter
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.pcr.adapters.ClickPcrDate
import com.lifepharmacy.application.ui.pcr.viewmodel.PcrListingViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.rewards.adapters.PcrAddressAdapter
import com.lifepharmacy.application.ui.rewards.adapters.PcrDateAdapter
import com.lifepharmacy.application.ui.rewards.adapters.PcrTimeAdapter
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.DateTimeUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.RecyclerPagingListener
import com.lifepharmacy.application.utils.universal.RecyclerViewPagingUtil
import com.livechatinc.inappchat.ChatWindowView
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class PcrDateFragment : BaseFragment<FragmentPcrDateBinding>(),
  ClickPcrAddressFragment, ClickTool, ClickPcrDate, RecyclerPagingListener {
  companion object {
    fun getPcrListingBundle(
      id: String?
    ): Bundle {
      val bundle = Bundle()
      id?.let {
        bundle.putString("id", id)
      }
      return bundle
    }
  }

  private val viewModel: PcrListingViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  private val addressViewModel: AddressViewModel by activityViewModels()
  private val viewModelAddress: AddressViewModel by activityViewModels()

  //  private val profileRepository: ProfileRepository
  private var layoutManager: GridLayoutManager? = null
  private lateinit var PcrDateAdapter: PcrDateAdapter
  private lateinit var PcrTimeAdapter: PcrTimeAdapter
  private lateinit var AddressAdapter: AddressAdapter
  private lateinit var PcrAddressAdapter: PcrAddressAdapter
  private lateinit var recyclerViewPagingUtil: RecyclerViewPagingUtil
  var savedAddressList: AddressMainModel? = null
  var savedAddressList1: ArrayList<AddressModel>? = ArrayList()
  var addresslist: ArrayList<AddressModel>? = ArrayList()
  var addresslistnew: ArrayList<AddressModel>? = ArrayList()
  var savedAddressList2: ArrayList<AddressModel>? = ArrayList()
  var savedAddressListnew: List<AddressModel> = listOf<AddressModel>()
  var test: ArrayList<AddressModel>? = ArrayList()
  private var fullScreenChatWindow: ChatWindowView? = null
  var firstTimeLoading = false
  var arrayListslots: ArrayList<Slots>? = ArrayList()
  var rootSelected: Booking_slots? = null
  var rootSelectedTime: Slots? = null
  var rootSelectedAddress: AddressModel? = null
  var strdate: String = ""
  var strtime: String = "08:00 - 09:00"
  var dateofWeek: String = ""
  var dayOfWeek: String = ""
  var addressSelection = AddressSelection.LATEST_ADDRESS
  private val requestLocationPermissions =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
      var result = true
      granted.entries.forEach {
        if (!it.value) {
          result = false
          return@forEach
        }
      }
      if (result) {
        locationPermissionGranted()
      } else {
        AlertManager.permissionRequestPopup(requireActivity())
      }

    }
  private val addressContract =
    registerForActivityResult(AddressSelectionActivity.Contract()) { result ->
      result?.address?.let {
        addressViewModel.deliveredAddressMut.value = it
        addressViewModel.addressSelection = AddressSelection.NON
      }
    }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      findNavController().navigateUp()
    }

  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.PcrDateScreenOpen()
    arguments?.let {
      viewModel.colid = it.getString("id")
    }

    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      firstTimeLoading = true
      initUI()
    }
    return mView
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
  }

  private fun initUI() {

//    savedAddressList1 = appManager.persistenceManager.getAddressList()?.addresses
//    Log.e(TAG, "initUI: $savedAddressList1")

    binding.viewModel = viewModel
    binding.profileViewModel = profileViewModel
    binding.lifecycleOwner = this
    binding.click = this
    binding.tollBar.click = this
    binding.tollBar.tvToolbarTitle.text = getString(R.string.covidrtpcr)
    binding.btnCheckOut.text = "next"
    binding.tvAmount.text = appManager.persistenceManager.totalwithoutadditonal.currencyFormat()
    binding.tvSubto.text =
      appManager.persistenceManager.subtotal.plus(appManager.persistenceManager.vattotal)
        .currencyFormat()
    binding.tvSubto.paintFlags =
      binding.tvSubto.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG)



    initDateListRV()
    initTimeListRV()
    initAddressListRV()
    observers()
//    observersAddresssNew()
    observersAddresssApi()

  }

  private fun locationPermissionGranted() {
    try {
      Log.e(TAG, "locationPermissionGranted: shifa")
      findNavController().navigate(R.id.addNewAddressDialog)
    } catch (e: Exception) {
    }
  }

  private fun initDateListRV() {
    PcrDateAdapter = PcrDateAdapter(requireActivity(), this, viewModel.appManager)
    layoutManager = GridLayoutManager(requireContext(), 1)

    binding.rvDate.adapter = PcrDateAdapter
    recyclerViewPagingUtil = RecyclerViewPagingUtil(binding.rvDate, layoutManager!!, this)

    binding.rvDate.addOnScrollListener(recyclerViewPagingUtil)
    binding.rvDate.post { // Call smooth scroll
      binding.rvDate.scrollToPosition(0)
    }
  }


  private fun initAddressListRV() {
//    savedAddressList1 = appManager.persistenceManager.getAddressList()?.addresses
    PcrAddressAdapter = PcrAddressAdapter(requireActivity(), this, viewModel.appManager)
    layoutManager = GridLayoutManager(requireContext(), 1)

    binding.rvAddress.adapter = PcrAddressAdapter
    recyclerViewPagingUtil = RecyclerViewPagingUtil(binding.rvAddress, layoutManager!!, this)

    binding.rvAddress.addOnScrollListener(recyclerViewPagingUtil)
    binding.rvAddress.post { // Call smooth scroll
      binding.rvAddress.scrollToPosition(0)
    }
  }


  private fun initTimeListRV() {
    PcrTimeAdapter = PcrTimeAdapter(requireActivity(), this, viewModel.appManager)
    layoutManager = GridLayoutManager(requireContext(), 1)

    binding.rvTime.adapter = PcrTimeAdapter
    recyclerViewPagingUtil = RecyclerViewPagingUtil(binding.rvTime, layoutManager!!, this)

    binding.rvTime.addOnScrollListener(recyclerViewPagingUtil)
    binding.rvTime.post { // Call smooth scroll
      binding.rvTime.scrollToPosition(0)
    }
  }

  private fun observersAddresssNew() {
    var addres = appManager.persistenceManager.addressesglobal
    Log.e(TAG, "observersAddresssNew: address global$addres")

    if (!appManager.persistenceManager.addressesglobal.isNullOrEmpty()) {
      Log.e(TAG, "observersAddresssNew: if")
      appManager.persistenceManager.addressesglobal.let { addresslistdata ->
        binding.rvAddress.adapter = PcrAddressAdapter
        addresslist = addresslistdata

        if (addresslist != null && addresslist!!.size > 0) {
          if (firstTimeLoading) {
            //              first time setting index 0 address
            appManager.persistenceManager.addressid = addresslistdata!![0].id
            appManager.persistenceManager.streetaddress =
              addresslistdata!![0].streetAddress.toString()
            appManager.persistenceManager.areaaddress =
              addresslistdata!![0].area.toString()
            appManager.persistenceManager.cityaddress =
              addresslistdata!![0].city.toString()
            appManager.persistenceManager.phoneaddress =
              addresslistdata!![0].phone.toString()
          }
          PcrAddressAdapter.setDataChanged(addresslist)

        } else {
          binding.rvAddress.visibility = android.view.View.GONE
        }
      }
    } else {
      Log.e(TAG, "observersAddresssNew: hiii else")
      observersAddresssApi()
    }
  }

  private fun observers() {
    if (!appManager.persistenceManager.bookingslots.isNullOrEmpty()) {
      appManager.persistenceManager.bookingslots.let {
        it?.let { bookingslot ->
//          if (firstTimeLoading) {
          bookingslot.let { data ->
            binding.rvDate.adapter = PcrDateAdapter
            PcrDateAdapter.setDataChanged(bookingslot)
            binding.rvTime.adapter = PcrTimeAdapter
            PcrTimeAdapter.setDataChanged(bookingslot[0].slots)

            if (!bookingslot!![0].slots[0].additional_charges_amount.equals(null)) {
              var addcharge = bookingslot!![0].slots[0].additional_charges_amount
              appManager.persistenceManager.additional_charges_amount = addcharge
            }
            var strdd: String = bookingslot[0].date.toString()
            var strtt: String = bookingslot[0].slots[0].time.toString()

            dateofWeek = DateTimeUtil.getDateStringDateFromStringWithoutTimeZone(strdd).toString()
            dayOfWeek = DateTimeUtil.getDayStringDateFromStringWithoutTimeZone(strdd).toString()

            binding.tvCompleteDate.text = "$dayOfWeek | $dateofWeek |"
            binding.tvCompleteTime.text = "$strtt"
            appManager.persistenceManager.completedate = "$dayOfWeek | $dateofWeek|"

            appManager.persistenceManager.datepcrglobal = "$strdd"
            appManager.persistenceManager.timepcrglobal = "$strtt"
          }
          if (bookingslot.isNotEmpty()) {
            if (arrayListslots != null) {
              var getFiltersCategory =
                bookingslot?.firstOrNull { rootCategory -> rootCategory.slots == arrayListslots }
              getFiltersCategory?.let { rootSelected ->
                val position = bookingslot.indexOf(rootSelected)
                PcrDateAdapter.setItemSelected(position)
                this.rootSelected = rootSelected
                PcrDateAdapter.setItemSelected(position)
                subCategoriesObserver(rootSelected)
              }
            } else {
              rootSelected = bookingslot[0]
              subCategoriesObserver(bookingslot[0])
            }

          }
//          }
        }
      }
    } else {
      viewModel.getPcrDate().observe(viewLifecycleOwner, Observer {
        it?.let { categoryList ->
          var appoinmentinfo = it.data?.data?.appointment_info
          appManager.persistenceManager.appointmentinfo = appoinmentinfo.toString()
          if (firstTimeLoading) {
            it.data?.data?.booking_slots?.let { data ->
              binding.rvDate.adapter = PcrDateAdapter
              PcrDateAdapter.setDataChanged(it.data?.data?.booking_slots)
              binding.rvTime.adapter = PcrTimeAdapter
              PcrTimeAdapter.setDataChanged(it.data?.data?.booking_slots!![0].slots)


              var strdd: String = it.data?.data?.booking_slots!![0].date.toString()
              var strtt: String = it.data?.data?.booking_slots!![0].slots[0].time.toString()
              var addcharge = it.data?.data?.booking_slots!![0].slots[0].additional_charges_amount
              appManager.persistenceManager.additional_charges_amount = addcharge

              dateofWeek = DateTimeUtil.getDateStringDateFromStringWithoutTimeZone(strdd).toString()
              dayOfWeek = DateTimeUtil.getDayStringDateFromStringWithoutTimeZone(strdd).toString()

              binding.tvCompleteDate.text = "$dayOfWeek | $dateofWeek |"
              binding.tvCompleteTime.text = "$strtt"
              appManager.persistenceManager.completedate = "$dayOfWeek | $dateofWeek|"
//            appManager.persistenceManager.completetime = "$strtt"
              appManager.persistenceManager.datepcrglobal = "$strdd"
              appManager.persistenceManager.timepcrglobal = "$strtt"
            }
            if (categoryList.data?.data?.booking_slots?.isNotEmpty() == true) {
              if (arrayListslots != null) {
                var getFiltersCategory =
                  categoryList.data?.data?.booking_slots?.firstOrNull { rootCategory -> rootCategory.slots == arrayListslots }
                getFiltersCategory?.let { rootSelected ->
                  val position = categoryList.data?.data?.booking_slots!!.indexOf(rootSelected)
                  PcrDateAdapter.setItemSelected(position)
                  this.rootSelected = rootSelected
                  PcrDateAdapter.setItemSelected(position)
                  subCategoriesObserver(rootSelected)
                }
              } else {
                rootSelected = categoryList.data?.data?.booking_slots!![0]
                subCategoriesObserver(categoryList.data?.data?.booking_slots!![0])
              }

            }
          }
        }

      })
    }
  }

  private fun subCategoriesObserver(id: Booking_slots?) {
    Log.e(TAG, "subCategoriesObserver: timepart")
    val timeArray = id?.slots
    if (timeArray != null) {
      PcrTimeAdapter.setDataChanged(timeArray)
    }

  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_pcr_date
  }


  override fun onClickBack() {
    handleCustomBackPress()

  }


  override fun onClickUploadImage() {

  }


  override fun onClickProceed() {
    if (viewModelAddress.deliveredAddressMut.value == null) {
      viewModelAddress.isSelecting.set(true)
      findNavController().navigate(R.id.nav_address)
    } else {
      uploadPrescription()
    }


  }

  override fun onClickLogin() {
    findNavController().navigate(R.id.nav_login_sheet)
  }

  override fun onClickcrPayment() {
    Log.e(TAG, "onClickcrPayment: ")
//    findNavController().navigate(R.id.pcrPaymentFragment)
    findNavController().navigate(
      R.id.pcrPaymentFragment, PcrPaymentFragment.getPcrListingBundle(viewModel.colid)
    )
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

  }


  private fun uploadFile(file: File) {


  }

  private fun observersAddresssApi() {
    Log.e(TAG, "observersAddresssApi: appi")
    val gson = Gson()
    viewModel.getPcrAddress().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.data?.addresses?.let { data ->
              binding.rvAddress.adapter = PcrAddressAdapter
              addresslist = it.data?.data?.addresses
              if (addresslist != null && addresslist!!.size > 0) {
                var n = addresslist!!.size

                if (firstTimeLoading) {
                  //              first time setting index 0 address
                  appManager.persistenceManager.addressid = it.data?.data?.addresses!![0].id
                  appManager.persistenceManager.streetaddress =
                    it.data?.data?.addresses!![0].streetAddress.toString()
                  appManager.persistenceManager.areaaddress =
                    it.data?.data?.addresses!![0].area.toString()
                  appManager.persistenceManager.cityaddress =
                    it.data?.data?.addresses!![0].city.toString()
                  appManager.persistenceManager.phoneaddress =
                    it.data?.data?.addresses!![0].phone.toString()
                }

                PcrAddressAdapter.setDataChanged(addresslist)
              } else {
                binding.rvAddress.visibility = View.GONE
              }
            }
          }
          Result.Status.ERROR -> {
            hideLoading()
            it.message?.let { it1 ->
              AlertManager.showErrorMessage(
                requireActivity(),
                it1
              )
            }
          }
          Result.Status.LOADING -> {
            recyclerViewPagingUtil.isLoading = true
          }
        }
      }
    })
  }

  private fun uploadPrescription() {

  }

  private fun handleCustomBackPress() {
//    if (viewModel.isNotClaimInsurance.get() == true || viewModel.isClaimInsurance.get() == true) {
//      viewModel.isNotClaimInsurance.set(false)
//      viewModel.isClaimInsurance.set(false)
//    } else {
    findNavController().navigateUp()
//      Utils.exitApp(requireActivity())
//    }
  }


  private fun openImageActionSheet(url: String) {

  }

  override fun onClickPcrDateTime(position: Int, rootCategory: Booking_slots) {
    appManager.persistenceManager.datepcrglobal = rootCategory.date
    strdate = rootCategory.date.toString()
    dateofWeek = DateTimeUtil.getDateStringDateFromStringWithoutTimeZone(strdate).toString()
    dayOfWeek = DateTimeUtil.getDayStringDateFromStringWithoutTimeZone(strdate).toString()

    binding.tvCompleteDate.text = "$dayOfWeek | $dateofWeek|"
    appManager.persistenceManager.completedate = "$dayOfWeek | $dateofWeek|"
    rootSelected = rootCategory
    PcrDateAdapter.setItemSelected(position)
    subCategoriesObserver(rootCategory)

//    binding.rvTime.isSelected = false
    var timeslotindexfirst = rootCategory.slots[0].time
    if (!timeslotindexfirst.isNullOrEmpty()) {
      appManager.persistenceManager.timepcrglobal = rootCategory.slots[0].time
      appManager.persistenceManager.additional_charges_amount =
        rootCategory.slots[0].additional_charges_amount
      binding.tvCompleteTime.text = "$timeslotindexfirst"
//      binding.rvTime.isSelected = true
      PcrTimeAdapter.setItemSelected(0)
      binding.rvTime.scrollToPosition(0)
      Log.e(TAG, "onClickPcrDateTime: timeslotindexfirst$timeslotindexfirst")
//      appManager.persistenceManager.completetime = "$timeslotindexfirst"
    }

  }

  override fun onClickPcrTime(position: Int, rootCategory: Slots) {
    appManager.persistenceManager.timepcrglobal = rootCategory.time
    strtime = rootCategory.time.toString()

    binding.tvCompleteTime.text = "$strtime"
//    appManager.persistenceManager.completetime = "$strtime"
    appManager.persistenceManager.timepcrglobal = "$strtime"

//    var addcharge = rootCategory.additional_charges_amount
//    appManager.persistenceManager.additional_charges_amount = addcharge

    rootSelectedTime = rootCategory
    PcrTimeAdapter.setItemSelected(position)

  }

  override fun onClickPcrAddress(position: Int, rootCategory: AddressModel) {
    appManager.persistenceManager.streetaddress = rootCategory.streetAddress.toString()
    appManager.persistenceManager.areaaddress = rootCategory.area.toString()
    appManager.persistenceManager.cityaddress = rootCategory.city.toString()
    viewModelAddress.deliveredAddressMut.value = rootCategory
    var p = rootCategory.phone
    Log.e(TAG, "onClickPcrAddress: $p")
    appManager.persistenceManager.phoneaddress = "$p"
    appManager.persistenceManager.addressid = rootCategory.id
    rootSelectedAddress = rootCategory
    PcrAddressAdapter.setItemSelected(position)
  }


  override fun onClickAddNew() {
    addressViewModel.isEdit = false
    addressViewModel.editAddress = AddressModel()
    addressViewModel.clearFields()
    requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
//    initUI()
    findNavController().navigate(R.id.pcrdateFragment)
//    initAddressListRV()
  }


//  private fun proceedButtonStatus() {
//    viewModel.isProceedEnable.value = viewModel.filesListLive.value.isNullOrEmpty()
//  }


  override fun onProductClicked(productDetails: BookingData, position: Int) {

  }

  override fun onPcrProductClicked(productDetails: BookingData, position: Int) {

  }

  override fun onClickAddProduct(productDetails: BookingData, position: Int) {

  }

  override fun onClickMinus(productDetails: BookingData, position: Int) {

  }

  override fun onClickPlus(productDetails: BookingData, position: Int) {

  }

  override fun onClickWishList(productDetails: BookingData, position: Int) {

  }

  override fun onClickNotify(productDetails: BookingData, position: Int) {

  }


  override fun onClickOrderOutOfStock(productDetails: BookingData, position: Int) {

  }

  override fun onNextPage(skip: Int, take: Int) {
  }

  override fun permissionGranted(requestCode: Int) {

  }
//  private fun docSelected(doc: DocumentModel) {
//    docsViewModel.selectedDoc.value = doc
//  }

  override fun onResume() {
    super.onResume()
//    observersAddresssApi()
    Log.e(TAG, "onResume:")
    binding.tvAmount.text = appManager.persistenceManager.totalwithoutadditonal.currencyFormat()
    PcrAddressAdapter.notifyDataSetChanged()
  }
}



