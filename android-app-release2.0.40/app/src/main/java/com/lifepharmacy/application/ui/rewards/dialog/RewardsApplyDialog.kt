package com.lifepharmacy.application.ui.rewards.dialog

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogAnimationRewardBinding
import com.lifepharmacy.application.ui.rewards.fragment.RewardsDetailFragment
import com.lifepharmacy.application.ui.rewards.viewmodels.RewardsViewModel
import com.lifepharmacy.application.ui.utils.dailogs.ClickAnimationComboActionDialog
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class RewardsApplyDialog : BaseDialogFragment<DailogAnimationRewardBinding>(),
  ClickRewardsDialog, ClickAnimationComboActionDialog {
  var ids: Int = 0
  var backAnimation: String = "bundle_completed.json"
  companion object {
    fun getDialogApplyBundle(
      id: Int
    ): Bundle {
      val bundle = Bundle()
      id?.let {
        bundle.putInt("id", id)
      }
      return bundle
    }
  }

  private val viewModel: RewardsViewModel by activityViewModels()
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCancelable = true
    arguments?.let {
      if (it.containsKey("id")) {
        ids = it.getInt("id")
        Log.e("ids --- ", "id: $ids")
      } else
        Log.e("id --- ", "onViewCreated: false")
    }
    initUI()
    observers()

  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogTheme)
  }

  private fun initUI() {
    binding.click = this
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    backAnimation()
//    binding.backAnimation.setAnimation("tick.json")
//    binding.backAnimation.playAnimation()
//    binding.animationView.setAnimation("tick.json")
//    binding.animationView.playAnimation()
  }

  private fun backAnimation() {
    binding.backAnimation.setAnimation(backAnimation)
    binding.backAnimation.repeatCount = 0
    binding.backAnimation.playAnimation()
    binding.backAnimation.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationStart(animation: Animator?) {
        Log.e("Animation:", "start")
      }

      override fun onAnimationEnd(animation: Animator?) {
        Log.e("Animation:", "end")
        //Your code for remove the fragment
//        findNavController().navigateUp()
      }

      override fun onAnimationCancel(animation: Animator?) {
        Log.e("Animation:", "cancel")
      }

      override fun onAnimationRepeat(animation: Animator?) {
        Log.e("Animation:", "repeat")
      }
    })
  }
  private fun observers() {
  }


  override fun getLayoutRes(): Int {
    return R.layout.dailog_animation_reward
  }

  override fun onClickContinue() {
    findNavController().navigateUp()
  }

  override fun onClickGotoOrders() {
//    TODO("Not yet implemented")
  }


  override fun onClickLater() {
    findNavController().navigateUp()
  }

  override fun onClickClaim() {
    findNavController().navigate(
      R.id.rewardsDetailFragment, RewardsDetailFragment.getRewardListingBundle(ids)
    )

  }

}
