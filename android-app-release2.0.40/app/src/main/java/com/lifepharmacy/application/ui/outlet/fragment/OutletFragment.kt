package com.lifepharmacy.application.ui.outlet.fragment

import android.animation.Animator
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.activity.addCallback
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentAppoinmentBinding
import com.lifepharmacy.application.databinding.FragmentOutletBinding
import com.lifepharmacy.application.databinding.FragmentWishlistBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.wishListScreenOpen
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeProduct
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.orders.adapters.OrdersAdapter
import com.lifepharmacy.application.ui.orders.fragments.PrescriptionOrderFragment
import com.lifepharmacy.application.ui.outlet.viewmodel.OutletViewModel
import com.lifepharmacy.application.ui.pcr.adapters.AppoinmentAdapter
import com.lifepharmacy.application.ui.pcr.adapters.ClickAppoinmentDetails
import com.lifepharmacy.application.ui.pcr.viewmodel.PcrListingViewModel
import com.lifepharmacy.application.ui.productList.adapter.ProductAdapter
import com.lifepharmacy.application.ui.products.fragment.ProductFragment
import com.lifepharmacy.application.ui.products.viewmodel.ProductViewModel
import com.lifepharmacy.application.ui.profile.fragments.ClickProfileHeader
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.rewards.adapters.PcrAdapter
import com.lifepharmacy.application.ui.rewards.fragment.RewardsDetailFragment
import com.lifepharmacy.application.ui.whishlist.viewmodel.WishListViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.vouchers.adapters.VouchersAdapter
import com.lifepharmacy.application.ui.webActivity.WebViewActivity
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.DateTimeUtil
import com.lifepharmacy.application.utils.URLs
import com.lifepharmacy.application.utils.universal.RecyclerPagingListener
import com.lifepharmacy.application.utils.universal.RecyclerViewPagingUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class OutletFragment : BaseFragment<FragmentOutletBinding>(), ClickTool, ClickOutletHeader,
  ClickProfileHeader {
  private val outletviewModel: OutletViewModel by activityViewModels()
  private var layoutManager: GridLayoutManager? = null
  private val viewModel: ProfileViewModel by activityViewModels()
  private val oviewModel: OutletViewModel by activityViewModels()
  var animation: String = "offer_tick_animation.json"
  var backAnimation: String = "qr-anim-3sec.json"
  var imageTitle: String = ""

  private var invoiceURl = ""


  //  qr-anim-3sec.json
//  qr-anim.json
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      findNavController().navigateUp()
//      navigate(R.id.nav_profile)
    }

  }


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
//    viewModel.appManager.analyticsManagers.wishListScreenOpen()
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
//      initUI()
    }
    initUI()
    observers()
    backAnimation()
    reselectionProfileObserver()
    return mView
  }

  private fun initUI() {
    binding.lifecycleOwner = this
    binding.click = this
    binding.toolbarTitle.click = this
    binding.lyHeader.click = this
    binding.lyHeader.mVieModel = oviewModel
    binding.lyHeader.lifecycleOwner = this
    binding.toolbarTitle.tvToolbarTitle.text = getString(R.string.outletbylife)
//    binding.linearLayout39.visibility = View.GONE
//    binding.imageerror.visibility = View.GONE
    Log.e(TAG, "initUI: available ${outletviewModel.available}")
    binding.available = outletviewModel.available

    loadbindingdata()
//    binding.imageTitle = outletviewModel.linkurl


  }

  fun loadbindingdata() {
    if (outletviewModel.available) {
      var validitydate: String? =
        DateTimeUtil.getStringDateFromStringWithoutTimeZoneAndMilSec(outletviewModel.date)

      binding.lyHeader.tvDate.text = "Valid till :$validitydate"

      binding.lyHeader.username.text = outletviewModel.username

      if (!outletviewModel.qrdata.isNullOrEmpty()) {
        val bitmap = generateQRCode(outletviewModel.qrdata)
        binding.imgProduct.setImageBitmap(bitmap)
      }
      if (outletviewModel.photo.isNotEmpty()) {
        activity?.let {

          Glide.with(it)
            .load(outletviewModel.photo)
            .error(R.drawable.ic_profile_placeholder)
            .fallback(R.drawable.ic_profile_placeholder)
            .placeholder(R.drawable.ic_profile_placeholder)
            .into(binding.lyHeader.ivProfile)
        }
      }
    } else {
      try {
        if (outletviewModel.nomemberurl.isNotEmpty()) {
          activity?.let {
            Glide.with(it).load(outletviewModel.nomemberurl).into(binding.imageerror)
          }
        }
      } catch (e: Exception) {
      }
    }

  }

  fun reselectionProfileObserver() {
    outletviewModel.appManager.loadingState.profileReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_profile)
          outletviewModel.appManager.loadingState.profileReselected.value = false
        }
      }
    }
  }

  private fun backAnimation() {
    binding.backAnimation.setAnimation(backAnimation)
    binding.backAnimation.repeatCount = Animation.INFINITE
    binding.backAnimation.playAnimation()
    binding.backAnimation.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationStart(animation: Animator?) {
        Log.e("Animation:", "start")
      }

      override fun onAnimationEnd(animation: Animator?) {
        Log.e("Animation:", "end")
        //Your code for remove the fragment
        findNavController().navigateUp()
      }

      override fun onAnimationCancel(animation: Animator?) {
        Log.e("Animation:", "cancel")
      }

      override fun onAnimationRepeat(animation: Animator?) {
        Log.e("Animation:", "repeat")
      }
    })
  }

  private fun generateQRCode(text: String): Bitmap {
    val width = 500
    val height = 500
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val codeWriter = MultiFormatWriter()
    try {
      val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
      for (x in 0 until width) {
        for (y in 0 until height) {
          bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
      }
    } catch (e: WriterException) {
      Log.d(TAG, "generateQRCode: ${e.message}")
    }
    return bitmap
  }

  //  private fun initUI() {
//    binding.toolbarTitle.click = this
//    binding.toolbarTitle.tvToolbarTitle.text = getString(R.string.appoinment)
//
//    productListAdapter = ProductAdapter(requireActivity(), this, this, viewModel.appManager,viewModel.appManager.storageManagers.config.backOrder ?: "Pre-Order")
//    binding.rvItems.adapter = productListAdapter
//  }
  private fun observers() {
    outletviewModel.getOutlet().observe(viewLifecycleOwner) {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {

            outletviewModel.available = it.data?.data?.is_available == true
            outletviewModel.linkurl = it.data?.data?.link_url.toString()

            if (it.data?.data?.is_available == true) {
              outletviewModel.photo = it.data?.data?.photo.toString()
              outletviewModel.username = it.data?.data?.name.toString()
              outletviewModel.date = it.data?.data?.expiry_date.toString()
              outletviewModel.qrdata = it.data?.data?.code.toString()
            } else {
              outletviewModel.nomemberurl = it.data?.data?.no_member_url.toString()
            }
//            binding.imageView.layout(0, 0, 0, 0);


            loadbindingdata()
//            hideLoading()
          }
          Result.Status.ERROR -> {
            Log.e(TAG, "observers: Error")
            it.message?.let { it1 ->
              AlertManager.showErrorMessage(
                requireActivity(),
                it1
              )
            }
//            hideLoading()
          }
          Result.Status.LOADING -> {
//            showLoading()
          }
        }
      }
    }
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_outlet
  }

  override fun permissionGranted(requestCode: Int) {

  }


  override fun onClickBack() {
    findNavController().navigateUp()
  }

  override fun onClickEdit() {
    Log.e(TAG, "onClickEdit: webview")
    val makeURl = outletviewModel.linkurl
    invoiceURl = makeURl
    val intent = CustomTabsIntent.Builder().build()
//    WebViewActivity.open(requireActivity(), linkurl)
//    WebViewActivity.open(requireActivity(), invoiceURl, getString(R.string.invoice))
    WebViewActivity.open(requireActivity(), invoiceURl, getString(R.string.lifeurl))
  }

  override fun onClickLogin() {

  }

  override fun onClickNotifications() {

  }


}
