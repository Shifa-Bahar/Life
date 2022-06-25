package com.lifepharmacy.application.ui.home.fragments

import android.animation.Animator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogAnimationComboBoxBinding
import com.lifepharmacy.application.databinding.DailogAnimationOfferCompleteBinding
import com.lifepharmacy.application.databinding.DailogAnimationOfferCompleteCouponBinding
import com.lifepharmacy.application.databinding.DailogInappmessageBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.model.inappmsg.Action_value
import com.lifepharmacy.application.model.inappmsg.Inappmsgrating
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Address
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet
import com.lifepharmacy.application.ui.home.viewModel.HomeViewModel
import com.lifepharmacy.application.ui.livechat.LiveChatActivity
import com.lifepharmacy.application.ui.orders.dailogs.ClickReturnProcessingDialog
import com.lifepharmacy.application.ui.orders.fragments.MainOrdersFragment
import com.lifepharmacy.application.ui.pcr.fragments.PcrListFragment
import com.lifepharmacy.application.ui.productList.ProductListFragment
import com.lifepharmacy.application.ui.products.fragment.ProductFragment
import com.lifepharmacy.application.ui.products.viewmodel.ProductViewModel
import com.lifepharmacy.application.ui.rewards.fragment.RewardsDetailFragment
import com.lifepharmacy.application.ui.utils.dailogs.ClickAnimationComboActionDialog
import com.lifepharmacy.application.utils.CalculationUtil
import com.lifepharmacy.application.utils.universal.IntentAction
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class InappmessageDialog : BaseDialogFragment<DailogInappmessageBinding>(),
  ClickReturnProcessingDialog, ClickInappmsg {
  private val viewModel: HomeViewModel by activityViewModels()
  private val productViewModel: ProductViewModel by activityViewModels()
  var onClickAnimationComboActionDialog: ClickAnimationComboActionDialog? = null
  fun setAnimationDialogActionListener(dialogResult: ClickAnimationComboActionDialog) {
    onClickAnimationComboActionDialog = dialogResult
  }

  var animation: String = "offer_tick_animation.json"
  var backAnimation: String = "bundle_completed.json"
  var amountTitle: String = "Buy 2 Get 1"
  var imageTitle: String = ""
  private var slug: String? = null
  var Inappmsgrating: Inappmsgrating? = null
  var action_value: ArrayList<Action_value>? = ArrayList()
  var navcart = false
  var apicallcount: Int = 0
  var productcount: Int = 0

  companion object {
    const val TAG = "AnimationDialog"

    fun getBundle(
      data: Inappmsgrating
    ): Bundle {
      val bundle = Bundle()
      data?.let {
        bundle.putString("amountTitle", data.inapp_data.action_button_label)
        bundle.putString("imageTitle", data.inapp_data.imageUrl)
      }
      return bundle
    }

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
    isCancelable = false
    arguments?.let {
      if (it.containsKey("amountTitle")) {
        amountTitle = it.getString("amountTitle")!!
      }
      binding.amountTitle = amountTitle
      if (viewModel.appManager.persistenceManager.Inappmsgrating?.inapp_data?.action_button_label == null) {
        binding.lnBtn.visibility = View.GONE
      } else {
        binding.lnBtn.visibility = View.VISIBLE
      }
      if (!viewModel.appManager.persistenceManager.Inappmsgrating?.inapp_data?.action_button_color.isNullOrEmpty()) {
        var colorbtn =
          viewModel.appManager.persistenceManager.Inappmsgrating?.inapp_data?.action_button_color
        binding.btn.setTextColor(Color.parseColor("$colorbtn"))
      } else {
        binding.btn.setTextColor(Color.parseColor("#ffffff"))
      }

//      binding.btn.setBackgroundColor(viewModel.appManager.persistenceManager.Inappmsgrating?.inapp_data?.action_button_bg_color!!?.toInt())
//      binding.btn.setTextColor(viewModel.appManager.persistenceManager.Inappmsgrating?.inapp_data?.action_button_color!!?.toInt())
      binding.backcolor =
        viewModel.appManager.persistenceManager.Inappmsgrating?.inapp_data?.action_button_bg_color.toString()



      if (it.containsKey("imageTitle")) {
        imageTitle = it.getString("imageTitle")!!
      }
      binding.imageTitle = imageTitle
      binding.imageView.layout(0, 0, 0, 0);
    }


  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//    setStyle(STYLE_NO_TITLE, R.style.FullScreenTransparentDialogTheme)
    setStyle(STYLE_NO_TITLE, R.style.FullScreenTransDialogTheme)
  }

  private fun initUI() {
    binding.click = this
  }

  override fun getLayoutRes(): Int {
    return R.layout.dailog_inappmessage
  }

  override fun onClickView() {
    findNavController().navigateUp()
  }

  private fun navigationForScreens(screen: String) {
    when (screen) {
      "vouchers" -> {
        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
          findNavController().navigate(R.id.nav_voucher)
        } else {
          findNavController().navigate(R.id.nav_login_sheet)
        }

      }
      "prescription_request" -> {
        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
          findNavController().navigate(R.id.nav_prescription)
        } else {
          findNavController().navigate(R.id.nav_login_sheet)
        }
      }
      "rewards" -> {
        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
          findNavController().navigate(R.id.nav_rewards)
        } else {
          findNavController().navigate(R.id.nav_login_sheet)
        }
      }
      "orders" -> {
        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
          findNavController().navigate(R.id.nav_orders)
        } else {
          findNavController().navigate(R.id.nav_login_sheet)
        }

      }
      "prescription_requests" -> {
        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
          findNavController().navigate(R.id.nav_orders, MainOrdersFragment.getBundle(1))
        } else {
          findNavController().navigate(R.id.nav_login_sheet)
        }
      }
      "wallet" -> {
        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
          findNavController().navigate(R.id.nav_wallet)
        } else {
          findNavController().navigate(R.id.nav_login_sheet)
        }
      }
      "cart" -> {
        findNavController().navigate(R.id.nav_cart)
      }
      "account" -> {
        requireActivity().findNavController(R.id.fragment).navigate(R.id.nav_profile)
      }
      "chat" -> {
        openLiveChat()
      }

    }
  }

  private fun openLiveChat() {
    LiveChatActivity.open(
      activity = requireActivity(),
      name = viewModel.appManager.persistenceManager.getLoggedInUser()?.name ?: "",
      email = viewModel.appManager.persistenceManager.getLoggedInUser()?.email ?: "",
      phone = viewModel.appManager.persistenceManager.getLoggedInUser()?.phone ?: "",
    )
  }

  fun moveToPage(id: String, title: String? = "") {
    findNavController().navigate(
      R.id.homeLandingFragment, HomeLandingFragment.getLandingPageBundle(title, id)
    )
  }

  fun onClickHomeSubItemInapp(
    title: String?,
    id: String?,
    type: String?
  ) {
    if (id != null && type != null) {
//      viewModel.appManager.filtersManager.addFirstFilter(id, type)
    }
    type?.let {
      if (type == "screen") {
        if (id != null) {
          navigationForScreens(id)
        }

      } else if (type == "test-booking-screen") {
        if (!id.isNullOrEmpty()) {
          viewModel.appManager.persistenceManager.collection_id = id
          if (viewModel.appManager.persistenceManager.isLoggedIn()) {
            viewModel.appManager.persistenceManager.pcritemselected.clear()
            findNavController().navigate(
              R.id.nav_pcr, PcrListFragment.getPcrListingBundle(id)
            )
          } else {
            findNavController().navigate(R.id.nav_login_sheet)
          }
        }

      } else if (type == "page") {
        if (id != null) {
          if (title == null) {
            moveToPage(id)
          } else {
            moveToPage(id, title)
          }
        }
      } else if (type == "search") {
        try {
          findNavController().navigate(R.id.nav_search)
        } catch (e: Exception) {

        }
      } else if (type == "link") {
        try {
          if (id != null) {
            IntentAction.openLink(id, requireActivity())
          }
//          val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(id))
//          startActivity(browserIntent)
        } catch (e: Exception) {
          e.printStackTrace()
        }

      } else if (type == "products") {
        findNavController().navigate(
          R.id.nav_product_listing, ProductListFragment.getProductListingBundle(title, id, type)
        )
      } else {
        if (type == "product") {
          if (id.isNullOrEmpty()) {
            findNavController().navigate(
              R.id.nav_product_listing
            )
          } else {
            findNavController().navigate(
              R.id.nav_product,
              ProductFragment.getBundle(id, 0)
            )
          }
        } else {
          Log.e(TAG, "onClickHomeSubItemInapp: $id")
          if (!id.isNullOrEmpty()) {
            findNavController().navigate(
              R.id.nav_product_listing, ProductListFragment.getProductListingBundle(title, id, type)
            )
          }
        }
      }

    }
  }


  override fun onClickContinue() {
    if (viewModel.appManager.persistenceManager.Inappmsgrating?.inapp_data?.action_key == "add-to-cart") {
      productcount =
        viewModel.appManager.persistenceManager.Inappmsgrating?.inapp_data?.action_value?.size!!

      viewModel.appManager.persistenceManager.Inappmsgrating?.inapp_data?.action_value!!.forEach { product ->
//        apicallcount += 1
        productObserver(product.id)

      }
    } else {
      onClickHomeSubItemInapp(
        viewModel.appManager.persistenceManager.Inappmsgrating?.inapp_data?.action_title,
        viewModel.appManager.persistenceManager.Inappmsgrating?.inapp_data?.action_value?.get(0)?.id,
        viewModel.appManager.persistenceManager.Inappmsgrating?.inapp_data?.action_key
      )
    }
  }

  private fun productObserver(productID: String) {
    productID?.let { it ->
      productViewModel.getProductDetails(it).observe(viewLifecycleOwner, Observer { result ->
        result?.let {
          when (result.status) {
            Result.Status.SUCCESS -> {

              result.data?.data?.let { it1 ->
                productViewModel.mainProductMut.value = it1
                //event tracking
                viewModel.appManager.eventlistmainManager.eventTrakingItemAddedToCart(it1.productDetails)
                //adding all products in cart
                viewModel.appManager.offersManagers.addProduct(
                  requireActivity(),
                  it1.productDetails
                )
                apicallcount += 1
                navcart = true

                if (apicallcount == productcount) {
                  findNavController().navigate(R.id.nav_cart)
                }


//                findNavController().navigate(R.id.nav_cart)
//                fun onCLickAddToCart() {
//                  productDetailsGlobal?.let {
//                    viewModel.appManager.offersManagers.addProduct(
//                      requireActivity(), it.productDetails, viewModel.qty.get()?.toInt()!!
//                    )
//                  }
//                }
//                fun onCLickAddAllToCart() {
//                  for (item in viewModel.addedProducts) {
//                    viewModel.appManager.offersManagers.addProduct(requireActivity(), item)
//                  }
//                }
              }
//              hideLoading()
            }
            Result.Status.ERROR -> {
              apicallcount += 1
              navcart = true
//              hideLoading()
              result.message?.let { it1 ->
                AlertManager.showErrorMessage(
                  requireActivity(),
                  it1
                )
              }
            }
            Result.Status.LOADING -> {
//              showLoading()
            }
          }
        }
      })
    }
  }

  override fun onClickLater() {
    findNavController().navigateUp()
  }

  override fun onClickClaim() {
    findNavController().navigateUp()
    requireActivity().findNavController(R.id.fragment).navigate(R.id.nav_offers)
  }
}


