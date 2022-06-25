package com.lifepharmacy.application.ui.home.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomWithLoading
import com.lifepharmacy.application.databinding.BottomSheetOrderSummaryBinding
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.*
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.orders.PlaceOrderRequest
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.model.payment.Urls
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.cart.adapter.CartShipmentProductAdapter
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import com.lifepharmacy.application.ui.pages.fragment.PageFragment
import com.lifepharmacy.application.ui.payment.WebViewPaymentActivity
import com.lifepharmacy.application.ui.wallet.viewmodels.TopViewModel
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.roundToInt
import com.google.android.play.core.splitinstall.d
import com.lifepharmacy.application.databinding.BottomSheetOrderRatingInappBinding
import com.lifepharmacy.application.model.inappmsg.Inappmsgrating
import com.lifepharmacy.application.model.orders.SubOrderDetail
import com.lifepharmacy.application.model.orders.SubOrderItem
import com.lifepharmacy.application.ui.cart.fragmets.ClickOrderSummarySheet
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet
import com.lifepharmacy.application.ui.orders.adapters.ClickOrderShipmentItem
import com.lifepharmacy.application.ui.orders.fragments.OrderDetailFragment
import com.lifepharmacy.application.ui.orders.viewmodels.OrderDetailViewModel
import com.lifepharmacy.application.ui.rating.RatingsViewModel
import com.lifepharmacy.application.ui.rating.fragments.MainRatingFragment
import com.lifepharmacy.application.utils.ObjectMapper


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class OrderRatingInappBottomSheet : BaseBottomWithLoading<BottomSheetOrderRatingInappBinding>(),
  ClickOrderSummarySheet, ClickOrderShipmentItem, ClickOrderRatingInapp {
  private val viewModel: RatingsViewModel by viewModels()
  private val viewModelOrderDetail: OrderDetailViewModel by activityViewModels()
  private val addressViewModel: AddressViewModel by activityViewModels()
  private val walletVieModel: WalletViewModel by activityViewModels()
  private val topViewModel: TopViewModel by activityViewModels()
  var rating: Float = 0.0F
  private lateinit var instantShipmentProducts: CartShipmentProductAdapter
  private lateinit var expressShipmentProducts: CartShipmentProductAdapter
  private lateinit var freeGiftsShipmentProducts: CartShipmentProductAdapter
  private var suborderID: String? = null

  private var animator: ValueAnimator? = null

  private var isTopUp = false
  private var isAnimationCanceled = false
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCancelable = false
    initLayout()
    observers()

  }

  companion object {
    var inView = false
    const val TAG = "AnimationDialog"

    fun getBundle(
      sub_order_id: String
    ): Bundle {
      val bundle = Bundle()
      sub_order_id?.let {
        bundle.putString("suborderid", sub_order_id)
//        bundle.putString("imageTitle", data.inapp_data.imageUrl)
      }
      return bundle
    }

  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    inView = true
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog: Dialog = super.onCreateDialog(savedInstanceState)
    arguments?.let {
      suborderID = it.getString("suborderid")
    }
    dialog.setOnShowListener(DialogInterface.OnShowListener { dialogInterface ->
      val bottomSheetDialog = dialogInterface as BottomSheetDialog
//      setupFullHeight(bottomSheetDialog)
      val bottomSheet =
        bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
      val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
      behavior.state = BottomSheetBehavior.STATE_EXPANDED
    })
    return dialog
  }

  private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
    val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
    val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
    val layoutParams = bottomSheet.layoutParams
    val windowHeight = getWindowHeight()
    if (layoutParams != null) {
      layoutParams.height = (windowHeight * 0.85).roundToInt()
    }
    bottomSheet.layoutParams = layoutParams
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
  }

  private fun getWindowHeight(): Int {
    // Calculate window height for fullscreen use
    val displayMetrics = DisplayMetrics()
    requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
  }

  private fun initLayout() {

    binding.click = this
    binding.viewModel = viewModel
    binding.addressViewModel = addressViewModel
    binding.lifecycleOwner = this
    binding.tvOrderId.text = "  ORDER #${viewModel.appManager.persistenceManager.subordernumber}"

//    viewModel.showLaodingOrderProgressBar.value = false


  }

  override fun onDismiss(dialog: DialogInterface) {
    cancelAnimation()
    super.onDismiss(dialog)
    inView = false
  }

  override fun onCancel(dialog: DialogInterface) {
    cancelAnimation()
    super.onCancel(dialog)
    inView = false
  }


  private fun observers() {
//    binding.simpleRatingBar.rating = rating ?: 0.0F
//    if (rating != null && rating != 0.0F) {
//      binding.simpleRatingBar.rating = rating ?: 0.0F
//      //CommentedForTesting
////      binding.simpleRatingBar.isClickable = false;
////      binding.simpleRatingBar.isScrollable = false;
////      binding.simpleRatingBar.isEnabled = false
//      ////END////
//    } else {
//      binding.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
//   onClickRating(viewModel.appManager.persistenceManager.suborderidraing, rating)
//      }
//    }
    suborderID?.let { suborderid ->
      viewModel.getSubOrderDetail(suborderid).observe(viewLifecycleOwner) {
        it?.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
              Log.e(TAG, "observers: inapprating")
//            onClickSubOrderViewDetail(it.data.data.subOrders, "suborder id")
              it.data?.data?.let { it1 -> handleSubOrderDetailResult(it1) }

//            hideLoading()


            }
            Result.Status.ERROR -> {
//            it.message?.let { it1 -> AlertManager.showErrorMessage(requireActivity(), it1) }
//            hideLoading()
            }
            Result.Status.LOADING -> {
//            showLoading()
            }

          }
        }
      }
    }

    binding.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
      onClickRatings(
        viewModel.appManager.persistenceManager.suborderidraing,
        viewModel.appManager.persistenceManager.subordernumber,
        rating
      )
      Log.e(TAG, "observers: Shifa rating")
    }
  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_order_rating_inapp
  }

  override fun permissionGranted(requestCode: Int) {
    if (requestCode == ConstantsUtil.PERMISSION_READ_SMS) {
    }
  }


  override fun getLoadingLayout(): ConstraintLayout {
    return binding.llLoading.clLoading
  }

  private fun animateProgressBar() {
//    animator = ValueAnimator.ofInt(0, binding.pbLoading.max)
    animator?.duration = 3000
//    animator?.addUpdateListener { animation ->
//      binding.pbLoading.progress = animation.animatedValue as Int
//    }
    animator?.addListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator?) {
        super.onAnimationEnd(animation)
        // start your activity here
        if (!isAnimationCanceled) {

        }
      }
    })
    animator?.start()
  }

  private fun cancelAnimation() {
//    viewModel.showLaodingOrderProgressBar.value = false
    isAnimationCanceled = true
    animator?.cancel()
  }

  override fun onClickProceed() {


  }

  override fun onClickTerms() {

  }

  override fun onClickCancel() {
    cancelAnimation()

  }

  override fun onClickClose() {
    findNavController().navigateUp()
  }

  private fun handleSubOrderDetailResult(subOrder: SubOrderDetail) {
//    var temsubOrder = ObjectMapper.getSubOrderMainFloatToRatingRating(subOrder)
//    binding.tollBar.orderId = subOrder.id.toString()
//    binding.tollBar.date = subOrder.createdAt
    if (!subOrder.subOrders.isNullOrEmpty()) {
      viewModel.selectedSubOrderItem.value = subOrder.subOrders?.firstOrNull()?.let {
        ObjectMapper.getSingleSubOrderItemFloatToJustItemRatRating(
          it
        )
      }
    }

  }

  override fun onClickSubOrderViewDetail(orderModel: SubOrderItem, orderId: String) {
    viewModelOrderDetail.selectedSubOrderItem.value = orderModel
    findNavController().navigate(
      R.id.nav_order_details,
      OrderDetailFragment.getOrderDetailBundle(orderModel.id.toString(), orderId)
    )
  }

  override fun onClickRating(orderModel: SubOrderItem, orderId: String, rating: Float) {

  }

  fun onClickRatings(orderId: String, subordernumber: String, rating: Float) {
    Log.e(TAG, "onClickRating: rating")
    findNavController().navigate(
      R.id.nav_rating,
      MainRatingFragment.getBundle(orderId, subordernumber, rating)
    )

  }

  override fun onClickSubmit() {
    Log.e(TAG, "onClickSubmit: ")
    onClickRatings(
      viewModel.appManager.persistenceManager.suborderidraing,
      viewModel.appManager.persistenceManager.subordernumber,
      binding.simpleRatingBar.rating
    )
  }

}