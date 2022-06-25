package com.lifepharmacy.application.ui.rewards.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentRewardsBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.*
//import com.lifepharmacy.application.managers.analytics.RewardsListScreenOpen
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.dashboard.adapter.HomeProductAdapter
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.rewards.adapters.ClickItemRewards
import com.lifepharmacy.application.ui.rewards.adapters.RewardsAdapter
import com.lifepharmacy.application.ui.rewards.dialog.RewardsApplyDialog
import com.lifepharmacy.application.ui.rewards.viewmodels.RewardsViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.universal.RecyclerPagingListener
import com.lifepharmacy.application.utils.universal.RecyclerViewPagingUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class RewardsFragment : BaseFragment<FragmentRewardsBinding>(), ClickItemRewards,
  ClickRewardsFragment, ClickTool,
  RecyclerPagingListener {
  private val viewModel: RewardsViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  lateinit var RewardsAdapter: RewardsAdapter
  private lateinit var homeProductAdapter: HomeProductAdapter
  private var layoutManager: GridLayoutManager? = null
  private lateinit var recyclerViewPagingUtil: RecyclerViewPagingUtil
 var id_reward: Int? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.RewardsListScreenOpen()
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      observers()
      initUI()
    }
    reselectionProfileObserver()
    return mView
  }

  private fun initUI() {

    binding.toolbarTitle.click = this
    binding.toolbarTitle.tvToolbarTitle.text = "Reward"
    binding.textnoreward.text = "No Reward Found"
    binding.layoutRewards.click = this
    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    binding.layoutRewards.viewModel = viewModel
    binding.layoutRewards.rewardText = viewModel.rewardText
    binding.layoutRewards.lifecycleOwner = this

//    binding.note = viewModel.note
//    viewModel.rewardText.setEditText(binding.ed_coupon)
//    binding.clickRewards.onClickApplyRewards()
//    binding.rewbtn.setOnClickListener(onClickApplyRewards());
    initPagination()
  }
  fun reselectionProfileObserver() {
    viewModel.appManager.loadingState.profileReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_profile)
          viewModel.appManager.loadingState.profileReselected.value = false
        }
      }
    }
  }
  private fun initPagination() {

    RewardsAdapter = RewardsAdapter(requireActivity(), this)
    layoutManager = GridLayoutManager(requireContext(), 1)
    binding.rvItems.layoutManager = layoutManager
    recyclerViewPagingUtil = RecyclerViewPagingUtil(
      binding.rvItems,
      layoutManager!!, this
    )
    binding.rvItems.adapter = RewardsAdapter
    binding.rvItems.addOnScrollListener(recyclerViewPagingUtil)
    binding.rvItems.post { // Call smooth scroll
      binding.rvItems.scrollToPosition(0)
    }
//    resetSkip()
  }


  private fun observers() {
    if (profileViewModel.isLoggedIn.get() != true) {
      findNavController().navigate(R.id.nav_login_sheet)
    }

    viewModel.getRewards().observe(viewLifecycleOwner, Observer {
      it.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            binding.showEmpty = it.data?.data.isNullOrEmpty()
            it.let {
              RewardsAdapter.setDataChanged(it.data?.data)
            }
          }
          Result.Status.ERROR -> {
            binding.showEmpty = true
            // binding.showEmpty = it.data?.data.isNullOrEmpty()
//            binding.textnoreward.text = "No Rewards Found"

//            viewModel.loading.value = false
//            viewModel.text.value = "Error"
          }

        }
      }
    })


  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_rewards
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }


//  override fun onClickRewards(id:Int) {
////    viewModel.selectedVoucher.value = RewardModel
//    findNavController().navigate(R.id.rewardsDetailFragment)
//  }

  override fun onClickRewards(id: Int) {
//    viewModel.appManager.filtersManager.addFirstFilter(id, type)
    findNavController().navigate(
      R.id.rewardsDetailFragment, RewardsDetailFragment.getRewardListingBundle(id)
    )
  }

  override fun onNextPage(skip: Int, take: Int) {
//
  }


  override fun onClickApplyRewards() {
//    Log.e(TAG, "onClickApplyRewards:shifffaaaa ")
//    Toast.makeText(context, "REWARDS", Toast.LENGTH_LONG).show()
    validateRewards()

  }

  //  private fun applyCoupon(validateReward: ValidateReward) {
//    homeProductAdapter.notifyDataSetChanged()
//    if (ValidateReward != null && ValidateReward.rewa != 0.0 && !ValidateReward.reward_code.isNullOrEmpty()) {
//      viewModel.isCouponApplied.value = true
//      viewModel.couponModel.value = couponModel
//      viewModel.isUpdatingCart.value = (false)
//      viewModel.doAmountCalculations()
//    }
//  }
  private fun validateRewards() {
    if (viewModel.rewardText.getValue()
        .isNotBlank()
    ) {
      viewModel.appManager.analyticsManagers.rewardEntered(viewModel.rewardText.getValue())


      viewModel.validateRewards().observe(viewLifecycleOwner, Observer {
        it.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
              it.let {
//                viewModel.termsAndConditions.value = it.data?.data?.reward_campaign?.terms
                id_reward = it.data?.data?.id!!
                Log.e("id RewardFagment --- ", "id: $id")
                viewModel.resetInput()
//                findNavController().navigate(R.id.dialogAnimationReward)
                findNavController().navigate(
                  R.id.dialogAnimationReward, RewardsApplyDialog.getDialogApplyBundle(id_reward!!)
                )
                // RewardsAdapter.setDataChanged(it.data?.data)
              }
            }
            Result.Status.ERROR -> {
              it.message?.let { it1 ->
                AlertManager.showErrorMessage(
                  requireActivity(),
                  it1
                )
                viewModel.resetInput()
              }
//              hideLoading()
//            viewModel.loading.value = false
//            viewModel.text.value = "Error"
            }

          }
        }
      })
//       viewModel.validateRewards().observe(viewLifecycleOwner, Observer {
//        it?.let {
//          when (it.status) {
//            Result.Status.SUCCESS -> {
//              applyCoupon(it.data?.data)
//              viewModel.appManager.analyticsManagers.couponApplied(viewModel.couponText.getValue())
//            }
//            Result.Status.ERROR -> {
//              viewModel.appManager.analyticsManagers.couponDenied(viewModel.couponText.getValue())
//              Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
//                .show()
//              viewModel.isCouponApplied.value = false
//              viewModel.isUpdatingCart.value = (false)
//            }
//            Result.Status.LOADING -> {
//              viewModel.isUpdatingCart.value = (true)
//            }
//          }
//        }
//      })
    }
  }


}
