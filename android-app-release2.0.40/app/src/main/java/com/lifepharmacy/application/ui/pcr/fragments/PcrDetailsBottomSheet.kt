package com.lifepharmacy.application.ui.pcr.fragments

import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.Constants
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpRatioScreenSheet
import com.lifepharmacy.application.databinding.BottomSheetPcrDetailsBinding
import com.lifepharmacy.application.enums.ProductDetailSelectedState
import com.lifepharmacy.application.managers.analytics.productScreenOpen
import com.lifepharmacy.application.managers.analytics.productViewed
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.pcr.viewmodel.PcrListingViewModel
import com.lifepharmacy.application.utils.PricesUtil
import com.lifepharmacy.application.utils.universal.IntentAction
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class PcrDetailsBottomSheet :
  BaseBottomUpRatioScreenSheet<BottomSheetPcrDetailsBinding>(0.7),
  ClickPcrProductFragment {
  companion object {
    fun getBundle(productID: String, position: Int): Bundle {
      val bundle = Bundle()
      bundle.putString("id", productID)
      bundle.putInt("position", position)
      return bundle
    }
  }

  private val viewModel: PcrListingViewModel by activityViewModels()
  private var productID: String? = null
  private var slug: String? = null
  private var position: Int? = 0
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCancelable = true
    arguments?.let {
      productID = it.getString("id")
      slug = it.getString("slug")
      position = it.getInt("position", 0)
      viewModel.appManager.analyticsManagers.productScreenOpen(productID.toString())
    }
    initLayout()
    observers()
  }

  private fun initLayout() {
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.click = this

    Log.e(TAG, "initLayout: shifa")
    viewModel.previewProductMut.value?.let { productDetails ->
      productDetails.let { productDetails1 ->
        appManager.offersManagers.checkAndGetExistingPcrQTY(productDetails)?.let {
          viewModel.previewQTY.value = it.toString()
        }
        viewModel.selectedDetails.value = ProductDetailSelectedState.OVERVIEW
        productDetails.short_description?.let { updateProductDescription(it) }
        viewModel.previewPrice.value =
          PricesUtil.getRelativePcrPrice(productDetails.prices, requireContext())
        binding.imageCarouselProduct.addData(viewModel.getPcrProductSliderList(productDetails1))
//        binding.customIndicator.let {
//          binding.imageCarouselProduct.setIndicator(
//            it
//          )
//        }
      }

      binding.tvPricess.paintFlags =
        binding.tvPricess.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG)
//      viewModel.isPreviewProductCart.value =
//        appManager.offersManagers.checkIfProductAlreadyExistInCart(productDetails)
//      setAnalytics(productDetails)
    }


  }

//  private fun setAnalytics(productDetails: ProductDetails) {
//    productDetails?.let {
//      viewModel.appManager.analyticsManagers.productViewed(
//        it, viewModel.position
//      )
//    }
//  }

  private fun observers() {
    viewModel.appManager.offersManagers.cartQtyCountMut.observe(this, Observer {
      it?.let {
        if (it > 0) {
          initLayout()
        }

      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_pcr_details
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickOverview() {
    Log.e(TAG, "onClickOverview: shifa")
    viewModel.selectedDetails.value = ProductDetailSelectedState.OVERVIEW
    viewModel.previewProductMut.value?.short_description?.let { updateProductDescription(it) }
  }

  override fun onClickDetails() {
    viewModel.selectedDetails.value = ProductDetailSelectedState.DETAIL
    viewModel.previewProductMut.value?.description?.let { updateProductDescription(it) }
  }

  override fun onClickMoreInfo() {
    viewModel.selectedDetails.value = ProductDetailSelectedState.MORE_INFO
    viewModel.previewProductMut.value?.inventory?.sku?.let {
      val string = "SKU: $it"
      updateProductDescription(string)
    }
  }

  override fun onClickAddToWishList() {

  }

//  override fun onClickAddToWishList() {
//    viewModel.previewProductMut.value?.let {
//      viewModel.appManager.wishListManager.selectUnselected(
//        it
//      )
//      viewModel.isInWishList.value = viewModel.isInWishList.value != true
//
//
//    }
//  }

  override fun onClickPlus() {

//    viewModel.previewProductMut.value?.let {
//      viewModel.appManager.offersManagers.addProduct(requireActivity(), it)
//    }
  }

  override fun onClickAddToCart() {
//    viewModel.previewProductMut.value?.let {
//      viewModel.appManager.offersManagers.addProduct(requireActivity(), it)
//    }
  }

  override fun onClickMinus() {
//    viewModel.previewQTY.value?.let {
//      if (it.toInt() == 1) {
//        MaterialAlertDialogBuilder(
//          requireActivity(),
//          R.style.ThemeOverlay_App_MaterialAlertDialog
//        )
//          .setTitle(requireActivity().resources.getString(R.string.remove_product_title))
//          .setMessage(requireActivity().resources.getString(R.string.remove_product_descr))
//          .setNegativeButton(requireActivity().resources.getString(R.string.cancel)) { dialog, which ->
//          }
//          .setPositiveButton(requireActivity().resources.getString(R.string.remove)) { dialog, which ->
//            viewModel.previewProductMut.value?.let {
//              viewModel.appManager.offersManagers.removeProduct(requireActivity(), it, 0)
//            }
//          }
//          .show()
//      } else {
//        viewModel.previewProductMut.value?.let {
//          viewModel.appManager.offersManagers.removeProduct(
//            requireActivity(), it, 0
//          )
//        }
//      }
//    }


  }

  override fun onAllDetails() {
    try {
      findNavController().navigate(
        R.id.productFragment,
//        viewModel.previewProductMut.value?.id?.let { ProductFragment.getBundle(productID = it, 0) }
      )
    } catch (e: Exception) {
      e.printStackTrace()
    }

  }

  override fun onClickShare() {
    val productLink = Constants.BASE_URL + "'\'${viewModel.previewProductMut.value?.inventory?.sku}"
    IntentAction.sendTextToOtherApps(requireActivity(), productLink)
  }

  private fun updateProductDescription(description: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      binding.lyTitle.tvOverviewTitle.text =
        Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)
    } else {
      binding.lyTitle.tvOverviewTitle.text = (Html.fromHtml(description))
    }
  }

}