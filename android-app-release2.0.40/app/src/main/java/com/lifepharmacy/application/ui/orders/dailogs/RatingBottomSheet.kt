package com.lifepharmacy.application.ui.orders.dailogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.databinding.BottomRatingBinding
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.orders.adapters.ClickRatingTag
import com.lifepharmacy.application.ui.orders.adapters.RatingTagsAdapter
import com.lifepharmacy.application.ui.orders.viewmodels.OrderDetailViewModel
import com.lifepharmacy.application.ui.rating.RatingsViewModel
import com.lifepharmacy.application.utils.ObjectMapper
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDoubleByDefault1
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class RatingBottomSheet : BaseBottomUpSheet<BottomRatingBinding>(),
  ClickRatingBottom, ClickRatingTag {


  companion object {
    var isAlreadyOpen: Boolean = false
    fun getRatingBottomSheetBundle(
      orderID: Int,
      subOrderID: Int,
      shipmentId: Int,
      rating: Float
    ): Bundle {
      val bundle = Bundle()
      bundle.putInt("orderID", orderID)
      bundle.putInt("subOrderID", subOrderID)
      bundle.putFloat("rating", rating)
      bundle.putInt("shipmentId", shipmentId)
      return bundle
    }
  }

  private val viewModel: RatingsViewModel by viewModels()
  private val viewOrderDetailViewModel: OrderDetailViewModel by activityViewModels()
  private lateinit var ratingTagAdapter: RatingTagsAdapter
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
      viewModel.subOrderId =
        it.getInt("subOrderID").intToNullSafeDoubleByDefault1().currencyFormat()
      viewModel.orderID = it.getInt("orderID")
      viewModel.rating = it.getFloat("rating")
      viewModel.shipmentId =
        it.getInt("shipmentId").intToNullSafeDoubleByDefault1().currencyFormat()
    }
  }
//  override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//
//  }

  private fun initLayout() {
    binding.click = this
    binding.review = viewModel.review
    viewModel.review.setEditText(binding.edReview)
    ratingTagAdapter = RatingTagsAdapter(requireActivity(), this)
    binding.rvReasons.adapter = ratingTagAdapter
  }

  private fun observers() {
    viewModel.getRatingsTags().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            ratingTagAdapter.setDataChanged(it.data)
          }
          Result.Status.ERROR -> {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
          }
          Result.Status.LOADING -> {

          }
        }
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_rating
  }

  override fun permissionGranted(requestCode: Int) {

  }


  override fun onClickSubmit() {
    submitRating()
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    isAlreadyOpen = false
  }

  override fun onCancel(dialog: DialogInterface) {
    super.onCancel(dialog)
    isAlreadyOpen = false
  }

  override fun onClickRatingTag(position: Int, reason: String) {
    viewModel.setRatingTagSelectedItems(reason)
    ratingTagAdapter.setItems(position, viewModel.selectedTags)
  }

  private fun submitRating() {
    viewModel.rateDriverShipment().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            viewOrderDetailViewModel.subOrderItemMut.value = it.data?.data?.let { it1 ->
              ObjectMapper.getSubOrderMainRatingToFloatRating(
                it1
              )
            }
//            if (!it.data?.data?.transactions.isNullOrEmpty()) {
//              viewOrderDetailViewModel.transactionModel.value = it.data?.data?.transactions?.first()
//            }
            it.data?.data?.transactions?.let {
              viewOrderDetailViewModel.transactionModel.value = it
            }
            isAlreadyOpen = false
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