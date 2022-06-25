package com.lifepharmacy.application.ui.orders.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.Constants
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentOrderDetailBinding
import com.lifepharmacy.application.managers.analytics.orderDetailScreenOpen
import com.lifepharmacy.application.model.orders.*
import com.lifepharmacy.application.model.orders.suborder.SubOrderDetailForRating
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.dashboard.viewmodel.DashboardViewModel
import com.lifepharmacy.application.ui.orders.adapters.ClickShipmentItem
import com.lifepharmacy.application.ui.orders.adapters.ClickShipmentProduct
import com.lifepharmacy.application.ui.orders.adapters.ShipmentAdapter
import com.lifepharmacy.application.ui.orders.dailogs.RatingBottomSheet
import com.lifepharmacy.application.ui.orders.viewmodels.OrderDetailViewModel
import com.lifepharmacy.application.ui.rating.fragments.MainRatingFragment
import com.lifepharmacy.application.ui.rating.fragments.ProductReviewSheet
import com.lifepharmacy.application.ui.utils.dailogs.ClickInputActionDialog
import com.lifepharmacy.application.ui.utils.dailogs.ImageSelectBottomSheet
import com.lifepharmacy.application.ui.utils.dailogs.InputActionDialog
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.webActivity.WebViewActivity
import com.lifepharmacy.application.utils.ObjectMapper
import com.lifepharmacy.application.utils.URLs
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeInt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class OrderDetailFragment : BaseFragment<FragmentOrderDetailBinding>(), ClickOrderDetailFragment,
  ClickShipmentProduct, ClickShipmentItem,
  ClickTool {
  var debouncePeriod: Long = 500
  private val coroutineScope = lifecycle.coroutineScope
  private var searchJob: Job? = null
  private var invoiceURl = ""
//  private val requestPermission =
//    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
//      var isGranted = true
//      granted.entries.forEach {
//        Log.e("DEBUG", "${it.key} = ${it.value}")
//        if (!it.value) {
//          isGranted = it.value
//        }
//      }
//      if (isGranted) {
//        downloadInvoice(invoiceURl)
//      }
//    }

  companion object {
    fun getOrderDetailBundle(subOrderId: String, id: String? = null): Bundle {
      val bundle = Bundle()
      id?.let {
        bundle.putString("order", id)
        bundle.putString("sub_orderId", subOrderId)
      }
      return bundle
    }
  }

  private val viewModel: OrderDetailViewModel by activityViewModels()
  private val viewModelDashBoard: DashboardViewModel by activityViewModels()
  lateinit var ordersAdapter: ShipmentAdapter
  var order: String? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      viewModel.orderId = it.getString("order").toString()
      viewModel.subOrderId = it.getString("sub_orderId").toString()
    }

  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.orderDetailScreenOpen()
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
    }
    reselectionCartObserver()
    viewModel.clearReturnItems()
    return mView
  }

  private fun initUI() {

    binding.toolbarTitle.click = this
    binding.click = this
    binding.viewModel = viewModel
    binding.lyOrderSummary.viewModel = viewModel
    binding.lyOrderSummary.lifecycleOwner = this
    binding.lyOrderHeader.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.lyPaymentMethods.viewModel = viewModel
    binding.lyPaymentMethods.lifecycleOwner = this
    binding.lyOrderHeader.lifecycleOwner = this
    binding.toolbarTitle.tvToolbarTitle.text = getString(R.string.order_details)
    ordersAdapter = ShipmentAdapter(requireActivity(), this, this)
    binding.rvShipments.adapter = ordersAdapter


  }
  fun reselectionCartObserver() {
    viewModel.appManager.loadingState.cartReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_cart)
          viewModel.appManager.loadingState.cartReselected.value = false
        }
      }
    }
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_order_detail
  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

  override fun onClickReturn() {
    findNavController().navigate(R.id.returningProductsFragment)
  }


  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickShipmentRating(ratingValue: Float, subOrderId: Int, shipmentId: Int) {
    searchJob?.cancel()
    searchJob = coroutineScope.launch {
      delay(debouncePeriod)
      if (!MainRatingFragment.isAlreadyOpen) {
        findNavController().navigate(
          R.id.nav_rating,
          MainRatingFragment.getBundle(
            subOrderID = subOrderId.toString(),
            rating = ratingValue
          )
        )
//        findNavController().navigate(
//          R.id.ratingBottomSheet,
//          RatingBottomSheet.getRatingBottomSheetBundle(
//            viewModel.subOrderItemMut.value?.id ?: 0,
//            subOrderId,
//            shipmentId,
//            ratingValue
//          )
//        )
      }
    }
  }

  override fun onClickInvoice(subOrderId: Int) {
    val makeURl = Constants.BASE_URL + URLs.INVOICE
    invoiceURl = makeURl.replace("####", subOrderId.toString())
//    invoiceURl = "https://lifeadmin-app.s3.me-south-1.amazonaws.com/users/invoice/AE-SB6640-1.pdf"
//    requestPermission.launch(ConstantsUtil.RequiredPermissionsStorage)
    val intent = CustomTabsIntent.Builder().build()
//    findNavController().navigate(R.id.invoiceBottomSheet)
    // Example non-cors-whitelisted headers.

    // Example non-cors-whitelisted headers.


//    val headers = Bundle()
//    headers.putString("Authorization", ConstantsUtil.BEARER + viewModel.appManager.persistenceManager.getToken())
//    headers.putString("redirect-url", "Some redirect url")
//    intent.intent.putExtra(Browser.EXTRA_HEADERS, headers)
//    intent.launchUrl(requireActivity(), Uri.parse(invoiceURl));


    WebViewActivity.open(requireActivity(), invoiceURl, getString(R.string.invoice))
//    val browserIntent = Intent(
//      Intent.ACTION_VIEW, Uri.parse(invoiceURl)
//    )
//    val bundle = Bundle()
//    bundle.putString("Authorization", ConstantsUtil.BEARER + viewModel.appManager.persistenceManager.getToken())
//    bundle.putString("Content-Type", "application/pdf")
//    bundle.putString("Accept", "application/pdf")
//    bundle.putString("Connection", "close")
//    browserIntent.putExtra(Browser.EXTRA_HEADERS, bundle)
//    startActivity(browserIntent)
//    val browserIntent = Intent(
//      Intent.ACTION_VIEW, Uri.parse(completeURl)
//    )
//    val bundle = Bundle()
//    bundle.putString("Authorization", ConstantsUtil.BEARER +  viewModel.appManager.persistenceManager.getToken())
//    browserIntent.putExtra(Browser.EXTRA_HEADERS, bundle)
//    startActivity(browserIntent)

//    val builder = CustomTabsIntent.Builder()
//    val customTabsIntent = builder.build()
//    val headers = Bundle()
//    headers.putString("Authorization", ConstantsUtil.BEARER +  viewModel.appManager.persistenceManager.getToken())
////    headers.putString("bearer-token", viewModel.appManager.persistenceManager.getToken())
////    headers.putString("redirect-url", "Some redirect url")
//    customTabsIntent.intent.putExtra(Browser.EXTRA_HEADERS, headers)
//    customTabsIntent.launchUrl(requireContext(), Uri.parse(invoiceURl))
  }

  override fun onClickViewStatusDetails(subOrderItem: SubOrderItem) {

    try {
      viewModel.selectedShipmentMut.value = subOrderItem
      findNavController().navigate(R.id.shipmentStatusBottomSheet)
    } catch (e: Exception) {
    }


  }


  override fun onClickProductRating(
    productId: String,
    ratingValue: Float,
    subOrderId: String,
    subOrderProductItem: SubOrderProductItem
  ) {
    viewModel.productID = productId
    viewModel.productRatingValue = ratingValue
    viewModel.subOrderID = subOrderId.stringToNullSafeInt()
    searchJob?.cancel()
    searchJob = coroutineScope.launch {
      delay(debouncePeriod)
      if (!InputActionDialog.isAlreadyOpen) {
        try {
          findNavController().navigate(
            R.id.productReviewSheet, ProductReviewSheet.getRatingReviewBottomSheetBundle(
              position = 0,
              subOrderID = subOrderId,
              subOrderProductItem = subOrderProductItem,
              rating = ratingValue ?: 1.0F,
              insideReview = true
            )
          )

//          openProductReviewDialog()
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
  }

  private fun observers() {
//    viewModel.getOrderDetails().observe(viewLifecycleOwner, Observer {
//      it?.let {
//        when (it.status) {
//          Result.Status.SUCCESS -> {
//            hideLoading()
//            it.data?.data?.let { it1 -> handleOrderDetailResult(it1) }
//          }
//          Result.Status.ERROR -> {
//            hideLoading()
//            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
//          }
//          Result.Status.LOADING -> {
//            showLoading()
//          }
//        }
//      }
//    })

    viewModel.getSubOrderDetail().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
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

  private fun handleOrderDetailResult(orderDetail: OrderDetailResponseModel) {
//    if (viewModel.selectedSubOrderItem.value != null) {
//      val temSuborderArray = ArrayList<SubOrderItem>()
//      temSuborderArray.add(viewModel.selectedSubOrderItem.value ?: return)
//      orderDetail.subOrders.clear()
//      orderDetail.subOrders = temSuborderArray
//    }
//    viewModel.subOrderItemMut.value = orderDetail
//    if (!orderDetail.transactions.isNullOrEmpty()) {
//      viewModel.transactionModel.value = orderDetail.transactions?.first()
//    }
//    ordersAdapter.setDataChanged(orderDetail.subOrders)
  }

  private fun handleSubOrderDetailResult(subOrder: SubOrderDetail) {
//    if (viewModel.selectedSubOrderItem.value != null) {
//      val temSuborderArray = ArrayList<SubOrderItem>()
//      temSuborderArray.add(viewModel.selectedSubOrderItem.value ?: return)
//      orderDetail.subOrders.clear()
//      orderDetail.subOrders = temSuborderArray
//    }
//    viewModel.orderModelMut.value = orderDetail
//    if (orderDetail.transactions!=null) {
//      viewModel.transactionModel.value = orderDetail.transactions?.first()
//    }
//
    viewModel.subOrderItemMut.value = subOrder
    subOrder?.let {
      viewModel.transactionModel.value = subOrder.transactions
    }
    ordersAdapter.setDataChanged(subOrder.subOrders)
  }

  private fun handleSubOrderRatingResult(subOrder: SubOrderDetailForRating) {
//    if (viewModel.selectedSubOrderItem.value != null) {
//      val temSuborderArray = ArrayList<SubOrderItem>()
//      temSuborderArray.add(viewModel.selectedSubOrderItem.value ?: return)
//      orderDetail.subOrders.clear()
//      orderDetail.subOrders = temSuborderArray
//    }
//    viewModel.orderModelMut.value = orderDetail
//    if (orderDetail.transactions!=null) {
//      viewModel.transactionModel.value = orderDetail.transactions?.first()
//    }
//

    var temSuBOrder = ObjectMapper.getSubOrderMainRatingToFloatRating(subOrder)
    viewModel.subOrderItemMut.value = temSuBOrder
    subOrder?.let {
      viewModel.transactionModel.value = subOrder.transactions
    }
    ordersAdapter.setDataChanged(temSuBOrder.subOrders)
  }

  private fun submitProductRating() {
    viewModel.rateProduct().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.data?.let { it1 -> handleSubOrderRatingResult(it1) }
//            viewModel.subOrderItemMut.value = it.data?.data
//            if (!it.data?.data?.transactions.isNullOrEmpty()) {
//              viewModel.transactionModel.value = it.data?.data?.transactions?.first()
//            }
            it.data?.data?.transactions?.let {
              viewModel.transactionModel.value = it
            }
            if (viewModel.productRatingValue >= 5) {
              viewModelDashBoard.openReviewBox.value = true
            }

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

  private fun openProductReviewDialog() {

    val inputActionBinding: InputActionDialog = InputActionDialog.newInstance(
      getString(R.string.feed_back),
      getString(R.string.feed_back_desc),
      getString(R.string.submit),
      getString(R.string.no_thanks)
    )
    inputActionBinding.show(
      childFragmentManager,
      ImageSelectBottomSheet.TAG
    )
    inputActionBinding.setInputDialogActionListener(object : ClickInputActionDialog {
      override fun onClickPositiveButton(string: String) {
        viewModel.productFeedback = string
        submitProductRating()
      }

      override fun onClickNegativeButton() {
//        submitProductRating()
      }

    })
  }

}

