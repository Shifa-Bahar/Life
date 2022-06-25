package com.lifepharmacy.application.ui.rating.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentRatingMainBinding
import com.lifepharmacy.application.enums.RatingFragmentState
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.model.orders.SubOrderDetail
import com.lifepharmacy.application.model.orders.SubOrderProductItem
import com.lifepharmacy.application.model.orders.suborder.SubOrderDetailForRating
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.cart.fragmets.AnimationOfferCompleteCouponDialog.Companion.TAG
import com.lifepharmacy.application.ui.rating.OnClickRatingMain
import com.lifepharmacy.application.ui.rating.RatingsViewModel
import com.lifepharmacy.application.ui.rating.adapters.ClickProductRating
import com.lifepharmacy.application.ui.rating.adapters.RatingProductsAdapter
import com.lifepharmacy.application.ui.utils.taglayout.TagView
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.ObjectMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class MainRatingFragment : BaseFragment<FragmentRatingMainBinding>(), OnClickRatingMain,
  ClickProductRating, ClickTool {
  private val viewModel: RatingsViewModel by viewModels()
  lateinit var productsAdapter: RatingProductsAdapter
  var debouncePeriod: Long = 750
  var subOrderID: String? = ""
  var subOrderrating: String? = ""
  private var slug: String? = null
  private var productRatingJob: Job? = null
  private val coroutineScope = lifecycle.coroutineScope


  companion object {
    var isAlreadyOpen: Boolean = false
    fun getBundle(
      subOrderID: String,
      subOrdernumber: String = "",
      rating: Float? = null,
      driverRating: Float? = null
    ): Bundle {
      val bundle = Bundle()
      bundle.putString("subOrderID", subOrderID)
      bundle.putString("subnumber", subOrdernumber)
      rating?.let {
        bundle.putFloat("rating", it)
      }
      driverRating?.let {
        bundle.putFloat("driverRating", it)
      }

      return bundle
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    isAlreadyOpen = true
    arguments?.let {
      viewModel.subOrderId = it.getString("subOrderID")
      viewModel.subordernumber = it.getString("subnumber")
      slug = it.getString("slug")

      viewModel.ratingMain = it.getFloat("rating")
      viewModel.driverRating = it.getFloat("driverRating")

    }

    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
    }

    getSubOrderDetails()
    observer()
    setFragmentResultListener(ProductReviewSheet.KEY) { key, bundle ->
      val rating = bundle.getFloat(ProductReviewSheet.RATING_VALUE, 0F)
      val review = bundle.getString(ProductReviewSheet.REVIEW, "")
      val position = bundle.getInt(ProductReviewSheet.POSITION, 0)
      val isAnonymous = bundle.getBoolean(ProductReviewSheet.IS_ANONYMOUS, false)
      val productId = bundle.getString(ProductReviewSheet.PRODUCT_ID, "")

      if (rating > 0) {
        productsAdapter.setRatingValues(
          rating = rating,
          reviewValue = review,
          position = position
        )
        submitProductRating(
          productID = productId,
          rating = rating,
          review = review,
          isAnonymous = isAnonymous
        )
      }
    }


//    getNavigationResult("review")?.observe(viewLifecycleOwner, {
//      it?.let {
//        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
//      }
//    })

    return mView
  }


  private fun initUI() {
    binding.tollBar.click = this
    binding.viewmodel = viewModel
    binding.lifecycleOwner = this
    binding.click = this
    viewModel.ratingState.value = RatingFragmentState.START
    binding.subOrderReview = viewModel.suOrderReview
    viewModel.review.setEditText(binding.edReview)
    binding.driverReview = viewModel.driverReview
    viewModel.review.setEditText(binding.edDriverReview)
    ratingChangeListener()
    viewModel.ratingMain?.let {
      binding.simpleRatingBar.setRating(it)
      viewModel.ratingState.value = RatingFragmentState.DETAIL
      viewModel.mainRatingTag.value = "Excellent"
    }
    viewModel.driverRating?.let {
      binding.driverRating.setRating(it)
    }
    subOrderID = viewModel.subOrderId
    Log.e(TAG, "initUI:subOrderID $subOrderID")
    Log.e(TAG, "initUI:slug $slug")
    initRVProducts()
  }

  private fun ratingChangeListener() {
    binding.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
      viewModel.ratingState.value = RatingFragmentState.DETAIL
      viewModel.setCorrespondingTag(rating)
    }
  }

  private fun initRVProducts() {
    productsAdapter = RatingProductsAdapter(requireActivity(), this, viewModel.subOrderId ?: "")
    binding.rvProducts.adapter = productsAdapter
  }


  private fun observer() {
    viewModel.showProducts.observe(viewLifecycleOwner, {
      it?.let {
        if (it) {
          viewModel.ratingState.value = RatingFragmentState.PRODUCTS
          binding.rvProducts.scrollToPosition(1)
          binding.svMain.post { binding.svMain.fullScroll(ScrollView.FOCUS_DOWN) }
        } else {
          viewModel.ratingState.value = RatingFragmentState.DETAIL
          viewModel.mainRatingTag.value = "Excellent"
        }
      }
    })

    binding.driverRating.setOnRatingChangeListener { ratingBar, rating, fromUser ->
      viewModel.isDriverRated.value = true
      viewModel.driverRating = rating
      viewModel.driverRating?.let {
        if (it > 0) {
          submitDriverRating()
        }
      }
    }
    viewModel.selectedRatingValue.observe(viewLifecycleOwner, {
      it?.let {
        viewModel.mainRatingTag.value = it.remarks
        setTags(it.tags as ArrayList<String>)
        viewModel.ratingMain?.let {
          if (it > 0) {
            submitSubOrderRating()
          }
        }
      }
    })
    viewModel.selectedSubOrderItem.observe(viewLifecycleOwner, {
      it?.let {
        viewModel.subOrderId = it.id.toString()
        viewModel.shipmentId = it.shipment?.id.toString()
        productsAdapter.setDataChanged(it.items?.let { it1 ->
          ObjectMapper.getSubOrderProductItemRatingToFloatRating(
            it1
          )
        })
      }
    })
  }

  private fun getSubOrderDetails() {
    subOrderID?.let { suborderid ->
      viewModel.getSubOrderDetail(suborderid).observe(viewLifecycleOwner, Observer {
        it?.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
              hideLoading()
              viewModel.subordernumber = it.data?.data?.subOrders?.first()?.subOrderId
              it.data?.data?.let { it1 -> handleSubOrderDetailResult(it1) }
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
    slug?.let { suborderid ->
      viewModel.getSubOrderDetail(suborderid).observe(viewLifecycleOwner, Observer {
        it?.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
              viewModel.subordernumber = it.data?.data?.subOrders?.first()?.subOrderId
              hideLoading()
              it.data?.data?.let { it1 -> handleSubOrderDetailResult(it1) }
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

  private fun handleSubOrderDetailResult(subOrder: SubOrderDetail) {
//    var temsubOrder = ObjectMapper.getSubOrderMainFloatToRatingRating(subOrder)
    Log.e(TAG, "handleSubOrderDetailResult:${subOrderrating.toString()} ")
    binding.tollBar.tvToolbarTitle.text = "Order #${viewModel.subordernumber.toString()}"
    binding.tollBar.date = subOrder.createdAt
    if (!subOrder.subOrders.isNullOrEmpty()) {
      viewModel.selectedSubOrderItem.value = subOrder.subOrders?.firstOrNull()?.let {
        ObjectMapper.getSingleSubOrderItemFloatToJustItemRatRating(
          it
        )
      }
    }

  }

  private fun handleSubOrderRatingResult(subOrder: SubOrderDetailForRating) {
//    binding.tollBar.tvToolbarTitle.text = subOrderrating.toString()
    binding.tollBar.date = subOrder.createdAt
    if (!subOrder.subOrders.isNullOrEmpty()) {
      viewModel.selectedSubOrderItem.value = subOrder.subOrders?.firstOrNull()
    }
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_rating_main
  }

  override fun permissionGranted(requestCode: Int) {
  }

  override fun onClickHideShowProducts() {
    viewModel.showProducts.value = viewModel.showProducts.value != true
  }

  override fun onClickRightDriverReview() {
    viewModel.isReviewDriver.value = true
  }

  override fun onClickSubmit() {
    submitOverAllRating()
  }

  override fun onClickProductRating(productId: String, ratingValue: Float, subOrderId: String) {
    productRatingJob?.cancel()
    productRatingJob = coroutineScope.launch {
      delay(debouncePeriod)
      if (ratingValue > 0) {
        submitProductRating(productId, ratingValue, "")
      }
    }

  }

  override fun onClickWriteReview(
    position: Int,
    subOrderProductItem: SubOrderProductItem,
    productId: String,
    ratingValue: Float?,
    subOrderId: String
  ) {
    findNavController().navigate(
      R.id.productReviewSheet, ProductReviewSheet.getRatingReviewBottomSheetBundle(
        position = position,
        subOrderID = subOrderId,
        subOrderProductItem = subOrderProductItem,
        rating = ratingValue ?: 1.0F
      )
    )

  }

  private fun setTags(list: ArrayList<String>) {
    binding.tagLayout.selected = viewModel.selectedTagPositions
    binding.tagLayout.setSelecteColors(Color.parseColor("#FFFFFFFF"), Color.parseColor("#FF365FC9"))
    binding.tagLayout.setTags(list)
    binding.tagLayout.setOnTagClickListener(object : TagView.OnTagClickListener {
      override fun onTagClick(position: Int, text: String?) {
        updateTagView(position, list)
      }

      override fun onTagLongClick(position: Int, text: String?) {
      }

      override fun onSelectedTagDrag(position: Int, text: String?) {
      }

      override fun onTagCrossClick(position: Int) {
      }
    })
  }

  fun updateTagView(position: Int, list: ArrayList<String>) {
    if (viewModel.selectedTagPositions.contains(position)) {
      viewModel.selectedTagPositions.remove(position)
      binding.tagLayout.selected = viewModel.selectedTagPositions
      binding.tagLayout.setTags(list)
    } else {
      viewModel.selectedTagPositions.add(position)
      binding.tagLayout.selected = viewModel.selectedTagPositions
      binding.tagLayout.setTags(list)

    }
  }

  private fun submitDriverRating() {
    viewModel.rateDriverShipment().observe(viewLifecycleOwner, {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.data?.let { it1 -> handleSubOrderRatingResult(it1) }
          }
          Result.Status.ERROR -> {
            hideLoading()
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
          }
          Result.Status.LOADING -> {
//            showLoading()
          }
        }
      }
    })
  }

  private fun submitSubOrderRating() {
    viewModel.rateSubOrderShipment().observe(viewLifecycleOwner, {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.data?.let { it1 -> handleSubOrderRatingResult(it1) }
          }
          Result.Status.ERROR -> {
            hideLoading()
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
          }
          Result.Status.LOADING -> {
//            showLoading()
          }
        }
      }
    })
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
    ).observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()

//            it.data?.data?.let { it1 -> handleSubOrderRatingResult(it1) }

          }
          Result.Status.ERROR -> {
            hideLoading()
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
          }
          Result.Status.LOADING -> {
          }
        }
      }
    })
  }

  private fun submitOverAllRating() {
    viewModel.rateOverAll(
      productArrayList = productsAdapter.arrayList,
      comments = productsAdapter.commentes,
      ratedValue = productsAdapter.ratedValue,
      isAnonymous = productsAdapter.isAnonymous
    ).observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            isAlreadyOpen = false
            showSuccessAllRate()
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

  private fun showSuccessAllRate() {
    MaterialAlertDialogBuilder(
      requireActivity(),
      R.style.ThemeOverlay_App_MaterialInfoDialog
    )
      .setTitle(getString(R.string.feedback_saved))
      .setPositiveButton(getString(R.string.ok)) { dialog, which ->
        findNavController().navigateUp()
      }
      .show()
  }


  override fun onClickBack() {
    isAlreadyOpen = false
    onBackPressed()
  }

  private fun onBackPressed() {
    MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialInfoDialog)
      .setTitle(resources.getString(R.string.unsaved_changes))
      .setMessage(resources.getString(R.string.all_changes_will_be_discarded))
      .setNeutralButton(getString(R.string.cancel)) { _, _ ->

      }
      .setNegativeButton(getString(R.string.discard_changes)) { _, _ ->
        findNavController().navigateUp()
      }
      .setPositiveButton(resources.getString(R.string.submite_review)) { _, _ ->
        submitOverAllRating()
      }
      .show()
  }
}



