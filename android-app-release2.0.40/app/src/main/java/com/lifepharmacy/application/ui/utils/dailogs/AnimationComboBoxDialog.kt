package com.lifepharmacy.application.ui.utils.dailogs

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
import com.lifepharmacy.application.ui.orders.dailogs.ClickReturnProcessingDialog
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class AnimationComboBoxDialog : BaseDialogFragment<DailogAnimationComboBoxBinding>(),
  ClickReturnProcessingDialog, ClickAnimationComboActionDialog {

  var onClickAnimationComboActionDialog: ClickAnimationComboActionDialog? = null
  fun setAnimationDialogActionListener(dialogResult: ClickAnimationComboActionDialog) {
    onClickAnimationComboActionDialog = dialogResult
  }

  var animation: String = "unlock_offer.json"
  var offerTitle: String = "Buy 2 Get 1"
  var isFreeGift: Boolean = false

  companion object {
    const val TAG = "AnimationDialog"
    fun newInstance(offerTitle: String): AnimationComboBoxDialog {
      val dialog = AnimationComboBoxDialog()
      val bundle = Bundle()
      bundle.putString("offerTitle", offerTitle)
      dialog.arguments = bundle
      return dialog
    }

    fun getBundle(offerTitle: String, isFreeGift: Boolean = false): Bundle {
      val bundle = Bundle()

      bundle.putString("offerTitle", offerTitle)
      bundle.putBoolean("isFreeGift", isFreeGift)
      return bundle

    }
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
    isCancelable = false
    arguments?.let {
      if (it.containsKey("offerTitle")) {
        offerTitle = (it.getString("offerTitle") ?: return@let)
      }
      if (it.containsKey("isFreeGift")) {
        isFreeGift = it.getBoolean("isFreeGift")
      }
    }
    binding.offerTitle = offerTitle
    binding.isFreeGift = isFreeGift
    appearingAnimation()
  }


  private fun appearingAnimation() {
    binding.animationView.setAnimation(animation)
    binding.animationView.repeatCount = 0
    val rightToLeft = AnimationUtils.loadAnimation(
      requireActivity(),
      R.anim.center_appear
    )
    rightToLeft.setAnimationListener(object : Animation.AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {

      }

      override fun onAnimationEnd(animation: Animation?) {
        boxAnimation()
      }

      override fun onAnimationStart(animation: Animation?) {

      }

    })
    binding.clMain.startAnimation(rightToLeft)
    binding.clMain.visibility = View.VISIBLE

  }

  private fun boxAnimation() {
    binding.animationView.playAnimation()
    binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationStart(animation: Animator?) {
        Log.e("Animation:", "start")
      }

      override fun onAnimationEnd(animation: Animator?) {
        Log.e("Animation:", "end")
        //Your code for remove the fragment
//        dismiss()
      }

      override fun onAnimationCancel(animation: Animator?) {
        Log.e("Animation:", "cancel")
      }

      override fun onAnimationRepeat(animation: Animator?) {
        Log.e("Animation:", "repeat")
      }
    })
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
    return R.layout.dailog_animation_combo_box
  }

  override fun onClickView() {
    findNavController().navigateUp()
  }
//  override fun onActivityCreated(arg0: Bundle?) {
//    super.onActivityCreated(arg0)
//    dialog!!.window?.attributes?.windowAnimations = R.style.comboBoxAnimation
//  }

  override fun onClickLater() {
    findNavController().navigateUp()
  }

  override fun onClickClaim() {
    findNavController().navigateUp()
    requireActivity().findNavController(R.id.fragment).navigate(R.id.nav_offers)
  }

//  override fun onStart() {
//    super.onStart()
//
//    // safety check
//    if (dialog == null) {
//      return
//    }
//
//    // set the animations to use on showing and hiding the dialog
//    dialog!!.window!!.setWindowAnimations(
//      R.style.comboBoxAnimation
//    )
//    // alternative way of doing it
//    //getDialog().getWindow().getAttributes().
//    //    windowAnimations = R.style.dialog_animation_fade;
//
//    // ... other stuff you want to do in your onStart() method
//  }
//  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
////    val root = RelativeLayout(activity)
////    root.layoutParams = ViewGroup.LayoutParams(
////      ViewGroup.LayoutParams.MATCH_PARENT,
////      ViewGroup.LayoutParams.MATCH_PARENT
////    )
//    val dialog = Dialog(requireActivity(), R.style.comboBoxAnimation)
//    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
////    dialog.setContentView(root)
//    dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//    return dialog
//  }
}
