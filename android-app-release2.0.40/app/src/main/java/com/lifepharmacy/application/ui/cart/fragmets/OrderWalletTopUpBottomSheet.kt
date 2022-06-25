package com.lifepharmacy.application.ui.cart.fragmets

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.facebook.appevents.AppEventsConstants
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.base.BaseBottomWithLoading
//import com.lifepharmacy.application.databinding.BottomSheetUserDetailsBinding
import com.lifepharmacy.application.enums.UserSignUpState
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.model.User
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.cart.viewmodel.UserDetailBottomViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.KeyboardUtils
import com.lifepharmacy.application.broadCastReciever.ReadSms
import com.lifepharmacy.application.broadCastReciever.SmsBroadcastReceiver
import com.lifepharmacy.application.databinding.BottomSheetOrderSummaryBinding
import com.lifepharmacy.application.databinding.BottomSheetOrderWalletTopBinding
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.orders.PlaceOrderRequest
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.model.payment.Urls
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.card.CardsAdapter
import com.lifepharmacy.application.ui.card.ClickCard
import com.lifepharmacy.application.ui.cart.adapter.CartShipmentProductAdapter
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import com.lifepharmacy.application.ui.pages.fragment.PageFragment
import com.lifepharmacy.application.ui.payment.WebViewPaymentActivity
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog.Companion.TAG
import com.lifepharmacy.application.ui.wallet.viewmodels.TopViewModel
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.FaceBookAnalyticsUtilUtil
import com.lifepharmacy.application.utils.universal.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ibrahimsn.lib.PhoneNumberKit
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class OrderWalletTopUpBottomSheet : BaseBottomUpSheet<BottomSheetOrderWalletTopBinding>(),
  ClickOrderWalletTopUpSheet, ClickCard {
  private val viewModel: CartViewModel by activityViewModels()
  private val addressViewModel: AddressViewModel by activityViewModels()
  private val walletVieModel: WalletViewModel by activityViewModels()
  private val topViewModel: TopViewModel by activityViewModels()

  private var isTopUp = false
  private lateinit var cardAdapter: CardsAdapter
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCancelable = true
    initLayout()
    observers()
  }

  companion object {
    var inView = false
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    inView = true
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    inView = false
  }

  override fun onCancel(dialog: DialogInterface) {
    super.onCancel(dialog)
    inView = false
  }

  private fun initLayout() {
    binding.click = this
    binding.viewModel = viewModel
    binding.addressViewModel = addressViewModel
    binding.lifecycleOwner = this
    initCardRV()
//    if (cardAdapter.arrayList.isNullOrEmpty()) {
//      viewModel.selectedPaymentType.value = PaymentType.NEW_FOR_WALLET
//    } else {
//      if (cardAdapter.rowIndex < 0) {
//        cardAdapter.selectedItem(0)
//      }
//      topViewModel.viaNewCard = false
////              binding.lyPaymentMethods.lyCardsForTopUp.isNewCardSelected = false
////              binding.lyPaymentMethods.lyCards.isNewCardSelected = false
//    }
  }

  private fun initCardRV() {
    cardAdapter = CardsAdapter(requireActivity(), this)
    binding.rvCards.adapter = cardAdapter
//    binding.lyPaymentMethods.lyCardsForTopUp.rvCards.adapter = cardAdapter

  }

  private fun observers() {
    binding.rgNewCard.setOnCheckedChangeListener { _, b ->
      if (b) {
        Log.e(TAG, "observers: newcard")
        topViewModel.viaNewCard = true
        viewModel.selectedPaymentType.value = PaymentType.NEW_FOR_WALLET
        cardAdapter.selectedItem(-1)
        if (viewModel.appManager.persistenceManager.defaultpg == 2) {
          //adding check for new for wallet selection
          viewModel.selectedtypenewwallet = true
          viewModel.selectedtypenew = true

          viewModel.selectedPaymentType.value = PaymentType.NEW_FOR_WALLET
          topViewModel.selectedPaymentType.value = PaymentType.NEW_FOR_WALLET
          findNavController().navigate(R.id.cardDetailsBottomSheet)
        }
      }
    }
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
                  cardAdapter.selectedItem(0)
                  walletVieModel.selectedCard = it.data[0]
                  topViewModel.cardMainModel = it.data[0]
//                  viewModel.selectedPaymentType.value = PaymentType.WALLET_WITH_LESS
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
  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_order_wallet_top
  }

  override fun permissionGranted(requestCode: Int) {
    if (requestCode == ConstantsUtil.PERMISSION_READ_SMS) {
    }
  }

  override fun onClickProceed() {

//    if (viewModel.appManager.persistenceManager.defaultpg == 2 &&
//      viewModel.selectedPaymentType.value == PaymentType.NEW_FOR_WALLET
//    ) {
//
////      viewModel.selectedPaymentType.value = PaymentType.NEW_FOR_WALLET
////          topViewModel.selectedPaymentType.value = PaymentType.NEW_FOR_WALLET
//      findNavController().navigate(R.id.cardDetailsBottomSheet)
//    }
    inView = false
    findNavController().navigateUp()
  }


  override fun onCardSelect(position: Int?, cardMainModel: CardMainModel, pg: Int) {
    topViewModel.viaNewCard = false
    topViewModel.card_pg = pg
    topViewModel.cardMainModel = cardMainModel
    walletVieModel.selectedCard = cardMainModel
    cardAdapter.selectedItem(position)
    viewModel.selectedPaymentType.value = PaymentType.WALLET_WITH_LESS
  }


  override fun onDeleteCard(position: Int?, cardMainModel: CardMainModel) {

  }

}