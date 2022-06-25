package com.lifepharmacy.application.ui.cart.fragmets

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpRatioScreenSheet
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.databinding.BottomSheetPaymentPreviewBinding
import com.lifepharmacy.application.enums.AddressChanged
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.initiateCheckOutForListProducts
import com.lifepharmacy.application.managers.analytics.withOutPrescriptionScreenOpen
import com.lifepharmacy.application.model.cart.DeliveryInstructions
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressSelectionActivity
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.card.CardsAdapter
import com.lifepharmacy.application.ui.card.ClickCard
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog.Companion.TAG
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.wallet.viewmodels.TopViewModel
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.utils.IntentHandler
import com.lifepharmacy.application.utils.IntentStarter
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.CreditCardFormatTextWatcher
import com.lifepharmacy.application.utils.universal.Logger
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


/**
 * Created by Shifa
 */
@AndroidEntryPoint
class PaymentPreviewBottomSheet :
  BaseBottomUpSheet<BottomSheetPaymentPreviewBinding>(),
  ClickTool, ClickToolNew, ClickCard, ClickPaymentFragment {
  //  BaseBottomUpRatioScreenSheet
  private val viewModel: CartViewModel by activityViewModels()
  private val addressViewModel: AddressViewModel by activityViewModels()
  private val walletVieModel: WalletViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  private val topViewModel: TopViewModel by activityViewModels()
  private lateinit var cardAdapter: CardsAdapter
  private val tv: CreditCardFormatTextWatcher? = null
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
//    OrderSummaryBottomSheet.inView = true
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      findNavController().navigate(R.id.paymentFragment)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
//    setStyle(STYLE_NORMAL, R.style.CustomBottomSheetStyle);
    isCancelable = false
    initLayout()
    observers()
    reselectionCartObserver()
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

  private val addressContract =
    registerForActivityResult(AddressSelectionActivity.Contract()) { result ->
      result?.address?.let {
        addressViewModel.deliveredAddressMut.value = it
        addressViewModel.addressSelection = AddressSelection.NON
      }
    }

  private fun initLayout() {
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
//    binding.click = this
//    bindCardNumber()
//    bindCardyear()
//    bindCvv()
    bindPayment()
    initCardRV()
    handlePaymentMethodSelection()
    viewModel.Bindpaymenttype()


  }

  private fun locationPermissionGranted() {
    addressContract.launch(true)
  }

  private fun bindPayment() {
    binding.lyPaymentMethods.profileViewModel = profileViewModel
    binding.lyPaymentMethods.mViewModel = viewModel
    binding.lyPaymentMethods.topViewModel = topViewModel
    binding.lyPaymentMethods.lifecycleOwner = this
    binding.clicktop = this
    binding.clicktup = this
    binding.tvToolbarTitle.text = getString(R.string.select_payment_method)

    if (!viewModel.selectedtypenew) {
      viewModel.bindDefaultPaymentMethod()
      viewModel.setLastPaymentType()
    }
  }

  private fun initCardRV() {
    cardAdapter = CardsAdapter(requireActivity(), this)
    binding.lyPaymentMethods.rvCards.adapter = cardAdapter

  }

  private fun handlePaymentMethodSelection() {
    binding.lyPaymentMethods.rgCash.setOnCheckedChangeListener { _, b ->
      if (b) {
        viewModel.selectedtypenew = false
        viewModel.selectedPaymentType.value = PaymentType.CASH
        cardAdapter.selectedItem(-1)
        viewModel.calculateCompleteShipmentCharges(profileViewModel.userObjectMut.value)
//        binding.lyDeliveryOptions.swLeaveAtDoor.isChecked = false  ----->check this case

      }
    }
    binding.lyPaymentMethods.rgNewCard.setOnCheckedChangeListener { _, b ->
      if (b) {
        viewModel.selectedtypenew = true
        viewModel.selectedPaymentType.value = PaymentType.NEW
        cardAdapter.selectedItem(-1)
        viewModel.calculateCompleteShipmentCharges(profileViewModel.userObjectMut.value)
        if (viewModel.appManager.persistenceManager.defaultpg == 2) {
          findNavController().navigate(R.id.cardDetailsBottomSheet)
        }
      }
    }
    binding.lyPaymentMethods.rgWallet.setOnCheckedChangeListener { _, b ->
      if (b) {
        viewModel.selectedtypenew = false
        viewModel.selectedPaymentType.value = PaymentType.WALLET
        cardAdapter.selectedItem(-1)
        viewModel.calculateCompleteShipmentCharges(profileViewModel.userObjectMut.value)
      }
    }
    viewModel.Bindpaymenttype()
  }

  private fun observers() {
//    observerCartSelectedItems()
    walletVieModel.cardsDataMut
      .observe(viewLifecycleOwner) {
        it?.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
              cardAdapter.setDataChanged(it.data)
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
//          binding.lyAddress.item = it
        }
      }

    }
    viewModel.selectedPaymentType.observe(viewLifecycleOwner) {
      it?.let {
        Logger.d(
          "SelectedPaymentType",
          "${viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)}"
        )
        when (it) {
          PaymentType.WALLET_WITH_LESS -> {
            if (!OrderWalletTopUpBottomSheet.inView && !OrderSummaryBottomSheet.inView && !CardDetailsBottomSheet.inView) {
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
            if (!OrderSummaryBottomSheet.inView) {
              viewModel.calculateCompleteShipmentCharges(profileViewModel.userObjectMut.value)
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
    return R.layout.bottom_sheet_payment_preview
  }

  override fun permissionGranted(requestCode: Int) {

  }

//  override fun onClickSelectContinue() {
//    viewModel.paymentview = true
//    Log.e(TAG, "onClickSelectContinue: ")
//    findNavController().navigateUp()
//  }

  //  override fun onClickProceed() {
//    viewModel.paymentview = true
//    findNavController().navigateUp()
//  }
  private fun triggerMultipleEvent() {
    viewModel.offersManagers.getAllProductsWithQTY().let {
      viewModel.appManager.analyticsManagers.initiateCheckOutForListProducts(it)
    }

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
    Log.e(TAG, "onClickPaymentpage: ")
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

  override fun onClickGotoOrders() {

  }

  override fun onClickContinueShopping() {

  }

  override fun onClickInstantDetails() {
  }

  override fun onClickExpressDetails() {

  }

  override fun onClickGiftDetails() {

  }

  override fun onClickDeliveryInstant() {

  }

  override fun onClickDeliveryExpress() {

  }

  override fun onClickLater() {

  }

  override fun onClickDeliveryIns(position: Int, delivertIns: DeliveryInstructions, add: Boolean) {

  }


  override fun onCardSelect(position: Int?, cardMainModel: CardMainModel, pg: Int) {
    Log.e(
      TAG,
      "onCardSelect: ${cardMainModel.cardData?.paymentInfo?.paymentDescription.toString()}"
    )
    var card = cardMainModel.cardData?.paymentInfo?.paymentDescription.toString()
    Log.e(TAG, "onCardSelect: ${card.length}")
    if (!card.isNullOrEmpty() && card.length == 19) {
      viewModel.cardno = card.replace(" ", "")

      viewModel.card4dig = card.subSequence(15, 19).toString()
      Log.e(TAG, "onCardSelect: ${viewModel.card4dig}")
    }
    viewModel.paymentview = true
    appManager.persistenceManager.cardpg = pg
    viewModel.card_pg = pg
    viewModel.selectedPaymentType.value = PaymentType.CARD
    topViewModel.cardMainModel = cardMainModel
    viewModel.selectedCard = cardMainModel
    walletVieModel.selectedCard = cardMainModel
    cardAdapter.selectedItem(position)
//    Logger.d("SelectedPaymentTyp","${viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)}")\\

    viewModel.checkAndUpdateAccordingTopPreviousPaymentTyp()
    viewModel.Bindpaymenttype()
  }


  override fun onDeleteCard(position: Int?, cardMainModel: CardMainModel) {

  }

  override fun onClickBack() {
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

  override fun onClickUp() {
//    findNavController().navigateUp()
    findNavController().navigate(R.id.paymentFragment)
  }


}