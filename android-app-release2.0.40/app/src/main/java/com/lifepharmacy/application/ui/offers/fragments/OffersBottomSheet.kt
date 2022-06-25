package com.lifepharmacy.application.ui.offers.fragments

import android.animation.Animator
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomWithLoading
import com.lifepharmacy.application.databinding.BottomSheetOffersBinding
import com.lifepharmacy.application.enums.ScrollingState
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.offersBottomSheetScreenOpen
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.cart.OffersCartModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.cart.adapter.ClickCartProduct
import com.lifepharmacy.application.ui.offers.adapters.CartOfferBottomChildProductsAdapter
import com.lifepharmacy.application.ui.offers.adapters.OfferBottomProductAdapter
import com.lifepharmacy.application.ui.offers.viewmodel.OffersViewModel
import com.lifepharmacy.application.ui.products.viewmodel.ProductViewModel
import com.lifepharmacy.application.utils.universal.DebouncingQueryTextListener
import com.lifepharmacy.application.utils.universal.Logger
import com.lifepharmacy.application.utils.universal.SearchDebounceListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class OffersBottomSheet : BaseBottomWithLoading<BottomSheetOffersBinding>(), ClickCartProduct,
  ClickOfferBottomSheet {

  var offerId: String? = "offerId"

  private lateinit var cartOfferBottomSheetAdapter: CartOfferBottomChildProductsAdapter
  private lateinit var offerBottomProductAdapter: OfferBottomProductAdapter

  private val viewModel: OffersViewModel by activityViewModels()
  private val productViewModel: ProductViewModel by activityViewModels()
  private var pastVisibleItems: Int = 0
  private var visibleItemCount: Int = 0
  private var totalItemCount: Int = 0
  private var previous_total: Int = 0
  private var isLoading = true
  private var layoutManager: GridLayoutManager? = null

  companion object {

//    fun getOfferSheetBundle(offerId: String): Bundle {
//      val bundle = Bundle()
//      bundle.putString("offerId", offerId)
//      return bundle
//    }
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.appManager.analyticsManagers.offersBottomSheetScreenOpen()
    initLayout()
    observers()
    isCancelable = true
    viewModel.skip = 0
    getProductsObserver()
    viewModel.offersManagers.inComboSheet = true
    findNavController().addOnDestinationChangedListener { _, destination, _ ->
      when (destination.id) {
        R.id.productFragment -> {
          dismiss()
        }
      }
    }
  }

  fun initLayout() {
//    binding.searchViewQuery.isIconified = false
    binding.click = this
    initSearchView()
//    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//      binding.svMain.setOnScrollChangeListener(
//        ScrollStateListener(viewModel.getScrollStateRepo(), binding.svMain)
//      )
//    }
    scrollObserver()
    initProductRV()
    reselectionCartObserver()
    viewModel.searchQuery = ""
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
  private fun initProductRV() {
    offerBottomProductAdapter =
      OfferBottomProductAdapter(requireActivity(), this, viewModel.appManager)
    layoutManager = GridLayoutManager(requireContext(), 1)
    binding.rvProductList.adapter = offerBottomProductAdapter
    binding.rvProductList.layoutManager = layoutManager
    binding.rvProductList.adapter = offerBottomProductAdapter
    val animator: RecyclerView.ItemAnimator? = binding.rvProductList.itemAnimator
    if (animator is SimpleItemAnimator) {
      animator.supportsChangeAnimations = false
    }
    binding.rvProductList.itemAnimator?.changeDuration = 0
    binding.rvProductList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        visibleItemCount = layoutManager!!.childCount
        totalItemCount = layoutManager!!.itemCount
        pastVisibleItems = layoutManager!!.findFirstCompletelyVisibleItemPosition()
        val visibleItem = layoutManager?.findLastCompletelyVisibleItemPosition()
        if (visibleItem != null) {
          if (visibleItem > viewModel.skip + 5 - viewModel.take) {
          }
//            onBottomHit()
        }
//        if (!binding.recyclerViewProducts.canScrollVertically(1)) {
//          onBottomHit()
//        }
      }
    })
  }

  private fun initOfferCartProductsRV(offerModel: OffersCartModel) {
    cartOfferBottomSheetAdapter =
      CartOfferBottomChildProductsAdapter(requireActivity(), this, offerModel.getSlotsCount())
    binding.rvCartProducts.adapter = cartOfferBottomSheetAdapter
    cartOfferBottomSheetAdapter.setDataChanged(offerModel)
    try {
      binding.rvCartProducts.smoothScrollToPosition(offerModel.getSlotsCount().minus(1))
    } catch (e: Exception) {
      e.printStackTrace()
    }

  }

  private fun initSearchView() {
    binding.searchViewQuery.setOnQueryTextListener(
      DebouncingQueryTextListener(
        viewLifecycleOwner.lifecycle,
        object : SearchDebounceListener {
          override fun onDebouncingQueryTextChange(text: String?) {
            text?.let {
              if (it.isNotEmpty()) {
                viewModel.skip = 0
                viewModel.searchQuery = it
                getProductsObserver()
              }
            }
          }

          override fun onSimpleTextChange(text: String?) {
            binding.searchViewQuery.isIconified = false

          }
        })
    )
    binding.searchViewQuery.setOnCloseListener {
//      binding.showRecommendations = true
      false
    }
  }

  fun observers() {
    viewModel.offersManagers.offersArrayMut.observe(viewLifecycleOwner, Observer {
      it?.let {
        val offerModel =
          it.firstOrNull { listModel -> listModel.offers?.id == viewModel.offersManagers.selectedOffer.value?.offers?.id }
        if (offerModel != null) {
          viewModel.offersManagers.selectedOffer.value = offerModel
        }
      }
    })
    viewModel.offersManagers.selectedOffer.observe(viewLifecycleOwner, Observer {
      it?.let {
        binding.item = it
        if (it.products.isEmpty()) {
          findNavController().navigateUp()
        } else {
          initOfferCartProductsRV(it)
        }

      }
    })
    viewModel.appManager.loadingState.getAnimationState().observe(this, Observer {
      it?.let {
        if (it) {
          showAnimations()
        } else {
          hideAnimations()
        }
      }
    })
  }

  private fun showAnimations() {
    binding.animationView.visibility = View.VISIBLE
    binding.animationView.setAnimation(viewModel.appManager.loadingState.animation)
    binding.animationView.repeatCount = 0
    binding.animationView.playAnimation()
    binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationStart(animation: Animator?) {
        Log.e("Animation:", "start")
      }

      override fun onAnimationEnd(animation: Animator?) {
        Log.e("Animation:", "end")
        //Your code for remove the fragment
        viewModel.appManager.loadingState.setAnimationState(false)
      }

      override fun onAnimationCancel(animation: Animator?) {
        Log.e("Animation:", "cancel")
      }

      override fun onAnimationRepeat(animation: Animator?) {
        Log.e("Animation:", "repeat")
      }
    })
  }

  private fun hideAnimations() {
    binding.animationView.visibility = View.GONE
  }

  private fun getProductsObserver() {
    viewModel.getProducts().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.data?.data?.products?.let { data ->
              viewModel.skip = viewModel.skip + viewModel.take
              previous_total = data.size
            }

            isLoading = false
            it.data?.data?.products?.let { it1 -> offerBottomProductAdapter.refreshData(it1) }
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
          }
        }
      }
    })
  }

  private fun scrollObserver() {
    viewModel.getScrollStateMut()
      .observe(viewLifecycleOwner, Observer {
        it?.let {
          when (it) {
            ScrollingState.ON_SCROLL -> {
            }
            ScrollingState.SCROLL_DOWN -> {
            }
            ScrollingState.SCROLL_UP -> {
            }
            ScrollingState.HIT_BOTTOM -> {
              onBottomHit()
              Logger.d("state", "Hit BOttom")
            }
            else -> {
            }
          }
        }
      })
  }

  private fun onBottomHit() {
    if (isLoading) {
      if (totalItemCount > previous_total) {
        isLoading = false
        previous_total = totalItemCount
      }
    }
    if (!isLoading && totalItemCount - visibleItemCount <= pastVisibleItems + viewModel.take) {
      pastVisibleItems++
      getProductsObserver()
      isLoading = true
    }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog: Dialog = super.onCreateDialog(savedInstanceState)
    dialog.setOnShowListener { dialogInterface ->
      val bottomSheetDialog = dialogInterface as BottomSheetDialog
      setupFullHeight(bottomSheetDialog)
    }
    return dialog
  }

  private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
    val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
    val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
    val layoutParams = bottomSheet.layoutParams
    val windowHeight = getWindowHeight()
    if (layoutParams != null) {
      layoutParams.height = windowHeight
    }
//    bottomSheet.layoutParams = layoutParams
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
  }

  private fun getWindowHeight(): Int {
    // Calculate window height for fullscreen use
    val displayMetrics = DisplayMetrics()
    requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_offers
  }


  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickPlus(productDetails: ProductDetails, position: Int) {
    //event tracking
    viewModel.appManager.eventlistmainManager.eventTrakingItemAddedToCart(productDetails)
    viewModel.offersManagers.addProduct(requireActivity(), productDetails, position)
  }

  override fun onCancel(dialog: DialogInterface) {
    viewModel.offersManagers.inComboSheet = false
    super.onCancel(dialog)
  }

  override fun onDismiss(dialog: DialogInterface) {
    viewModel.offersManagers.inComboSheet = false
    super.onDismiss(dialog)
  }

  override fun onClickMinus(productDetails: ProductDetails, position: Int) {
    //event tracking
    viewModel.appManager.eventlistmainManager.eventTrakingItemRemovedFromCart(productDetails)
    viewModel.offersManagers.removeProduct(requireActivity(), productDetails, position)
    offerBottomProductAdapter.notifyDataSetChanged()
  }

  override fun onClickRemove(productDetails: ProductDetails, position: Int) {
    //event tracking
    viewModel.appManager.eventlistmainManager.eventTrakingItemRemovedFromCart(productDetails)
    viewModel.offersManagers.removeProduct(requireActivity(), productDetails, position)
  }

  override fun onClickProduct(productDetails: ProductDetails, position: Int) {
    try {
      productViewModel.position = position
      productViewModel.previewProductMut.value = productDetails
      findNavController().navigate(R.id.productPreviewBottomSheet)
    } catch (e: Exception) {
      e.printStackTrace()
    }

  }

  override fun onClickChecked(cartModel: CartModel, position: Int) {

  }

  override fun onClickEmptyClicked(offerModel: OffersCartModel, position: Int) {
    binding.rvProductList.alpha = 0.5F
    CoroutineScope(Dispatchers.Main.immediate).launch {
      delay(200)
      binding.rvProductList.alpha = 1.0F
    }

  }

  override fun onClickNotify(productDetails: ProductDetails, position: Int) {
    notifyAboutProduct(productDetails)
  }

  override fun getLoadingLayout(): ConstraintLayout {
    return binding.llLoading.clLoading
  }

  override fun onClickDone() {
    viewModel.offersManagers.inComboSheet = false
    findNavController().navigateUp()
  }

  override fun onClickCheckOut() {
    findNavController().navigate(R.id.nav_cart)
  }

  override fun onClickSearch() {
    binding.searchViewQuery.isIconified = false
  }

  private fun notifyAboutProduct(productDetails: ProductDetails) {
    productViewModel.notifyProduct(productDetails).observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.message?.let { it1 -> AlertManager.showSuccessMessage(requireActivity(), it1) }
          }
          Result.Status.ERROR -> {
            it.message?.let { it1 -> AlertManager.showErrorMessage(requireActivity(), it1) }
            hideLoading()
          }
          Result.Status.LOADING -> {
            showLoading()
          }
        }
      }
    })
  }


}