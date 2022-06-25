package com.lifepharmacy.application.ui.pcr.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentPcrPaymentBinding
import com.lifepharmacy.application.enums.AddressChanged
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.*
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.model.payment.Urls
import com.lifepharmacy.application.model.pcr.pcrlist.Products
import com.lifepharmacy.application.model.pcr.pcrrequestmodel.PlacePcrOrderRequest
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressSelectionActivity
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.address.dailog.ClickLayoutAddress
import com.lifepharmacy.application.ui.card.CardsAdapter
import com.lifepharmacy.application.ui.card.ClickCard
import com.lifepharmacy.application.ui.cart.fragmets.OrderSummaryBottomSheet
import com.lifepharmacy.application.ui.cart.fragmets.OrderWalletTopUpBottomSheet
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.outofstockorder.OutOfStockOrderViewModel
import com.lifepharmacy.application.ui.pages.fragment.PageFragment
import com.lifepharmacy.application.ui.payment.WebViewPaymentActivity
import com.lifepharmacy.application.ui.pcr.adapters.ClickPcrProduct
import com.lifepharmacy.application.ui.pcr.adapters.PcrProductPaymentAdapter
import com.lifepharmacy.application.ui.pcr.viewmodel.PcrListingViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.wallet.viewmodels.TopViewModel
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Logger
import com.lifepharmacy.application.utils.universal.RecyclerPagingListener
import com.lifepharmacy.application.utils.universal.RecyclerViewPagingUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_pcr_payment.*
import kotlinx.android.synthetic.main.item_card.*
import kotlinx.android.synthetic.main.item_card.view.*
import kotlinx.coroutines.*
import java.util.*


@AndroidEntryPoint
class PcrPaymentFragment : BaseFragment<FragmentPcrPaymentBinding>(),
  ClickCard, ClickLayoutAddress, ClickTool, ClickPcrProduct, ClickPcrBottomSheetFragment,
  RecyclerPagingListener {
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

  //  private val cartViewModel: CartViewModel by activityViewModels()
  private val viewModel: CartViewModel by activityViewModels()
  private val pcrviewModel: PcrListingViewModel by activityViewModels()
  private val addressViewModel: AddressViewModel by activityViewModels()
  private val walletVieModel: WalletViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  var cardarrayList: ArrayList<CardMainModel>? = ArrayList()
  private val topViewModel: TopViewModel by activityViewModels()
  private lateinit var PcrProductPaymentAdapter: PcrProductPaymentAdapter
  private var layoutManager: GridLayoutManager? = null
  private var isAnimationCanceled = false
  private lateinit var recyclerViewPagingUtil: RecyclerViewPagingUtil
  private lateinit var cardAdapter: CardsAdapter
  var productglobal: ArrayList<Products>? = ArrayList()
  var streetaddress: String = ""
  var cityaddress: String = ""
  var areaaddress: String = ""
  private var animator: ValueAnimator? = null
  var phone: String = ""
  var appointmentinfo: String = ""
  private var isTopUp = false
  var addressid: Int = 0
  var timepcr: String = ""

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
      result?.address.let {
        addressViewModel.deliveredAddressMut.value = it
      }
    }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.paymentScreenOpen()
    arguments?.let {
      pcrviewModel.colid = it.getString("id")
    }
    mView = super.onCreateView(inflater, container, savedInstanceState)
    observers()
    initUI()
    return mView
  }

  private fun initUI() {
//    viewModel.setCartModel()
    binding.addressViewModel = addressViewModel
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.click = this
    binding.tollBar.click = this
    binding.tollBar.tvToolbarTitle.text = getString(R.string.covidrtpcr)
    binding.tvCompleteDate.text = appManager.persistenceManager.completedate
    binding.tvCompleteTime.text = appManager.persistenceManager.timepcrglobal
    streetaddress = appManager.persistenceManager.streetaddress
    binding.tvAmount.text = appManager.persistenceManager.calprice.toString()
    areaaddress = appManager.persistenceManager.areaaddress
    cityaddress = appManager.persistenceManager.cityaddress
    phone = appManager.persistenceManager.phoneaddress
    addressid = appManager.persistenceManager.addressid!!
    binding.tvAddress.text = "$streetaddress ,$areaaddress, $cityaddress"
    binding.tvPhone.text = "$phone"
    productglobal = appManager.persistenceManager.pcritemselected
    appointmentinfo = appManager.persistenceManager.appointmentinfo
    Log.e(TAG, "initUI:appointmentinfo$appointmentinfo  $streetaddress")

    if (!appointmentinfo.isNullOrEmpty()) {
      binding.tvInfoview.visibility = View.VISIBLE
      binding.tvSampleinfo.text = appointmentinfo
    } else {
      binding.tvInfoview.visibility = View.GONE
    }
//    appManager.persistenceManager.pcritemselected.clear()
//    pcrviewModel.getPlaceOrderModel(addressid)
//    placeOrderRequest()
//    refreshvalues()
    initProductsListRV()
    bindPayment()
    bindOrderSummery()
    initCardRV()
    handlePaymentMethodSelection()
    viewModel.codCharges.value =
      viewModel.appManager.storageManagers.config.cODCHARGES ?: ConstantsUtil.COD_CHARGES
    walletVieModel.requestCards()
    binding.isLoggedIn = profileViewModel.isLoggedIn
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      profileViewModel.isLoggedIn.set(true)
    } else {
      profileViewModel.isLoggedIn.set(false)
    }
//commenting as use case need to be confirmed use not found
//    viewModel.doAmountCalculationsPcr()

  }

  private fun initProductsListRV() {
    PcrProductPaymentAdapter =
      PcrProductPaymentAdapter(requireActivity(), this, viewModel.appManager)
    layoutManager = GridLayoutManager(requireContext(), 1)

    binding.rvProduct.adapter = PcrProductPaymentAdapter

    recyclerViewPagingUtil = RecyclerViewPagingUtil(
      binding.rvProduct,
      layoutManager!!, this
    )

    binding.rvProduct.addOnScrollListener(recyclerViewPagingUtil)
    binding.rvProduct.post { // Call smooth scroll
      binding.rvProduct.scrollToPosition(0)
    }
    if (productglobal != null) {
      PcrProductPaymentAdapter.setDataChanged(productglobal)

    }


//    resetSkip()
  }

  fun refreshvalues() {
    binding.tvOrderAmount.text = appManager.persistenceManager.subtotal.currencyFormat()
    binding.tvDis.text = appManager.persistenceManager.discounttotal.currencyFormat()
    binding.tvVatAmount.text = appManager.persistenceManager.vattotal.currencyFormat()
    binding.additionalcharges.text =
      appManager.persistenceManager.additional_charges_amount.toString()
    var sumtotal = appManager.persistenceManager.totalwithoutadditonal
    var add = appManager.persistenceManager.additional_charges_amount
    binding.tvTotalamount.text = sumtotal.plus(add).currencyFormat()
    binding.tvAmount.text = sumtotal.plus(add).currencyFormat()

  }

  private fun bindPayment() {
    viewModel.paymentDetailsTitle.value = getString(R.string.price_details)
    viewModel.paymentDetailsSubTotalTitle.value = getString(R.string.subtotal)
    binding.profileViewModel = profileViewModel
    binding.viewModel = viewModel
    binding.topViewModel = topViewModel
    refreshvalues()
    binding.lifecycleOwner = this
    bindDefaultPaymentMethod()
    if (viewModel.appManager.persistenceManager.isPaymentMethod()) {
      when (viewModel.appManager.persistenceManager.getLastPayment()) {
        "card" -> {
          if (viewModel.appManager.storageManagers.config.isCardEnabled == true) {
            viewModel.selectedPaymentType.value = PaymentType.CARD
          }
        }
        "cash" -> {
          if (viewModel.appManager.storageManagers.config.isCodeEnabled == true) {
            viewModel.selectedPaymentType.value = PaymentType.CASH
          }
        }
        "wallet" -> {
          if (viewModel.appManager.storageManagers.config.isWalletEnabled == true) {
            viewModel.selectedPaymentType.value = PaymentType.WALLET
          }
        }
      }
    } else {
      viewModel.selectedPaymentType.value = PaymentType.CARD
    }

  }

  private fun bindDefaultPaymentMethod() {
    if (viewModel.appManager.storageManagers.config.isWalletEnabled == true) {
      viewModel.selectedPaymentType.value = PaymentType.WALLET
    }
    if (viewModel.appManager.storageManagers.config.isCodeEnabled == true) {
      viewModel.selectedPaymentType.value = PaymentType.CASH
    }
    if (viewModel.appManager.storageManagers.config.isCardEnabled == true) {
      viewModel.selectedPaymentType.value = PaymentType.CARD
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.appManager.storageManagers.getSettings()
  }


  private fun bindOrderSummery() {

//    binding.isPayment = true
//    binding.viewModel = viewModel
//    binding.lifecycleOwner = this
//   viewModel.calculateCompleteShipmentCharges(profileViewModel.userObjectMut.value)
////    viewModel.calculateCompleteShipmentCharges()

  }

  //calculateCompleteShipmentCharges
  fun clearRadioChecked() {
    binding.rgCash.setChecked(false)
    binding.rgNewCard.setChecked(false)
    binding.rgWallet.setChecked(false)
  }


  private fun handlePaymentMethodSelection() {
    binding.rgCash.setOnCheckedChangeListener { _, b ->
      if (b) {
        viewModel.selectedPaymentType.value = PaymentType.CASH
        appManager.persistenceManager.pcrpaymenttypeselected = PaymentType.CASH
        clearRadioChecked()
        binding.rgCash.setChecked(true)
        cardAdapter.selectedItem(-1)
//        pcrviewModel.checkWalletIsValid()
//        viewModel.calculateCompleteShipmentCharges()
//        viewModel.calculateCompleteShipmentChargesPcr(profileViewModel.userObjectMut.value,true)
      }
    }
    binding.rgNewCard.setOnCheckedChangeListener { _, b ->
      if (b) {
        viewModel.selectedPaymentType.value = PaymentType.NEW
        appManager.persistenceManager.pcrpaymenttypeselected = PaymentType.NEW
        clearRadioChecked()
        binding.rgNewCard.setChecked(true)
        cardAdapter.selectedItem(-1)
//      viewModel.calculateCompleteShipmentCharges()
//        viewModel.calculateCompleteShipmentChargesPcr(profileViewModel.userObjectMut.value,true)
      }
    }
    binding.rgWallet.setOnCheckedChangeListener { _, b ->
      if (b) {
        viewModel.selectedPaymentType.value = PaymentType.WALLET
        appManager.persistenceManager.pcrpaymenttypeselected = PaymentType.WALLET
        clearRadioChecked()
        binding.rgWallet.setChecked(true)
        cardAdapter.selectedItem(-1)
        viewModel.checkWalletIsValidPcr(profileViewModel.userObjectMut.value)
//        viewModel.calculateCompleteShipmentCharges()
//        viewModel.calculateCompleteShipmentChargesPcr(profileViewModel.userObjectMut.value,true)
      }
    }
  }

  private fun initCardRV() {
    cardAdapter = CardsAdapter(requireActivity(), this)
    binding.rvCards.adapter = cardAdapter


  }


  private fun observers() {

//    viewModel.outOfStockCartItem.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//      it?.let {
//        binding
//      }
//    })

    walletVieModel.cardsDataMut
      .observe(viewLifecycleOwner) {
        it?.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
              cardAdapter.setDataChanged(it.data)
//              Log.e(TAG, "observers: ${it.data}")
//              if(viewModel.selectedPaymentType.value == PaymentType.WALLET || viewModel.selectedPaymentType.value == PaymentType.NEW ||
//                viewModel.selectedPaymentType.value == PaymentType.CASH){
//
//                cardarrayList = it.data
//                cardarrayList?.clear()
//                rv_cards?.adapter?.notifyDataSetChanged()
//
//              }

              when {
                it.data == null -> {
//                viewModel.selectedPaymentType.value = PaymentType.NEW
                }
                it.data.isEmpty() -> {
//                viewModel.selectedPaymentType.value = PaymentType.NEW
                }
                else -> {
                  if (viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "card" ||
                    viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "new"
                  ) {
                    cardAdapter.selectedItem(0)
                    walletVieModel.selectedCard = it.data[0]
                    topViewModel.cardMainModel = it.data[0]

                  }
                  viewModel.checkAndUpdateAccordingTopPreviousPaymentTyp()
                }
              }
            }
            Result.Status.ERROR -> {
              Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                .show()
            }
            Result.Status.LOADING -> {

            }
          }
        }

      }

    addressViewModel.deliveredAddressMut.observe(viewLifecycleOwner) {
      it?.let {
        if (it.id == null) {
          addressViewModel.isThereAnyAddress.value = (false)
        } else {
          addressViewModel.isThereAnyAddress.value = (true)
        }
      }

    }
    viewModel.selectedPaymentType.observe(viewLifecycleOwner) {
      it?.let {
        Logger.d(
          "SelectedPaymentTyp",
          "${viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)}"
        )
        when (it) {
          PaymentType.WALLET_WITH_LESS -> {
            Log.e(TAG, "observers: wallet less")
//            findNavController().navigate(R.id.orderWalletTopUpBottomSheet)
            if (!OrderWalletTopUpBottomSheet.inView && !OrderSheetPcrBottomSheet.inView) {
              findNavController().navigate(R.id.orderWalletTopUpBottomSheet)
            }

          }
          PaymentType.CARD -> {
            if (cardAdapter.arrayList.isNullOrEmpty()) {
              viewModel.selectedPaymentType.value = PaymentType.NEW
            } else {
              if (cardAdapter.rowIndex < 0) {
                cardAdapter.selectedItem(0)
              }
            }
          }
          PaymentType.NEW -> {
//            binding.lyPaymentMethods.lyCards.isNewCardSelected = true
          }

          PaymentType.NEW_FOR_WALLET -> {
            topViewModel.viaNewCard = true
//            binding.lyPaymentMethods.lyCardsForTopUp.isNewCardSelected = true
          }
          PaymentType.WALLET -> {
            if (!OrderSheetPcrBottomSheet.inView) {
              viewModel.checkWalletIsValidPcr(profileViewModel.userObjectMut.value)
            }

          }
          else -> {
            topViewModel.viaNewCard = false
          }
        }
      }

    }
    addressViewModel.addressChanged.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
      it?.let {
        it?.let {
          when (it) {
            AddressChanged.ADDRESS_CHANGED -> {
              findNavController().navigateUp()
            }
            AddressChanged.ADDRESS_UNCHANGED -> {

            }
          }
        }
      }
    })
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_pcr_payment
  }

  override fun permissionGranted(requestCode: Int) {
  }

  override fun changeAddress() {

  }

  private fun locationPermissionGranted() {
    addressContract.launch(true)
  }

  private fun setTermsText() {
    val text =
      "<font color=#707070>${getString(R.string.by_placing_order_terms)} </font> <font color=#365FC9> <b>  ${
        getString(
          R.string.terms_and_condition_small
        )
      }</b></font>"

    val html = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY);
//    binding.tvTerms.text = html
  }

  override fun onCardSelect(position: Int?, cardMainModel: CardMainModel,pg:Int) {
    viewModel.selectedPaymentType.value = PaymentType.CARD
    topViewModel.cardMainModel = cardMainModel
    walletVieModel.selectedCard = cardMainModel
    cardAdapter.selectedItem(position)
//    Logger.d("SelectedPaymentTyp","${viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)}")\\

    viewModel.checkAndUpdateAccordingTopPreviousPaymentTyp()

  }

  override fun onDeleteCard(position: Int?, cardMainModel: CardMainModel) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

  override fun onClickTerms() {
    findNavController().navigate(
      R.id.pageFragment,
      PageFragment.getPageFragmentBundle("terms-and-conditions")
    )
  }

  private fun animateProgressBar() {
//    animator = ValueAnimator.ofInt(0, binding.pbLoading.max)
//    animator?.duration = 3000
//    animator?.addUpdateListener { animation ->
//      binding.pbLoading.progress = animation.animatedValue as Int
//    }
    animator?.addListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator?) {
        super.onAnimationEnd(animation)
        // start your activity here
        if (!isAnimationCanceled) {
          placeOrderRequest()
        }
      }
    })
    animator?.start()
  }
//existing
//  override fun onClickProceed() {
//    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
////      triggerMultipleEvent()
//      if ((!viewModel.appManager.persistenceManager.getLoggedInUser()?.name.isNullOrBlank()
//            && !viewModel.appManager.persistenceManager.getLoggedInUser()?.email.isNullOrBlank())
//      ) {
////        if (appManager.persistenceManager.addressid != null) {
////          findNavController().navigate(R.id.orderSheetPcrBottomSheet)
//        placeOrderRequest()
//
//      } else {
//        requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
//      }
//    } else {
//      findNavController().navigate(R.id.nav_login_sheet)
//    }
//  }

//  override fun onClickProceed() {
//    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
////      triggerMultipleEvent()
//      if ((!viewModel.appManager.persistenceManager.getLoggedInUser()?.name.isNullOrBlank()
//            && !viewModel.appManager.persistenceManager.getLoggedInUser()?.email.isNullOrBlank())
//      ) {
//        if (appManager.persistenceManager.addressid != null) {
//          if (viewModel.selectedPaymentType.value == PaymentType.NEW || viewModel.selectedPaymentType.value == PaymentType.CASH ||
//            viewModel.selectedPaymentType.value == PaymentType.CARD
//          ) {
//            placeOrderRequest()
//          } else if (viewModel.selectedPaymentType.value == PaymentType.WALLET || viewModel.selectedPaymentType.value == PaymentType.WALLET_WITH_LESS ||
//            viewModel.selectedPaymentType.value == PaymentType.NEW_FOR_WALLET
//          ) {
//            findNavController().navigate(R.id.orderSheetPcrBottomSheet)
//          }
//        } else {
//          requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
//        }
//      } else {
//        findNavController().navigate(R.id.nav_login_sheet)
//      }
//    } else {
//      findNavController().navigate(R.id.nav_login_sheet)
//    }
//  }


  override fun onClickProceed() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      Log.e(TAG, "onClickProceed: ")
      if (appManager.persistenceManager.addressid != null) {
        placeOrderRequest()
      } else {
        requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
      }
//      when (viewModel.selectedPaymentType.value) {
//        PaymentType.CARD -> {
//          animateProgressBar()
//          isAnimationCanceled = false
//          viewModel.showLaodingOrderProgressBar.value = true
//        }
//        else -> {
//          placeOrderRequest()
//        }
//      }

//      triggerMultipleEvent()
//      if ((!viewModel.appManager.persistenceManager.getLoggedInUser()?.name.isNullOrBlank()
//            && !viewModel.appManager.persistenceManager.getLoggedInUser()?.email.isNullOrBlank())
//      ) {
//        if (addressViewModel.deliveredAddressMut.value != null && addressViewModel.deliveredAddressMut.value?.id != null) {
//          findNavController().navigate(R.id.orderSheetPcrBottomSheet)
////          placeOrderRequest()
//
//        } else {
//          requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
//        }
//      } else {
//        findNavController().navigate(R.id.nav_login_sheet)
//      }
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }
  }

  override fun onClickPlus() {
//    viewModel.plus()
  }

  override fun onClickMinus() {
//    viewModel.outOfStockCartItem.value?.qty?.let {
//      if (it == 1) {
//        MaterialAlertDialogBuilder(
//          requireContext(),
//          R.style.ThemeOverlay_App_MaterialAlertDialog
//        )
//          .setTitle(getString(R.string.remove_product_title))
//          .setMessage(getString(R.string.remove_product_descr))
//          .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
//          }
//          .setPositiveButton(getString(R.string.remove)) { dialog, which ->
//            findNavController().navigateUp()
//          }
//          .show()
//      } else {
//        viewModel.minus()
//      }
//    }
  }
//
//  private fun placeOrderRequest() {
//    Log.e(TAG, "placeOrderRequest: ")
////    if (addressViewModel.deliveredAddressMut.value?.isAddressValid() == true) {
//    appManager.persistenceManager.addressid?.let {
//      pcrviewModel.getPlaceOrderModel(
//        it
//      )
//    }?.let { placePcrOrderRequest ->
//      Log.e(TAG, "placeOrderRequest:${viewModel.orderIdpcr} ")
//      when {
//        viewModel.orderIdpcr == 0 -> {
//          when (viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)) {
//            "wallet_with_less", "new_for_wallet" -> {
//              isTopUp = true
//              createOrder(placePcrOrderRequest)
//            }
//            else -> {
//              isTopUp = false
//              createOrder(placePcrOrderRequest)
//            }
//          }
//        }
//        isTopUp -> {
//          createTopUpTransaction()
//        }
//        else -> {
//          when (viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)) {
//            "wallet_with_less", "new_for_wallet" -> {
//              isTopUp = true
//              createTopUpTransaction()
//            }
//            else -> {
//              isTopUp = false
//              createTransaction()
//            }
//          }
//
//        }
//      }
//
//    }
//    /*} else {
//
//
//
//
//
//
//      MaterialAlertDialogBuilder(
//        requireActivity(),
//        R.style.ThemeOverlay_App_MaterialInfoDialog
//      )
//        .setTitle("Invalid Address")
//        .setMessage("Some of the fields of selected address is not valid please edit and try again ")
//        .setPositiveButton(getString(R.string.ok)) { dialog, which ->
//
//          findNavController().navigate(R.id.nav_address)
//        }
//        .show()
//    }*/
//  }

  private fun placeOrderRequest() {
    Log.e(TAG, "placeOrderRequest: ")
//    if (addressViewModel.deliveredAddressMut.value?.isAddressValid() == true) {
    appManager.persistenceManager.addressid?.let {
      pcrviewModel.getPlaceOrderModel(
        it
      )
    }?.let { placePcrOrderRequest ->
      createOrder(placePcrOrderRequest)
//      when {
//        viewModel.orderIdpcr == 0 -> {
//          Log.e(TAG, "placeOrderRequest: 1")
//          when (viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)) {
//            "wallet_with_less", "new_for_wallet" -> {
//              Log.e(TAG, "placeOrderRequest: 2")
//              isTopUp = true
//              createOrder(placePcrOrderRequest)
//            }
//            else -> {
//              isTopUp = false
//              Log.e(TAG, "placeOrderRequest: 3")
//              createOrder(placePcrOrderRequest)
//            }
//          }
//        }
//        isTopUp -> {
//          Log.e(TAG, "placeOrderRequest: 4")
//          createTopUpTransaction()
//        }
//        else -> {
//          when (viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)) {
//            "wallet_with_less", "new_for_wallet" -> {
//              isTopUp = true
//              Log.e(TAG, "placeOrderRequest: 5")
//              createTopUpTransaction()
//            }
//            else -> {
//              isTopUp = false
//              Log.e(TAG, "placeOrderRequest: 6")
//              createTransaction()
//            }
//          }
//
//        }
//      }
    }
    /*} else {
      MaterialAlertDialogBuilder(
        requireActivity(),
        R.style.ThemeOverlay_App_MaterialInfoDialog
      )
        .setTitle("Invalid Address")
        .setMessage("Some of the fields of selected address is not valid please edit and try again ")
        .setPositiveButton(getString(R.string.ok)) { dialog, which ->

          findNavController().navigate(R.id.nav_address)
        }
        .show()
    }*/
  }

  private fun handleTopUpTransaction(result: TransactionModel) {
    isTopUp = false
    when (viewModel.selectedPaymentType.value) {
      PaymentType.CASH -> {
      }
      PaymentType.CARD, PaymentType.NEW_FOR_WALLET -> {
        if (topViewModel.viaNewCard) {
          result.let { it1 ->
            openActivityForPayment(it1.paymentUrl, it1.successUrl, it1.failUrl)
          }
        } else {
          if (result?.status == 0) {
            AlertManager.showErrorMessage(
              requireActivity(),
              getString(R.string.transaction_failed)
            )
          } else {
            viewModel.selectedPaymentType.value = PaymentType.WALLET
            placeOrderRequest()
          }
        }
      }
      PaymentType.WALLET -> {

      }

      PaymentType.WALLET_WITH_LESS -> {
        if (result?.status == 0) {
          AlertManager.showErrorMessage(
            requireActivity(),
            getString(R.string.transaction_failed)
          )
        } else {
          viewModel.selectedPaymentType.value = PaymentType.WALLET
          placeOrderRequest()
        }
      }
      else -> {

      }
    }
  }

  private fun createTopUpTransaction() {
    isTopUp = true
    topViewModel.amount.setValue(viewModel.walletDifference.value.toString())
    topViewModel.topUp(topViewModel.getTransactionModer())
      .observe(viewLifecycleOwner) {
        Log.e(TAG, "createTopUpTransaction: shiffaa")
        it?.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
              hideLoading()
              it.data?.data?.let { it1 -> handleTopUpTransaction(it1) }
            }
            Result.Status.ERROR -> {
              hideLoading()
              it.message?.let { it1 ->
                AlertManager.showErrorMessage(
                  requireActivity(),
                  it1
                )
              }
              viewModel.appManager.analyticsManagers.transactionFailed()
            }
            Result.Status.LOADING -> {
              showLoading()
            }
          }
        }

      }
  }

  private fun handleCreateOrderResult(response: OrderResponseModel) {
//    viewModel.appManager.analyticsManagers.orderCreated(response)
    response.orderId?.let { displayID ->
      viewModel.orderDisplayId = displayID
    }
    response.id?.let { orderId ->
      viewModel.orderIdpcr = orderId
      pcrviewModel.orderId = orderId
      Log.e(TAG, "handleCreateOrderResult: shifaa$isTopUp")
      if (isTopUp) {
        viewModel.setWalletDifference(response.total)
//        viewModel.totalAmount.value = response.total
//        viewModel.totalAmountPcr.value = response.total
        createTopUpTransaction()
      } else {
        createTransaction()
      }
    }
  }

  private fun orderPlace(transactionModel: TransactionModel?) {
    //OrderPlaced
    findNavController().navigate(R.id.appoinmentPlacedDialog)

  }

  private fun openActivityForPayment(paymentUrl: String?, successUrl: String?, failUrl: String?) {
    val intent = Intent(requireActivity(), WebViewPaymentActivity::class.java)
    intent.putExtra("paymentURL", paymentUrl)
    intent.putExtra("successURL", successUrl)
    intent.putExtra("failURL", failUrl)
    startActivityForResult(intent, ConstantsUtil.PAYMENT_ACTIVITY_REQUEST_CODE)
//    WebViewPaymentActivity.open(requireActivity(), urls)
  }

  @SuppressLint("Recycle")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
      when (requestCode) {
        ConstantsUtil.PAYMENT_ACTIVITY_REQUEST_CODE -> {
          val status = (data ?: return).getIntExtra("status", 0)
          if (status == 1) {
            viewModel.appManager.analyticsManagers.checkOutCompleted()
            when (pcrviewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)) {
              "wallet_with_less", "new_for_wallet" -> {
                viewModel.selectedPaymentType.value = PaymentType.WALLET
                createTransaction()
              }
              else -> {
                orderPlace(null)
              }
            }
          } else {
            isTopUp =
              when (viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)) {
                "wallet_with_less", "new_for_wallet" -> {
                  true
                }
                else -> {
                  false
                }
              }
            viewModel.appManager.analyticsManagers.transactionFailed()
            AlertManager.showErrorMessage(
              requireActivity(),
              getString(R.string.payment_failed)
            )
          }
        }
      }
    }
  }

  private fun handleCreateTransactionForOrder(result: GeneralResponseModel<TransactionModel>) {
    result?.data?.let {
      when (viewModel.selectedPaymentType.value) {
        PaymentType.CASH -> {
          orderPlace(result.data)
        }
        PaymentType.CARD -> {
          if (it.status == 0) {
            Log.e(TAG, "handleCreateTransactionForOrder: shifa")
            AlertManager.showErrorMessage(
              requireActivity(),
              getString(R.string.transaction_failed)
            )
          } else {
            orderPlace(result.data)
          }
        }
        PaymentType.NEW -> {
          it.let { it1 -> openActivityForPayment(it1.paymentUrl, it1.successUrl, it1.failUrl) }
        }
        PaymentType.WALLET -> {
          orderPlace(result.data)
        }

        else -> {
          if (it.status == 0) {
            result.message.let { it1 ->
              AlertManager.showErrorMessage(
                requireActivity(),
                it1
              )
            }
          } else {
            orderPlace(result.data)
          }
        }

      }
    }
  }

  private fun createTransaction() {
    pcrviewModel.createPcrTransaction(viewModel.getTransactionModers(walletVieModel.selectedCard?.id))
      .observe(
        viewLifecycleOwner
      ) {
        it?.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
              hideLoading()
              it.data?.let { it1 ->
                handleCreateTransactionForOrder(it1)
              }
            }
            Result.Status.ERROR -> {
              it.message?.let { it1 ->
                AlertManager.showErrorMessage(
                  requireActivity(),
                  it1
                )
              }
              viewModel.appManager.analyticsManagers.transactionFailed()
              hideLoading()
            }
            Result.Status.LOADING -> {
              showLoading()
            }
          }
        }

      }
  }

  private fun createOrder(placePcrOrderRequest: PlacePcrOrderRequest) {
    pcrviewModel.createPcrOrder(placePcrOrderRequest)
      .observe(viewLifecycleOwner) {
        it?.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
              hideLoading()

              it?.data?.data?.let { it1 -> handleCreateOrderResult(it1) }
            }
            Result.Status.ERROR -> {
              it.message?.let { it1 ->
                AlertManager.showErrorMessage(
                  requireActivity(),
                  it1
                )
              }
//              updateCartObserver()
              hideLoading()
              viewModel.appManager.analyticsManagers.checkOutFailed()
            }
            Result.Status.LOADING -> {
              showLoading()
            }
          }
        }

      }
  }

  override fun onClickChangeAddress() {
    addressContract.launch(true)
  }

  private fun placeOrder() {
    findNavController().navigate(R.id.orderSummaryOutOfStockBottomSheet)
  }

  override fun onClickPcrListSelected(position: Int, rootCategory: Products) {

  }

  override fun onProductClicked(productDetails: Products, position: Int) {

  }

  override fun onPcrAddClicked(position: Int, rootCategory: Products) {

    appManager.persistenceManager.addProductfirst(rootCategory)
//    findNavController().navigate(R.id.pcrPaymentFragment)

    refreshvalues()

  }

  override fun onPcrAddbtnClicked(position: Int, rootCategory: Products) {
    appManager.persistenceManager.onPcrAddbtnClicked(position, rootCategory)
//    findNavController().navigate(R.id.pcrPaymentFragment)
    refreshvalues()

  }

  override fun onPcrMinusbtnClicked(position: Int, rootCategory: Products) {

    appManager.persistenceManager.onPcrMinusbtnClicked(position, rootCategory)
//    findNavController().navigate(R.id.pcrPaymentFragment)
    refreshvalues()

  }

  override fun onPcrProductClicked(productDetails: Products, position: Int) {
    pcrviewModel.position = position
    pcrviewModel.previewProductMut.value = productDetails
//    findNavController().navigate(
//      R.id.pcrDetailsBottomSheet,
//      PcrDetailsBottomSheet.getBundle(productID = productDetails.id, position)
//    )
    findNavController().navigate(
      R.id.pcrDetailsBottomSheet
    )
  }


  override fun onClickAddProduct(productDetails: Products, position: Int) {

  }

  override fun onClickMinus(productDetails: Products, position: Int) {

  }

  override fun onClickPlus(productDetails: Products, position: Int) {

  }

  override fun onClickWishList(productDetails: Products, position: Int) {

  }

  override fun onClickNotify(productDetails: Products, position: Int) {

  }

  override fun onClickOrderOutOfStock(productDetails: Products, position: Int) {

  }

  override fun onNextPage(skip: Int, take: Int) {

  }


}

