package com.lifepharmacy.application.ui.products.fragment

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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogAnimationComboBoxBinding
import com.lifepharmacy.application.databinding.DailogAnimationOfferCompleteBinding
import com.lifepharmacy.application.databinding.DailogAnimationOfferIncompleteCouponBinding
import com.lifepharmacy.application.databinding.DailogAnimationShipmentTypeChangeBinding
import com.lifepharmacy.application.ui.orders.dailogs.ClickReturnProcessingDialog
import com.lifepharmacy.application.ui.utils.dailogs.ClickAnimationComboActionDialog
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class AnimationShipmentTypeChangedDialog :
  BaseDialogFragment<DailogAnimationShipmentTypeChangeBinding>(),
  ClickReturnProcessingDialog, ClickAnimationComboActionDialog {

  var onClickAnimationComboActionDialog: ClickAnimationComboActionDialog? = null
  fun setAnimationDialogActionListener(dialogResult: ClickAnimationComboActionDialog) {
    onClickAnimationComboActionDialog = dialogResult
  }

  var animation: String = "offer_tick_animation.json"
  var backAnimation: String = "bundle_completed.json"
  var amountTitle: String = "This item now will be delivered as Standard Shipping in 24-48hrs"

  companion object {
    const val TAG = "AnimationDialog"
    fun newInstance(
      offerTitle: String
    ): AnimationShipmentTypeChangedDialog {
      val dialog = AnimationShipmentTypeChangedDialog()
      val bundle = Bundle()
      offerTitle?.let {
        bundle.putString("offerTitle", offerTitle)
      }
      dialog.arguments = bundle
      return dialog
    }

    fun getBundle(offerTitle: String): Bundle {
      val bundle = Bundle()

      bundle.putString("amountTitle", offerTitle)
      return bundle

    }
  }
//  companion object {
//    const val TAG = "AnimationDialog"
//    fun newInstance(offerTitle: String): AnimationOfferCompleteCouponDialog {
//      val dialog = AnimationOfferCompleteCouponDialog()
//      val bundle = Bundle()
//      bundle.putString("amountTitle", offerTitle)
//      dialog.arguments = bundle
//      return dialog
//    }
//
//    fun getBundle(offerTitle: String): Bundle {
//      val bundle = Bundle()
//
//      bundle.putString("amountTitle", offerTitle)
//      return bundle
//
//    }
//  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
    isCancelable = false
    arguments?.let {
      if (it.containsKey("amountTitle")) {
        amountTitle = it.getString("amountTitle")!!
      }
    }
    binding.amountTitle = amountTitle
    backAnimation()
    tickCheckAnimation()
  }

  private fun backAnimation() {
//    binding.backAnimation.setAnimation(backAnimation)
//    binding.backAnimation.repeatCount = 0
//    binding.backAnimation.playAnimation()
//    binding.backAnimation.addAnimatorListener(object : Animator.AnimatorListener {
//      override fun onAnimationStart(animation: Animator?) {
//        Log.e("Animation:", "start")
//      }
//
//      override fun onAnimationEnd(animation: Animator?) {
//        Log.e("Animation:", "end")
//        //Your code for remove the fragment
//        findNavController().navigateUp()
//      }
//
//      override fun onAnimationCancel(animation: Animator?) {
//        Log.e("Animation:", "cancel")
//      }
//
//      override fun onAnimationRepeat(animation: Animator?) {
//        Log.e("Animation:", "repeat")
//      }
//    })
  }


  private fun tickCheckAnimation() {
//    binding.animationView.setAnimation(animation)
//    binding.animationView.repeatCount = 0
//    binding.animationView.playAnimation()
//    binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
//      override fun onAnimationStart(animation: Animator?) {
//        Log.e("Animation:", "start")
//      }
//
//      override fun onAnimationEnd(animation: Animator?) {
//        Log.e("Animation:", "end")
//
//      }
//
//      override fun onAnimationCancel(animation: Animator?) {
//        Log.e("Animation:", "cancel")
//      }
//
//      override fun onAnimationRepeat(animation: Animator?) {
//        Log.e("Animation:", "repeat")
//      }
//    })
  }

  //
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_TITLE, R.style.FullScreenTransparentDialogTheme)
  }

  private fun initUI() {
    binding.click = this
  }

  override fun getLayoutRes(): Int {
    return R.layout.dailog_animation_shipment_type_change
  }

  override fun onClickView() {
    findNavController().navigateUp()
  }

  override fun onClickLater() {
    findNavController().navigateUp()
  }

  override fun onClickClaim() {
    findNavController().navigateUp()
    requireActivity().findNavController(R.id.fragment).navigate(R.id.nav_offers)
  }
}
