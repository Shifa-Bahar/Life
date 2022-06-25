package com.lifepharmacy.application.ui.products.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpRatioScreenSheet
import com.lifepharmacy.application.databinding.BottomSheetProductReviewsBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.products.adapter.ReviewsCardsAdapter
import com.lifepharmacy.application.ui.products.viewmodel.ProductViewModel
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.universal.RecyclerPagingListener
import com.lifepharmacy.application.utils.universal.RecyclerViewPagingUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class ProductReviewsSheet : BaseBottomUpRatioScreenSheet<BottomSheetProductReviewsBinding>(0.80),
  RecyclerPagingListener {
  private val viewModel: ProductViewModel by activityViewModels()
  private lateinit var reviewAdapter: ReviewsCardsAdapter
  private var layoutManager: GridLayoutManager? = null
  private lateinit var recyclerViewPagingUtil: RecyclerViewPagingUtil

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    AnalyticsUtil.setEvenWithNamePropertyValue(
      requireContext(),
      getString(R.string.analytic_screen), "ProductReviewsSheet"
    )
    isCancelable = true
    initLayout()
    resetSkip()
    reviewsObserver()
  }

  private fun initLayout() {
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    reviewAdapter = ReviewsCardsAdapter(
      requireActivity(),
      appManager.storageManagers.config.noCommentLable ?: "No Comment"
    )
    layoutManager = GridLayoutManager(requireContext(), 1)
    binding.rvReviews.layoutManager = layoutManager
    recyclerViewPagingUtil = RecyclerViewPagingUtil(
      binding.rvReviews,
      layoutManager!!, this
    )
    binding.rvReviews.addOnScrollListener(recyclerViewPagingUtil)
    binding.rvReviews.adapter = reviewAdapter
  }

  private fun reviewsObserver() {
    viewModel.getReviews().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.data?.data?.let { data ->
              recyclerViewPagingUtil.nextPageLoaded(data.size)
            }
            if (viewModel.skip == 0) {
              reviewAdapter.refreshData(it.data?.data)
            } else {
              reviewAdapter.setDataChanged(it.data?.data)
            }

            hideLoading()
          }
          Result.Status.ERROR -> {
            hideLoading()
            it.message?.let { it1 ->
              AlertManager.showErrorMessage(
                requireActivity(),
                it1
              )
            }
          }
          Result.Status.LOADING -> {
            showLoading()
            recyclerViewPagingUtil.isLoading = true
          }
        }
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_product_reviews
  }

  override fun permissionGranted(requestCode: Int) {

  }

  private fun resetSkip() {
    recyclerViewPagingUtil.skip = 0
    viewModel.skip = 0
  }

  override fun onNextPage(skip: Int, take: Int) {
    viewModel.skip = skip
    viewModel.take = take
    reviewsObserver()
  }
}