package com.lifepharmacy.application.ui.utils.dailogs

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogLoadingBinding
import com.lifepharmacy.application.ui.orders.dailogs.ClickReturnProcessingDialog
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class AnimationDialog : BaseDialogFragment<DailogLoadingBinding>(),
  ClickReturnProcessingDialog {
  var animation:String = "loading_main.json"

  companion object {
    const val TAG = "AnimationDialog"
    fun newInstance(string: String): AnimationDialog {
      val dialog = AnimationDialog()
      val bundle = Bundle()
      bundle.putString("animation", string)
      dialog.arguments = bundle
      return dialog
    }
  }




  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
    isCancelable = false
    arguments?.let {
     if (it.containsKey("animation")) {
       animation= it.getString("animation")!!
     }
    }
    binding.animationView.setAnimation(animation)
    binding.animationView.repeatCount = 1
    binding.animationView.playAnimation()
    binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
      override  fun onAnimationStart(animation: Animator?) {
        Log.e("Animation:", "start")
      }

      override fun onAnimationEnd(animation: Animator?) {
        Log.e("Animation:", "end")
        //Your code for remove the fragment
        dismiss()
      }

      override fun onAnimationCancel(animation: Animator?) {
        Log.e("Animation:", "cancel")
      }

      override fun onAnimationRepeat(animation: Animator?) {
        Log.e("Animation:", "repeat")
      }
    })
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_TITLE, R.style.FullScreenTransparentDialogTheme)

  }

  private fun initUI() {
    binding.click = this

  }

  override fun getLayoutRes(): Int {
    return R.layout.dailog_loading
  }

  override fun onClickView() {
    findNavController().navigateUp()
  }

}
