package com.lifepharmacy.application.ui.cart.dailog

import android.animation.Animator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpRatioScreenSheet
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogAnimationComboBoxBinding
import com.lifepharmacy.application.databinding.DailogOfferIncompleteBinding
import com.lifepharmacy.application.model.cart.CartResponseModel
import com.lifepharmacy.application.model.cart.CouponModel
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import com.lifepharmacy.application.ui.orders.dailogs.ClickReturnProcessingDialog
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.utils.dailogs.ClickAnimationComboActionDialog
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class OfferIncompleteDialog : BaseBottomUpRatioScreenSheet<DailogOfferIncompleteBinding>(1.0),
  ClickOfferIncompleteDialog {

  private val viewModel: CartViewModel by activityViewModels()
  private val vieModelProfile: ProfileViewModel by activityViewModels()
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
    isCancelable = false
  }


  //
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_TITLE, R.style.FullScreenTransparentDialogTheme)
  }

  private fun initUI() {
    binding.click = this
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
  }

  override fun getLayoutRes(): Int {
    return R.layout.dailog_offer_incomplete
  }

  override fun onClickClaimOffer() {
    findNavController().navigateUp()

  }

  override fun onClickWithoutOffer() {
    updateServerCart()
  }

  override fun permissionGranted(requestCode: Int) {

  }

  private fun updateServerCart() {
    if (viewModel.appManager.persistenceManager.isThereCart()) {
      updateCartObserver()
    } else {
      createCartObserver()
    }
  }

  private fun updateCartObserver() {
    viewModel.updateCart().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.data?.let { it1 -> handleCartFromServerResponse(it1) }
          }
          Result.Status.ERROR -> {
            viewModel.isUpdatingCart.value = (true)
            createCartObserver()
          }
          Result.Status.LOADING -> {
            viewModel.isUpdatingCart.value = (true)
          }
        }
      }
    })
  }

  private fun createCartObserver() {
    viewModel.createCart().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.data?.let { it1 -> handleCartFromServerResponse(it1) }

          }
          Result.Status.ERROR -> {
            viewModel.isUpdatingCart.value = (false)
//            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
//              .show()
          }
          Result.Status.LOADING -> {
            viewModel.isUpdatingCart.value = (true)
          }
        }
      }
    })
  }

  private fun handleCartFromServerResponse(result: GeneralResponseModel<CartResponseModel>) {
    viewModel.isUpdatingCart.value = (false)
    viewModel.offersManagers.freeGiftProductArrayMut
    result.data?.items?.let { it1 ->
      viewModel.offersManagers.updateCartFromServer(
        requireActivity(),
        it1,
        result.data
      )
    }
    appManager.persistenceManager.saveCartID(result.data?.id.toString())
    applyCoupon(result.data?.couponModel)
    proceedToPayment()

  }

  private fun applyCoupon(couponModel: CouponModel?) {
    if (couponModel != null && couponModel.couponValue != 0.0 && !couponModel.couponCode.isNullOrEmpty()) {
      viewModel.isCouponApplied.value = true
      viewModel.couponModel.value = couponModel
      viewModel.isUpdatingCart.value = (false)
      viewModel.doAmountCalculations()
    }
  }

  private fun proceedToPayment() {

    viewModel.calculateCompleteShipmentCharges(vieModelProfile.userObjectMut.value)
//    viewModel.cartManager.setCheckedItems()
//    viewModel.cartManager.getCalculateCounts()
//    viewModel.cartManager.changeDeliveryOption(false)
    findNavController().navigate(R.id.paymentFragment)
  }
}
