package com.lifepharmacy.application.ui.outofstockorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentOutOfStockOrderBinding
import com.lifepharmacy.application.enums.AddressChanged
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.paymentScreenOpen
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressSelectionActivity
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.address.dailog.ClickLayoutAddress
import com.lifepharmacy.application.ui.card.CardsAdapter
import com.lifepharmacy.application.ui.card.ClickCard
import com.lifepharmacy.application.ui.cart.fragmets.OrderSummaryBottomSheet
import com.lifepharmacy.application.ui.cart.fragmets.OrderWalletTopUpBottomSheet
import com.lifepharmacy.application.ui.pages.fragment.PageFragment
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.wallet.viewmodels.TopViewModel
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class OrderOutOFStockFragment : BaseFragment<FragmentOutOfStockOrderBinding>(),
  ClickCard, ClickLayoutAddress, ClickTool, ClickOrderOutOfStockFragment {
  private val viewModel: OutOfStockOrderViewModel by activityViewModels()
  private val addressViewModel: AddressViewModel by activityViewModels()
  private val walletVieModel: WalletViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  private val topViewModel: TopViewModel by activityViewModels()
  private lateinit var cardAdapter: CardsAdapter
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
    mView = super.onCreateView(inflater, container, savedInstanceState)
    initUI()
    observers()

    return mView
  }

  private fun initUI() {
    viewModel.setCartModel()
    binding.addressViewModel = addressViewModel
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.click = this
    binding.tollBar.click = this
    binding.tollBar.tvToolbarTitle.text = viewModel.appManager.storageManagers.config.backOrder
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

    viewModel.doAmountCalculations()

  }

  private fun bindPayment() {
    viewModel.paymentDetailsTitle.value = getString(R.string.price_details)
    viewModel.paymentDetailsSubTotalTitle.value = getString(R.string.subtotal)
    binding.profileViewModel = profileViewModel
    binding.viewModel = viewModel
    binding.topViewModel = topViewModel
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
    viewModel.calculateCompleteShipmentCharges()

  }

  private fun handlePaymentMethodSelection() {
    binding.rgCash.setOnCheckedChangeListener { _, b ->
      if (b) {
        viewModel.selectedPaymentType.value = PaymentType.CASH
        cardAdapter.selectedItem(-1)
        viewModel.calculateCompleteShipmentCharges()

      }
    }
    binding.rgNewCard.setOnCheckedChangeListener { _, b ->
      if (b) {
        viewModel.selectedPaymentType.value = PaymentType.NEW
        cardAdapter.selectedItem(-1)
        viewModel.calculateCompleteShipmentCharges()

      }
    }
    binding.rgWallet.setOnCheckedChangeListener { _, b ->
      if (b) {
        viewModel.selectedPaymentType.value = PaymentType.WALLET
        cardAdapter.selectedItem(-1)
        viewModel.calculateCompleteShipmentCharges()
      }
    }
  }

  private fun initCardRV() {
    cardAdapter = CardsAdapter(requireActivity(), this)
    binding.rvCards.adapter = cardAdapter

  }


  private fun observers() {

    viewModel.outOfStockCartItem.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
      it?.let {
        binding
      }
    })

    walletVieModel.cardsDataMut
      .observe(viewLifecycleOwner, {
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

      })

    addressViewModel.deliveredAddressMut.observe(viewLifecycleOwner, {
      it?.let {
        if (it.id == null) {
          addressViewModel.isThereAnyAddress.value = (false)
        } else {
          addressViewModel.isThereAnyAddress.value = (true)
        }
      }

    })
    viewModel.selectedPaymentType.observe(viewLifecycleOwner, {
      it?.let {
        Logger.d(
          "SelectedPaymentTyp",
          "${viewModel.selectedPaymentType.value?.name?.toLowerCase(Locale.ROOT)}"
        )
        when (it) {
          PaymentType.WALLET_WITH_LESS -> {
            if (!OrderWalletTopUpBottomSheet.inView && !OrderSummaryBottomSheet.inView) {
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
              viewModel.calculateCompleteShipmentCharges()
            }

          }
          else -> {
            topViewModel.viaNewCard = false
          }
        }
      }

    })
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
    return R.layout.fragment_out_of_stock_order
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
    binding.tvTerms.text = html
  }

  override fun onCardSelect(position: Int?, cardMainModel:CardMainModel,pg:Int) {
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

  override fun onClickProceed() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      if ((!viewModel.appManager.persistenceManager.getLoggedInUser()?.name.isNullOrBlank()
            && !viewModel.appManager.persistenceManager.getLoggedInUser()?.email.isNullOrBlank())
      ) {
        if (addressViewModel.deliveredAddressMut.value != null && addressViewModel.deliveredAddressMut.value?.id != null) {
          placeOrder()

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

  override fun onClickPlus() {
    viewModel.plus()
  }

  override fun onClickMinus() {
    viewModel.outOfStockCartItem.value?.qty?.let {
      if (it == 1) {
        MaterialAlertDialogBuilder(
          requireContext(),
          R.style.ThemeOverlay_App_MaterialAlertDialog
        )
          .setTitle(getString(R.string.remove_product_title))
          .setMessage(getString(R.string.remove_product_descr))
          .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
          }
          .setPositiveButton(getString(R.string.remove)) { dialog, which ->
            findNavController().navigateUp()
          }
          .show()
      } else {
        viewModel.minus()
      }
    }
  }

  override fun onClickChangeAddress() {
    addressContract.launch(true)
  }

  private fun placeOrder() {
    findNavController().navigate(R.id.orderSummaryOutOfStockBottomSheet)
  }


}

