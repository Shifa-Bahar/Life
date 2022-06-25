package com.lifepharmacy.application.ui.rating.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.databinding.BottomProductReviewBinding
import com.lifepharmacy.application.databinding.BottomRatingBinding
import com.lifepharmacy.application.model.orders.SubOrderProductItem
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.rating.RatingsViewModel
import com.lifepharmacy.application.utils.universal.setNavigationResult
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class ProductReviewSheet : BaseBottomUpSheet<BottomProductReviewBinding>(),
  ClickProductReviewBottom {


  var position: Int = 0
  var rating: Float = 0F
  var inSideReview: Boolean = false


  companion object {
    var isAlreadyOpen: Boolean = false
    var KEY = "review"
    var POSITION = "position"
    var RATING_VALUE = "rating"
    var PRODUCT_ID = "product_id"
    var REVIEW = "review"
    var SELECTED_SUBORDER_ITEM = "selectedSubOrderItem"
    var SUB_ORDER_ID = "subOrderId"
    var INSIDE_REVIEW = "insideReview"
    var IS_ANONYMOUS = "isAnonymous"
    fun getRatingReviewBottomSheetBundle(
      position: Int,
      subOrderID: String,
      subOrderProductItem: SubOrderProductItem,
      rating: Float,
      insideReview: Boolean = false
    ): Bundle {
      val bundle = Bundle()
      bundle.putInt(POSITION, position)
      bundle.putString(SUB_ORDER_ID, subOrderID)
      bundle.putFloat(RATING_VALUE, rating)
      bundle.putBoolean(INSIDE_REVIEW, insideReview)
      bundle.putParcelable(SELECTED_SUBORDER_ITEM, subOrderProductItem)
      return bundle
    }
  }

  private val viewModel: RatingsViewModel by viewModels()
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCancelable = true
    initLayout()
    observers()
    isAlreadyOpen = true
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    arguments?.let {
      position = it.getInt("position", 0)
      rating = it.getFloat("rating", 0F)
      inSideReview = it.getBoolean(INSIDE_REVIEW, false)
      if (it.containsKey(SUB_ORDER_ID)) {
        viewModel.subOrderId = it.getString(SUB_ORDER_ID)
      }

      viewModel.selectedSubOrderProductItem.value = it.getParcelable("selectedSubOrderItem")
//      position = it.getInt("position")
    }
  }
//  override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//
//  }

  private fun initLayout() {
    binding.click = this
    binding.viewModel = viewModel
    binding.review = viewModel.review
    viewModel.review.setEditText(binding.edReview)
    binding.simpleRatingBar.rating = rating
    binding.swAnonymous.isChecked = false
  }

  private fun observers() {
    binding.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
      this.rating = rating
    }
  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_product_review
  }

  override fun permissionGranted(requestCode: Int) {

  }


  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    isAlreadyOpen = false
  }

  override fun onCancel(dialog: DialogInterface) {
    super.onCancel(dialog)
    isAlreadyOpen = false
  }

  override fun onClickSubmit() {

    if (inSideReview) {
      submitProductRating(
        productID = viewModel.selectedSubOrderProductItem.value?.id.toString(),
        rating = rating,
        isAnonymous = binding.swAnonymous.isChecked,
        review = viewModel.review.getValue()
      )
    } else {
      val bundle = Bundle()
      bundle.putInt(POSITION, position)
      bundle.putString(REVIEW, viewModel.review.getValue())
      bundle.putString(PRODUCT_ID, viewModel.selectedSubOrderProductItem.value?.id.toString())
      bundle.putFloat(RATING_VALUE, rating)
      bundle.putBoolean(IS_ANONYMOUS, binding.swAnonymous.isChecked)
      bundle.putParcelable(SELECTED_SUBORDER_ITEM, viewModel.selectedSubOrderProductItem.value)
      setFragmentResult(KEY, bundle)
      findNavController().navigateUp()
    }
  }

  private fun submitProductRating(
    productID: String,
    rating: Float,
    review: String,
    isAnonymous: Boolean = false
  ) {
    viewModel.rateProduct(
      productID = productID,
      rating = rating,
      review = review,
      isAnonymous = isAnonymous
    ).observe(viewLifecycleOwner, {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            findNavController().navigateUp()
          }
          Result.Status.ERROR -> {
            hideLoading()
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
          }
          Result.Status.LOADING -> {
            showLoading()
          }
        }
      }
    })
  }
}