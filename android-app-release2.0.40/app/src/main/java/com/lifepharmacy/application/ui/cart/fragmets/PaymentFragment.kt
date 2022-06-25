package com.lifepharmacy.application.ui.cart.fragmets

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.checkout.android_sdk.Utils.CardUtils
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentPaymentBinding
import com.lifepharmacy.application.enums.AddressChanged
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.initiateCheckOutForListProducts
import com.lifepharmacy.application.managers.analytics.paymentScreenOpen
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.cart.DeliveryInstructions
import com.lifepharmacy.application.model.config.DeliverySlot
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.model.pcr.pcrdatetime.Slots
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressSelectionActivity
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.address.ClickSelectedAddress
import com.lifepharmacy.application.ui.address.dailog.ClickLayoutAddress
import com.lifepharmacy.application.ui.card.CardsAdapter
import com.lifepharmacy.application.ui.card.ClickCard
import com.lifepharmacy.application.ui.cart.adapter.*
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.rewards.adapters.DeliveryInsAdapter
import com.lifepharmacy.application.ui.rewards.adapters.PcrDateAdapter
import com.lifepharmacy.application.ui.rewards.adapters.PcrTimeAdapter
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog.Companion.TAG
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.wallet.viewmodels.TopViewModel
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Logger
import com.lifepharmacy.application.utils.universal.RecyclerViewPagingUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class PaymentFragment : BaseFragment<FragmentPaymentBinding>(),
  ClickCard, ClickLayoutAddress, ClickTool, ClickSelectedAddress, ClickPaymentFragment {
  private val viewModel: CartViewModel by activityViewModels()
  private val addressViewModel: AddressViewModel by activityViewModels()
  private val walletVieModel: WalletViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  private val topViewModel: TopViewModel by activityViewModels()
  private lateinit var cardAdapter: CardsAdapter
  private var layoutManager: GridLayoutManager? = null

  //  private lateinit var deliveryOptionAdapter: DeliveryOptionsAdapter
  private lateinit var deliveryInsAdapter: DeliveryInsAdapter
  private lateinit var instantShipmentProducts: CartShipmentProductAdapter
  private lateinit var expressShipmentProducts: CartShipmentProductAdapter
  private lateinit var freeGiftsShipmentProducts: CartShipmentProductAdapter
  private lateinit var instantSlots: DeliverSlotsAdapter
  private lateinit var standardSlots: StandardDeliverSlotsAdapter
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

  private var isSetManualInstantSwitchListen = false

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.paymentScreenOpen()
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      appManager.storageManagers.deliveryinsselected.clear()
      Log.e(TAG, "onCreateView: ${appManager.storageManagers.deliveryinsselected}")
//      initUI()
    }
    Log.e(TAG, "onCreateView: ")
//    mView = super.onCreateView(inflater, container, savedInstanceState)
    viewModel.offersManagers.makeProductsArray()
    initUI()
    observers()
    if (viewModel.offersManagers.cartQtyCountMut.value == null || viewModel.offersManagers.cartQtyCountMut.value == 0) {
      findNavController().navigateUp()
    }
    CoroutineScope(Dispatchers.Main.immediate).launch {
      delay(1000)
      isSetManualInstantSwitchListen = true
    }
    reselectionCartObserver()
    return mView
  }

  private fun initUI() {
    Log.e(TAG, "initUI: ttt")
    binding.lyDeliveryOptions.viewModel = viewModel
    binding.addressViewModel = addressViewModel
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.lyDeliveryOptions.lifecycleOwner = this
    binding.click = this
    binding.layoutLocation.click = this
    binding.layoutLocation.mViewModel = addressViewModel
    binding.layoutLocation.lifecycleOwner = this
    binding.layoutDelivery.click = this
//    binding.layoutDelivery. = addressViewModel
    binding.layoutDelivery.lifecycleOwner = this
    binding.lyAddress.clicklayout = this
    binding.lyAddress.isShipment = true
    binding.llOrderPlaced.click = this
    binding.tollBar.click = this
    binding.llReturnPolicy.viewModel = viewModel
    binding.llReturnPolicy.lifecycleOwner = this
    binding.tollBar.tvToolbarTitle.text = getString(R.string.payment)
//    bindPayment()
    alerview()
    bindpaymentbottomview()
    bindDeliveryOptions()
    bindOrderSummery()
    viewModel.instantCharge.value =
      viewModel.appManager.storageManagers.config.iNSTANTDELIVERYFEE
        ?: ConstantsUtil.INSTANT_DELIVERY_FEE
    viewModel.standardCharge.value =
      viewModel.appManager.storageManagers.config.eXPRESSDELIVERYFEE
        ?: ConstantsUtil.EXPRESS_DELIVERY_FEE
    viewModel.isInstantChecked.value = (false)
//    initCardRV()
//    handlePaymentMethodSelection()
    handleGetTogetherListener()
    initRVInstantShipment()
    deliveryInstruction()
    initRVExpressShipment()
    initRVGiftShipment()
    initRVDeliverySlots()
    initRVStandardDeliverySlots()

    viewModel.codCharges.value =
      viewModel.appManager.storageManagers.config.cODCHARGES ?: ConstantsUtil.COD_CHARGES
    walletVieModel.requestCards()
    binding.isLoggedIn = profileViewModel.isLoggedIn
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      profileViewModel.isLoggedIn.set(true)
    } else {
      profileViewModel.isLoggedIn.set(false)
    }
    viewModel.orderId = 0

    viewModel.Bindpaymenttype()
    Log.e(TAG, "initUI:cardno ${viewModel.cardno}")
    type()
//    carddetail(viewModel.cardno)
    binding.tvPaymenttype.text = viewModel.cardend
  }

  private fun sanitizeEntry(entry: String): String {
    return entry.replace("\\D".toRegex(), "")
  }


  fun reselectionCartObserver() {
    viewModel.appManager.loadingState.cartReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_cart)
          viewModel.appManager.loadingState.cartReselected.value = false
        }
      }
    }
  }

  private fun bindPayment() {
    viewModel.paymentDetailsTitle.value = getString(R.string.price_details)
    viewModel.paymentDetailsSubTotalTitle.value = getString(R.string.subtotal)
    binding.lyPaymentMethods.profileViewModel = profileViewModel
    binding.lyPaymentMethods.mViewModel = viewModel
    binding.lyPaymentMethods.topViewModel = topViewModel
    binding.lyPaymentMethods.lifecycleOwner = this
    binding.lyOrderSummary.isShipping = true
    viewModel.bindDefaultPaymentMethod()
    viewModel.setLastPaymentType()
  }

  override fun onResume() {
    Log.e(TAG, "onResume: payment frag${viewModel.cardend}")
    binding.tvPaymenttype.text = viewModel.cardend
    type()
    super.onResume()
    bindpaymentbottomview()
    viewModel.appManager.storageManagers.getSettings()
  }

//  override fun onStart() {
//    Log.e(TAG, "onStart: ")
//    super.onStart()
//  }
//
//
//  override fun onDestroy() {
//    Log.e(TAG, "onDestroy: ")
//    super.onDestroy()
//  }
//
//  override fun onPause() {
//    Log.e(TAG, "onPause: ")
//    super.onPause()
//  }
//
//  override fun onStop() {
//    Log.e(TAG, "onStop: ")
//    super.onStop()
//
//  }

  fun alerview() {

    if (!viewModel.appManager.storageManagers.alertmessage.title.isNullOrEmpty()) {
      viewModel.appManager.storageManagers.alertmessage.let {
        binding.layoutDelivery.items = it.icon
        binding.layoutDelivery.tvDelivery1.text = it.title.toString()
        binding.layoutDelivery.tvDeliveryin2.text = it.subtitle.toString()
        binding.layoutDelivery.clDelivery?.setBackgroundColor(Color.parseColor(it.background.toString()))
      }
    } else {
      binding.layoutDelivery.clMain.visibility = View.GONE
    }
  }

  fun bindpaymentbottomview() {
    Log.e(TAG, "bindpaymentbottomview: ${viewModel.paymentview}")
    if (viewModel.paymentview) {
      binding.btnSelectpayment.visibility = View.GONE
      binding.clBottom.visibility = View.VISIBLE
    } else {
      binding.btnSelectpayment.visibility = View.VISIBLE
      binding.clBottom.visibility = View.GONE
    }
  }

  private fun bindDeliveryOptions() {
    binding.lyDeliveryOptions.click = this
    binding.lyDeliveryOptions.note = viewModel.note
    viewModel.note.setEditText(binding.lyDeliveryOptions.edNote)
    viewModel.isLeaveAtMyDoor.value = binding.lyDeliveryOptions.swLeaveAtDoor.isChecked
    binding.lyDeliveryOptions.swLeaveAtDoor.setOnCheckedChangeListener { _, b ->
      viewModel.isLeaveAtMyDoor.value = b
    }
  }

  private fun bindOrderSummery() {
    binding.lyOrderSummary.isPayment = true
    binding.lyOrderSummary.mViewModel = viewModel
    binding.lyOrderSummary.lifecycleOwner = this
    viewModel.calculateCompleteShipmentCharges(profileViewModel.userObjectMut.value)

  }

  private fun deliveryInstruction() {
    deliveryInsAdapter = DeliveryInsAdapter(requireActivity(), this, viewModel.appManager)
    layoutManager = GridLayoutManager(requireContext(), 1)
    binding.lyDeliveryOptions.rvDeliveryins.adapter = deliveryInsAdapter
  }

  private fun initRVInstantShipment() {
    instantShipmentProducts = CartShipmentProductAdapter(requireActivity())
    binding.lyDeliveryOptions.rvInstantProducts.adapter = instantShipmentProducts
  }

  private fun initRVExpressShipment() {
    expressShipmentProducts = CartShipmentProductAdapter(requireActivity())
    binding.lyDeliveryOptions.rvExpressProducts.adapter = expressShipmentProducts
  }

  private fun initRVGiftShipment() {
    freeGiftsShipmentProducts = CartShipmentProductAdapter(requireActivity())
    binding.lyDeliveryOptions.rvGiftProducts.adapter = freeGiftsShipmentProducts
  }

  private fun initRVDeliverySlots() {
    instantSlots = DeliverSlotsAdapter(requireActivity(), object : ClickDeliverySlot {
      override fun onClickSlot(slot: DeliverySlot, position: Int) {
        viewModel.setInstantSlot(slot)
        instantSlots.setItemSelected(position)
      }

    })
    binding.lyDeliveryOptions.rvSlots.adapter = instantSlots
  }

  private fun initRVStandardDeliverySlots() {
    standardSlots =
      StandardDeliverSlotsAdapter(requireActivity(), object : ClickStandardDeliverySlot {
        override fun onClickSlot(slot: DeliverySlot, position: Int) {
          viewModel.setStandardSlot(slot)
          standardSlots.setItemSelected(position)
        }

      })
    binding.lyDeliveryOptions.rvStandardSlots.adapter = standardSlots
  }

  private fun handleGetTogetherListener() {
    binding.lyDeliveryOptions.swTogether.setOnCheckedChangeListener { _, b ->
      viewModel.isInstantChecked.value = (b)
      viewModel.offersManagers.changeDeliveryOption(b)
      viewModel.doAmountCalculations(profileViewModel.userObjectMut.value)
      if (isSetManualInstantSwitchListen) {
        viewModel.isManualInstantChecked.value = !b
      }
    }
    if (viewModel.appManager.storageManagers.config.inInstantIsDefault == true) {
      binding.lyDeliveryOptions.swTogether.isChecked = true
      viewModel.isInstantChecked.value = (true)
      viewModel.offersManagers.changeDeliveryOption(true)
      viewModel.doAmountCalculations(profileViewModel.userObjectMut.value)
    } else {
      viewModel.offersManagers.changeDeliveryOption(false)
    }
  }

//  private fun handlePaymentMethodSelection() {
//    binding.lyPaymentMethods.rgCash.setOnCheckedChangeListener { _, b ->
//      if (b) {
//        viewModel.selectedPaymentType.value = PaymentType.CASH
//        cardAdapter.selectedItem(-1)
//        viewModel.calculateCompleteShipmentCharges(profileViewModel.userObjectMut.value)
//        binding.lyDeliveryOptions.swLeaveAtDoor.isChecked = false
//
//      }
//    }
//    binding.lyPaymentMethods.rgNewCard.setOnCheckedChangeListener { _, b ->
//      if (b) {
//        viewModel.selectedPaymentType.value = PaymentType.NEW
//        cardAdapter.selectedItem(-1)
//        viewModel.calculateCompleteShipmentCharges(profileViewModel.userObjectMut.value)
//
//      }
//    }
//    binding.lyPaymentMethods.rgWallet.setOnCheckedChangeListener { _, b ->
//      if (b) {
//        viewModel.selectedPaymentType.value = PaymentType.WALLET
//        cardAdapter.selectedItem(-1)
//        viewModel.calculateCompleteShipmentCharges(profileViewModel.userObjectMut.value)
//      }
//    }
//  }

  private fun initCardRV() {
    cardAdapter = CardsAdapter(requireActivity(), this)
    binding.lyPaymentMethods.rvCards.adapter = cardAdapter

  }

  private fun observerCartSelectedItems() {


    viewModel.appManager.storageManagers.deliveryins.let {
      deliveryInsAdapter.setDataChanged(it)
    }

    viewModel.offersManagers.instantProductsMut.observe(viewLifecycleOwner) {
      it?.let {
        instantShipmentProducts.setDataChanged(it)

      }
    }
    viewModel.offersManagers.expressProductsMut.observe(viewLifecycleOwner) {
      it?.let {
        expressShipmentProducts.setDataChanged(it)

      }
    }
    viewModel.offersManagers.freeGiftProductArrayMut.observe(viewLifecycleOwner) {
      it?.let {
        freeGiftsShipmentProducts.setDataChanged(it)

      }
    }
    viewModel.offersManagers.instantCount.observe(viewLifecycleOwner) {
      it?.let {
        if (it == 0) {
          binding.lyDeliveryOptions.swTogether.isChecked = false
          viewModel.isInstantChecked.value = (false)
          instantSlots.clearSlots()
          viewModel.appManager.storageManagers.instantDeliverySlots.clear()
        } else {
          instantSlots.setDataChanged(viewModel.appManager.storageManagers.instantDeliverySlots)
          viewModel.setInstantDefaultSelectedSlot()
        }
      }
    }
    viewModel.offersManagers.expressCount.observe(viewLifecycleOwner) {
      it?.let {
        if (it == 0) {
          standardSlots.clearSlots()
          viewModel.appManager.storageManagers.standardDeliverySlots.clear()
        } else {
          standardSlots.setDataChanged(viewModel.appManager.storageManagers.standardDeliverySlots)
          viewModel.setStandardDefaultSelectedSlot()
        }
      }
    }
    viewModel.offersManagers.cartQtyCountMut.observe(viewLifecycleOwner) {
      it?.let {
        if (it > 0) {
          viewModel.isThereCart.value = (true)
        } else {
          viewModel.isThereCart.value = (false)
        }
      }
    }
  }

  private fun observers() {
    observerCartSelectedItems()
//    walletVieModel.cardsDataMut
//      .observe(viewLifecycleOwner) {
//        it?.let {
//          when (it.status) {
//            Result.Status.SUCCESS -> {
//              cardAdapter.setDataChanged(it.data)
//              when {
//                it.data == null -> {
////                viewModel.selectedPaymentType.value = PaymentType.NEW
//                }
//                it.data.isEmpty() -> {
////                viewModel.selectedPaymentType.value = PaymentType.NEW
//                }
//                else -> {
//                  if (viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "card" ||
//                    viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT) == "new"
//                  ) {
//                    cardAdapter.selectedItem(0)
//                    walletVieModel.selectedCard = it.data[0]
//                    topViewModel.cardMainModel = it.data[0]
//
//                  }
//                  viewModel.checkAndUpdateAccordingTopPreviousPaymentTyp()
//                }
//              }
//            }
//            Result.Status.ERROR -> {
//              Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
//                .show()
//            }
//            Result.Status.LOADING -> {
//
//            }
//          }
//        }
//
//      }

    addressViewModel.deliveredAddressMut.observe(viewLifecycleOwner) {
      it?.let {
        if (it.id == null) {
          addressViewModel.isThereAnyAddress.value = (false)
        } else {
          addressViewModel.isThereAnyAddress.value = (true)
          binding.lyAddress.item = it
        }
      }

    }
//    viewModel.selectedPaymentType.observe(viewLifecycleOwner) {
//      it?.let {
//        Logger.d(
//          "SelectedPaymentType",
//          "${viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)}"
//        )
//        when (it) {
//          PaymentType.WALLET_WITH_LESS -> {
//            if (!OrderWalletTopUpBottomSheet.inView && !OrderSummaryBottomSheet.inView) {
//              findNavController().navigate(R.id.orderWalletTopUpBottomSheet)
//            }
//
//          }
//          PaymentType.CARD -> {
//            if (cardAdapter.arrayList.isNullOrEmpty()) {
//              viewModel.selectedPaymentType.value = PaymentType.NEW
//            } else {
//              if (cardAdapter.rowIndex < 0) {
//                cardAdapter.selectedItem(0)
//              }
//            }
//          }
//          PaymentType.NEW -> {
////            binding.lyPaymentMethods.lyCards.isNewCardSelected = true
//          }
//
//          PaymentType.NEW_FOR_WALLET -> {
//            topViewModel.viaNewCard = true
////            binding.lyPaymentMethods.lyCardsForTopUp.isNewCardSelected = true
//          }
//          PaymentType.WALLET -> {
//            if (!OrderSummaryBottomSheet.inView) {
//              viewModel.calculateCompleteShipmentCharges(profileViewModel.userObjectMut.value)
//            }
//
//          }
//          else -> {
//            topViewModel.viaNewCard = false
//          }
//        }
//      }
//
//    }
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

  fun carddetail(card: String) {
    val sanitized = sanitizeEntry(card.toString())
    val cardType = CardUtils.getType(sanitized)

    Log.e(TAG, "carddetail:type${cardType.name.toString()} ")
    Log.e(TAG, "carddetail:viewModel.cardScheme${viewModel.cardScheme} ")

    when (viewModel.cardScheme.toLowerCase()) {
      "visa" -> activity?.let {
//        binding.imagetype = "{@drawable/ic_card}"
        Glide.with(it).load(R.drawable.ic_visa).into(binding.imgType)
        Glide.with(it).load(R.drawable.ic_visa).override(60, 20).into(binding.imgType)
      }
      "mastercard" -> activity?.let {
//        binding.payingwith.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.mastercard, 0);
        Log.e(TAG, "carddetail: mc")
        Glide.with(it).load(R.drawable.ic_master_card).into(binding.imgType)
//        Glide.with(it).load(R.drawable.ic_master_card).override(150, 25).into(binding.imgType)
      }
      "americanexpress" -> activity?.let {
//        Glide.with(it).load(R.drawable.ic_american_express).override(60, 15).into(binding.imgType)
        Glide.with(it).load(R.drawable.ic_american_express).into(binding.imgType)
      }
      else -> activity?.let {
//        type()
        Log.e(TAG, "carddetail: here")
        Glide.with(it).load(R.drawable.ic_card).into(binding.imgType)
      }
    }
  }

  fun type() {
    Log.e(TAG, "type: ")
    when (viewModel.selectedPaymentType.value) {
      PaymentType.NEW -> activity?.let {
        Glide.with(it).load(R.drawable.ic_card).into(binding.imgType)
//        Glide.with(it).load(R.drawable.ic_card).override(100, 20).into(binding.imgType)
      }
      PaymentType.CASH -> activity?.let {
//        binding.payingwith.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.mastercard, 0);
//        Glide.with(it).load(R.drawable.ic_cash).override(100, 20).into(binding.imgType)
        Glide.with(it).load(R.drawable.ic_cash).into(binding.imgType)
      }
      PaymentType.CARD -> activity?.let {
        carddetail(viewModel.cardno)
//        carddetail(viewModel.selectedCard?.cardData?.paymentInfo?.paymentDescription.toString())
      }
      PaymentType.WALLET -> activity?.let {
        Glide.with(it).load(R.drawable.ic_wallet_back).into(binding.imgType)
//        Glide.with(it).load(R.drawable.ic_wallet_back).override(70, 20).into(binding.imgType)
      }
      else -> activity?.let {
        Log.e(TAG, "type: eee")
        Glide.with(it).load(R.drawable.ic_card).into(binding.imgType)
//        Glide.with(it).load(R.drawable.ic_card).override(100, 20).into(binding.imgType)
      }
    }
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_payment
  }

  override fun permissionGranted(requestCode: Int) {
  }

  override fun changeAddress() {
    addressContract.launch(true)
  }

  private fun locationPermissionGranted() {
    addressContract.launch(true)
  }

  override fun onCardSelect(position: Int?, cardMainModel: CardMainModel, pg: Int) {
//    viewModel.selectedPaymentType.value = PaymentType.CARD
//    topViewModel.cardMainModel = cardMainModel
//    walletVieModel.selectedCard = cardMainModel
//    cardAdapter.selectedItem(position)
////    Logger.d("SelectedPaymentTyp","${viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)}")\\
//
//    viewModel.checkAndUpdateAccordingTopPreviousPaymentTyp()

  }

  override fun onDeleteCard(position: Int?, cardMainModel: CardMainModel) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

  override fun onClickProceed() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      triggerMultipleEvent()
      if ((!viewModel.appManager.persistenceManager.getLoggedInUser()?.name.isNullOrBlank()
            && !viewModel.appManager.persistenceManager.getLoggedInUser()?.email.isNullOrBlank())
      ) {
        if (addressViewModel.deliveredAddressMut.value != null && addressViewModel.deliveredAddressMut.value?.id != null) {
          findNavController().navigate(R.id.orderSummaryBottomSheet)
//          placeOrderRequest()

        } else {
          requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
        }
      } else {
        findNavController().navigate(R.id.nav_login_sheet)
      }
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }
  }

  override fun onClickPaymentpage() {
    viewModel.paymentview = true
    bindpaymentbottomview()
    findNavController().navigate(R.id.paymentPreviewBottomSheet)
  }

  private fun triggerMultipleEvent() {
    viewModel.offersManagers.getAllProductsWithQTY().let {
      viewModel.appManager.analyticsManagers.initiateCheckOutForListProducts(it)
    }
//    CoroutineScope(Dispatchers.IO).launch {
//      for (item in viewModel.offersManagers.getAllProductsWithQTY()) {
//        viewModel.appManager.analyticsManagers.initiateCheckout(item.productDetails)
//      }
//    }
  }

  private fun triggerMultiplecartEvent() {
    viewModel.offersManagers.getAllProductsWithQTY().let {
      viewModel.appManager.eventlistmainManager.eventTrakingCheckoutStatus(
        it, viewModel.totalAmount.value!!, viewModel.discountAmount.value!!,
        "checkout_completed"
      )

    }
//    CoroutineScope(Dispatchers.IO).launch {
//      for (item in viewModel.offersManagers.getAllProductsWithQTY()) {
//        viewModel.appManager.analyticsManagers.initiateCheckout(item.productDetails)
//      }
//    }
  }

  override fun onClickGotoOrders() {
    findNavController().navigate(R.id.nav_orders)
  }

  override fun onClickContinueShopping() {
    requireActivity().findNavController(R.id.fragment).navigate(R.id.nav_home)
  }

  override fun onClickInstantDetails() {
    viewModel.shipmentSelected = 1
    findNavController().navigate(R.id.shipmentProductList)
  }

  override fun onClickExpressDetails() {
    viewModel.shipmentSelected = 2
    findNavController().navigate(R.id.shipmentProductList)
  }

  override fun onClickGiftDetails() {
    viewModel.shipmentSelected = 3
    findNavController().navigate(R.id.shipmentProductList)
  }

  override fun onClickDeliveryInstant() {
    var title = ""
    var description = ""
    title = getString(R.string.instant_delivery)
    description = viewModel.appManager.storageManagers.config.instantInfo ?: ""
    AlertManager.showInfoAlertDialog(requireActivity(), title, description)
  }

  override fun onClickDeliveryExpress() {
    var title = ""
    var description = ""
    title = getString(R.string.express_delivery)
    description = viewModel.appManager.storageManagers.config.expressInfo ?: ""
    AlertManager.showInfoAlertDialog(requireActivity(), title, description)
  }

  override fun onClickLater() {
    Log.e(TAG, "onClickLater: ")
    binding.layoutDelivery.clDelivery.visibility = View.GONE
  }

  override fun onClickDeliveryIns(
    position: Int,
    delivertIns: DeliveryInstructions,
    add: Boolean
  ) {

    if (add) {
      appManager.storageManagers.deliveryinsselected.add(delivertIns)
    } else {
      appManager.storageManagers.deliveryinsselected.remove(delivertIns)
    }


    Log.e(TAG, "onClickDeliveryIns: ${appManager.storageManagers.deliveryinsselected}")
    deliveryInsAdapter.notifyDataSetChanged()
//    deliveryInsAdapter.setItemSelected(position)
  }

  override fun onClickChangeAddress() {
    addressContract.launch(true)
  }
}

