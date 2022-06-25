package com.lifepharmacy.application.ui.pcr.fragments

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.lifepharmacy.application.Constants
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentAppoinmentDetailsBinding
import com.lifepharmacy.application.databinding.FragmentRewardsDetailsBinding
import com.lifepharmacy.application.databinding.FragmentVouchersDetailsBinding
import com.lifepharmacy.application.enums.VoucherStatus
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.RewardsListScreenOpen
import com.lifepharmacy.application.managers.analytics.voucherDetailScreenOpen
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Products
import com.lifepharmacy.application.model.vouchers.VoucherModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.orders.viewmodels.OrderDetailViewModel
import com.lifepharmacy.application.ui.pcr.adapters.ClickAppoinymenttem
import com.lifepharmacy.application.ui.pcr.adapters.PcrAppointmentproductAdapter
import com.lifepharmacy.application.ui.pcr.adapters.PcrReportAppointmentAdapter
import com.lifepharmacy.application.ui.pcr.viewmodel.PcrListingViewModel
import com.lifepharmacy.application.ui.rewards.dialog.RewardsBarcodeDialog
import com.lifepharmacy.application.ui.rewards.viewmodels.RewardsViewModel
import com.lifepharmacy.application.ui.splash.SplashActivity
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.vouchers.viewmodels.VouchersViewModel
import com.lifepharmacy.application.ui.webActivity.WebViewActivity
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.DateTimeUtil
import com.lifepharmacy.application.utils.StatusUtil
import com.lifepharmacy.application.utils.URLs
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import com.lifepharmacy.application.utils.universal.RecyclerPagingListener
import com.lifepharmacy.application.utils.universal.RecyclerViewPagingUtil
import com.pnuema.java.barcode.Barcode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dailog_reward_barcode.view.*
import kotlinx.android.synthetic.main.fragment_splash_first_page.*
import kotlinx.android.synthetic.main.layout_rewards_front.view.*
import kotlinx.android.synthetic.main.layout_voucher_back.view.*
import kotlinx.android.synthetic.main.layout_voucher_front.view.*
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class AppoinmentDetailFragment : BaseFragment<FragmentAppoinmentDetailsBinding>(), ClickTool,
  ClickAppoinymenttem,
  RecyclerPagingListener {

  companion object {
    fun getAppoinmentListingBundle(
      id: Int?
    ): Bundle {
      val bundle = Bundle()
      id?.let {
        bundle.putInt("id", id)
      }
      return bundle
    }
  }

  private val viewModel: OrderDetailViewModel by activityViewModels()
  private val pcrviewModel: PcrListingViewModel by activityViewModels()
  private lateinit var PcrAppointmentproductAdapter: PcrAppointmentproductAdapter
  private lateinit var PcrReportAppointmentAdapter: PcrReportAppointmentAdapter
  private lateinit var recyclerViewPagingUtil: RecyclerViewPagingUtil
  private var layoutManager: GridLayoutManager? = null
  private var mSetRightOut: AnimatorSet? = null
  private var mSetLeftIn: AnimatorSet? = null
  private var mIsBackVisible = false
  private var voucherString = ""
  private var imageUrl: String = ""
  var productglobal: ArrayList<Products>? = ArrayList()
  private var reward_code: String = ""
  var dateofWeeks: String = ""
  var dayOfWeeks: String = ""
  var timestart: String = ""
  var timeend: String = ""
  var visibility: String = ""
  private var invoiceURl = ""

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    arguments?.let {
      pcrviewModel.appoinmentid = it.getInt("id")
      Log.e(TAG, "onCreateView:appoint id ${pcrviewModel.appoinmentid}")
    }
    viewModel.appManager.analyticsManagers.RewardsListScreenOpen()
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
//      initUI()
      initProductsListRV()
      initReportListRV()
      observers()
//      initUI()
//      observers()
    }
//    initProductsListRV()
//    initReportListRV()
//    observers()
//    initUI()
//    if (view == null) {
//      mView = super.onCreateView(inflater, container, savedInstanceState)
//      initProductsListRV()
//      initReportListRV()
//      observers()
//      initUI()
//
//    }

    return mView
  }


  @SuppressLint("SetTextI18n")
  private fun initUI() {
    binding.toolbarTitle.click = this
    binding.lifecycleOwner = this
    binding.viewModel = pcrviewModel
    binding.lyOrderHeader.viewModel = pcrviewModel
    binding.lyOrderHeader.lifecycleOwner = this
    binding.lyItems.lifecycleOwner = this
    binding.lyReportitems.lifecycleOwner = this
    binding.lyPaymentMethods.lifecycleOwner = this
    binding.toolbarTitle.tvToolbarTitle.text = "Appointment Details"
    test()
  }


  private fun test() {
    binding.tvStatus.text = pcrviewModel.status.toString()
    binding.lyOrderHeader.tvApporderid.text = "APPOINTMENT #${pcrviewModel.appoinmentorderid}"
    binding.lyOrderHeader.tvAddressTitle.text = pcrviewModel.appaddress?.type
    binding.lyOrderHeader.addressname.text = pcrviewModel.appaddress?.name
    binding.lyOrderHeader.tvStreetaddress.text = pcrviewModel.appaddress?.street_address
    binding.lyOrderHeader.tvPhone.text = pcrviewModel.appaddress?.phone
    binding.lyPaymentMethods.pcrviewModel = pcrviewModel

    var validity =
      DateTimeUtil.getDayDAteFromStringWithoutTimeZone(pcrviewModel.appointmentDetails?.created_at.toString())
    binding.lyOrderHeader.tvDate.text = "Placed on $validity"

    var startdate: String = pcrviewModel.appointmentDetails?.start_time.toString()
    var enddate: String = pcrviewModel.appointmentDetails?.end_time.toString()

    dateofWeeks = DateTimeUtil.getDateStringDateFromStringWithoutTimeZone(startdate).toString()
    dayOfWeeks = DateTimeUtil.getDayStringDateFromStringWithoutTimeZone(startdate).toString()
    timestart = DateTimeUtil.getFormatonlyTimeString(startdate).toString()
    timeend = DateTimeUtil.getFormatonlyTimeString(enddate).toString()
    binding.tvCompleteDate.text = "$dayOfWeeks | $dateofWeeks |"
    binding.tvCompleteTime.text = "$timestart-$timeend"
    Log.e(TAG, "test:scheme ${pcrviewModel.paymentdetails?.card_info?.cardScheme}")

    if (pcrviewModel.paymentdetails?.method == "card") {
      binding.lyPaymentMethods.tvCard.visibility = View.VISIBLE
      binding.lyPaymentMethods.paymeth.visibility = View.GONE
      binding.lyPaymentMethods.tvCard.text =
        pcrviewModel.paymentdetails?.card_info?.paymentDescription
    } else {
      binding.lyPaymentMethods.tvCard.visibility = View.GONE
      binding.lyPaymentMethods.paymeth.visibility = View.VISIBLE
      binding.lyPaymentMethods.paymeth.text = pcrviewModel.paymentdetails?.method
    }
//checking if type is visa or mastercard
    binding.lyPaymentMethods.showEmpty =
      pcrviewModel.paymentdetails?.card_info?.cardScheme?.toLowerCase() == "visa"

    binding.lyOrderSummary.tvSubtotalvalue.text = pcrviewModel.orderdetails?.sub_total.toString()

    if (pcrviewModel.orderdetails?.discount?.equals(null) == true || pcrviewModel.orderdetails?.discount?.equals(
        0.0
      ) == true
    ) {
      binding.lyOrderSummary.tvlydisccount.visibility = View.GONE
    } else {
      binding.lyOrderSummary.tvlydisccount.visibility = View.VISIBLE
      binding.lyOrderSummary.tvDiscountvalue.text = pcrviewModel.orderdetails?.discount.toString()
    }

    binding.lyOrderSummary.tvTotalvalue.text = pcrviewModel.orderdetails?.total.toString()
    binding.lyOrderSummary.tvVatAmount.text = pcrviewModel.orderdetails?.tax.toString()
  }

  private fun initProductsListRV() {
    PcrAppointmentproductAdapter =
      PcrAppointmentproductAdapter(requireActivity(), viewModel.appManager)
    layoutManager = GridLayoutManager(requireContext(), 1)
    binding.lyItems.rvItems.adapter = PcrAppointmentproductAdapter

    recyclerViewPagingUtil = RecyclerViewPagingUtil(
      binding.lyItems.rvItems,
      layoutManager!!, this
    )

    binding.lyItems.rvItems.addOnScrollListener(recyclerViewPagingUtil)
    binding.lyItems.rvItems.post { // Call smooth scroll
      binding.lyItems.rvItems.scrollToPosition(0)
    }
  }

  private fun initReportListRV() {
    PcrReportAppointmentAdapter =
      PcrReportAppointmentAdapter(requireActivity(), this, viewModel.appManager)
    layoutManager = GridLayoutManager(requireContext(), 1)
    binding.lyReportitems.rvItems.adapter = PcrReportAppointmentAdapter

    recyclerViewPagingUtil = RecyclerViewPagingUtil(
      binding.lyReportitems.rvItems,
      layoutManager!!, this
    )

    binding.lyReportitems.rvItems.addOnScrollListener(recyclerViewPagingUtil)
    binding.lyReportitems.rvItems.post { // Call smooth scroll
      binding.lyReportitems.rvItems.scrollToPosition(0)
    }
  }

  private fun observers() {
    pcrviewModel.getPcrAppoinmentDetail().observe(viewLifecycleOwner, Observer {
      it.let {
        when (it.status) {
          Result.Status.SUCCESS -> {

            it.let {
              pcrviewModel.status = it.data?.data?.status
              pcrviewModel.appoinmentorderid = it.data?.data?.order?.order_id
              pcrviewModel.appaddress = it.data?.data?.address!!
              pcrviewModel.appointmentDetails = it.data?.data?.appointment_details!!
              PcrAppointmentproductAdapter.setDataChanged(it.data.data!!.products)

              if (!it.data.data!!.reports.isNullOrEmpty()) {
                binding.lyReportitems.rvItems.visibility = View.VISIBLE
                binding.lyReportitems.tvReport.visibility = View.GONE
                PcrReportAppointmentAdapter.setDataChanged(it.data.data!!.reports)
              } else {
                binding.lyReportitems.rvItems.visibility = View.GONE
                binding.lyReportitems.tvReport.visibility = View.VISIBLE
              }
              pcrviewModel.paymentdetails = it.data.data!!.payment_details
              pcrviewModel.orderdetails = it.data.data!!.order
            }
            initUI()
            hideLoading()
          }
          Result.Status.ERROR -> {

            it.message?.let { it1 ->
              AlertManager.showErrorMessage(
                requireActivity(),
                it1
              )
            }
            hideLoading()
          }
          Result.Status.LOADING -> {
            showLoading()
          }

        }
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_appoinment_details
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }


  override fun onNextPage(skip: Int, take: Int) {

  }

  override fun onClickInvoice(url: String) {
//    val makeURl = Constants.BASE_URL + URLs.INVOICE
    val makeURl = url
//    invoiceURl = makeURl.replace("####", subOrderId.toString())
    invoiceURl = makeURl

    val intent = CustomTabsIntent.Builder().build()

    WebViewActivity.open(requireActivity(), invoiceURl, getString(R.string.report))


  }
}
