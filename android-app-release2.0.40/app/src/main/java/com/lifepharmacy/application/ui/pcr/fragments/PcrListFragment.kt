package com.lifepharmacy.application.ui.pcr.fragments

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentPcrListBinding
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.documents.viewmodel.DocumentsViewModel
import com.lifepharmacy.application.managers.analytics.PcrListScreenOpen
import com.lifepharmacy.application.model.CartAll
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.docs.DocumentModel
import com.lifepharmacy.application.model.pcr.pcrdatetime.Booking_slots
import com.lifepharmacy.application.model.pcr.pcrlist.Products
import com.lifepharmacy.application.model.product.ProductDetails

import com.lifepharmacy.application.ui.address.AddressSelectionActivity
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.address.ClickSelectedAddress
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.pcr.adapters.ClickPcrProduct

import com.lifepharmacy.application.ui.pcr.viewmodel.PcrListingViewModel
import com.lifepharmacy.application.ui.prescription.fragments.ClickPrescriptionFiles

import com.lifepharmacy.application.ui.products.fragment.ProductFragment
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.rewards.adapters.PcrAdapter
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.*
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Logger
import com.lifepharmacy.application.utils.universal.RecyclerPagingListener
import com.lifepharmacy.application.utils.universal.RecyclerViewPagingUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class PcrListFragment : BaseFragment<FragmentPcrListBinding>(),
  ClickPcrListFragment, ClickTool, ClickSelectedAddress, ClickPcrProduct,
  ClickPrescriptionFiles, RecyclerPagingListener {
  companion object {
    fun getPcrListingBundle(
      id: String?
    ): Bundle {
      val bundle = Bundle()
      id?.let {
        bundle.putString("id", id)
      }
      return bundle
    }
  }

  private val viewModel: PcrListingViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  private val docsViewModel: DocumentsViewModel by activityViewModels()
  private val viewModelAddress: AddressViewModel by activityViewModels()
  private lateinit var PcrAdapter: PcrAdapter
  var firstTimeLoading = false
  var pcrtitle: String = ""
  var productselct: ArrayList<Products>? = ArrayList()
  private lateinit var recyclerViewPagingUtil: RecyclerViewPagingUtil
  var productSelected: Products? = null
  private var layoutManager: GridLayoutManager? = null
  lateinit var intentHandler: IntentHandler
  lateinit var intentStarter: IntentStarter
  private val addressContract =
    registerForActivityResult(AddressSelectionActivity.Contract()) { result ->
      result?.address?.let {
        viewModelAddress.deliveredAddressMut.value = it
        viewModelAddress.addressSelection = AddressSelection.NON
      }
    }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Override this method to customize back press
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      handleBackPress()
    }

    if (savedInstanceState == null) {
//      clearing the list when page opened for first time
//      appManager.persistenceManager.pcritemselected.clear()
      Log.e(TAG, "onCreate: inside saveinstance")


    }

  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.PcrListScreenOpen()
    arguments?.let {
      viewModel.colid = it.getString("id")
    }

    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      firstTimeLoading = true
      initUI()
//      observers()
    }
    if (appManager.persistenceManager.pcritemselected.size > 0) {
      binding.cvPricedetail.visibility = View.VISIBLE
      binding.selectpcrtxt.visibility = View.GONE
    } else {
      binding.cvPricedetail.visibility = View.GONE
      binding.selectpcrtxt.visibility = View.VISIBLE
    }
//    initUI()
    reselectionObserver()
    return mView
  }

  private fun initUI() {
    binding.viewModel = viewModel
    binding.profileViewModel = profileViewModel
    binding.lifecycleOwner = this
    binding.click = this
    binding.tollBar.click = this
    binding.tollBar.tvToolbarTitle.text = getString(R.string.covidrtpcr)
    binding.btnCheckOut.text = "SELECT TIME SLOT"
//    appManager.persistenceManager.pcritemselected.clear()
    Log.e(TAG, "initUI: ${appManager.persistenceManager.pcritemselected}")


//    var imageUrl = appManager.storageManagers.config.prescriptionBanner ?: ""
    initProductsListRV()
    if (firstTimeLoading) {
      observers()
    }
//    observers()
    observersDate()
    observersAddresssNew()


  }

  fun reselectionObserver() {
    viewModel.appManager.loadingState.homeReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_home)
          viewModel.appManager.loadingState.homeReselected.value = false
        }
      }
    }
  }

  private fun initProductsListRV() {
    PcrAdapter = PcrAdapter(requireActivity(), this, viewModel.appManager)
    layoutManager = GridLayoutManager(requireContext(), 1)

    binding.recyclerViewProducts.adapter = PcrAdapter

    recyclerViewPagingUtil = RecyclerViewPagingUtil(
      binding.recyclerViewProducts,
      layoutManager!!, this
    )

    binding.recyclerViewProducts.addOnScrollListener(recyclerViewPagingUtil)
    binding.recyclerViewProducts.post { // Call smooth scroll
      binding.recyclerViewProducts.scrollToPosition(0)
    }
  }


  private fun observers() {
    Log.e(TAG, "observers: api call")
    val gson = Gson()
    viewModel.getPcrProducts().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.data?.images?.let { data ->
              var imageUrl = it.data.data!!.images.banner ?: ""

              Log.e("shifa", "initUI: " + imageUrl)
              try {
                if (imageUrl.isNotEmpty()) {
                  activity?.let { Glide.with(it).load(imageUrl).into(binding.pcrbannerimg) }
                }
              } catch (e: Exception) {
              }

            }
            it.data?.data?.products?.let { data ->
//              clearing arraylist before the list loading
//              appManager.persistenceManager.pcritemselected.clear()
              binding.recyclerViewProducts.adapter = PcrAdapter
              PcrAdapter.setDataChanged(it.data?.data?.products)
//              if (data.isNotEmpty()) {
//                if (productListAdapter.arrayList?.contains(data.first()) == true) {
//                  Toast.makeText(requireContext(), "productExists", Toast.LENGTH_LONG).show()
//                }
//              }
            }
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
            recyclerViewPagingUtil.isLoading = true
          }
        }
      }
    })
  }

  private fun observersDate() {
    val gson = Gson()
    viewModel.getPcrDate().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            var appoinmentinfo = it.data?.data?.appointment_info
            appManager.persistenceManager.appointmentinfo = appoinmentinfo.toString()
            appManager.persistenceManager.bookingslots = it.data?.data?.booking_slots!!
            Log.e(TAG, "observersDate: ${appManager.persistenceManager.bookingslots}")
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
            recyclerViewPagingUtil.isLoading = true
          }
        }
      }
    })
  }

  private fun observersAddresssNew() {
    val gson = Gson()
    viewModel.getPcrAddress().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            appManager.persistenceManager.addressesglobal = it.data?.data?.addresses
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
            recyclerViewPagingUtil.isLoading = true
          }
        }
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_pcr_list
  }

  override fun permissionGranted(requestCode: Int) {

  }


  override fun onClickBack() {
    findNavController().navigateUp()
  }

  private fun handleBackPress() {
    findNavController().navigateUp()
  }

  override fun onClickClaimInsurance() {
//    viewModel.isClaimInsurance.set(true)
  }

  override fun onClickUploadImage() {

  }

  override fun onClickUploadCardBackImage() {
  }

  override fun onClickUploadCardFrontImage() {

  }

  override fun onClickUploadInsurance() {

  }

  override fun onClickUploadInsuranceBack() {

  }

  override fun onNotClaimInsurance() {
//    viewModel.isNotClaimInsurance.set(true)
  }

  override fun onClickProceed() {
//    findNavController().navigate(R.id.pcrDetailsBottomSheet)
  }

  override fun onClickNext() {
//    findNavController().navigate(R.id.pcrdateFragment)
    findNavController().navigate(
      R.id.pcrdateFragment, PcrDateFragment.getPcrListingBundle(viewModel.colid)
    )
  }

  override fun onClickLogin() {
    findNavController().navigate(R.id.nav_login_sheet)
  }

  override fun onClickMayBeLater() {

  }


  private fun proceedButtonStatus() {
//    viewModel.isProceedEnable.value =
//      !(viewModel.cardFrontUrl.get().isNullOrEmpty() || viewModel.cardBackUrl.get()
//        .isNullOrEmpty() || viewModel.filesListLive.value.isNullOrEmpty())
  }

  override fun onClickChangeAddress() {
    viewModelAddress.isSelecting.set(true)
    addressContract.launch(true)
  }

  private fun docSelected(doc: DocumentModel) {
    docsViewModel.selectedDoc.value = doc
  }

  override fun onClickPcrListSelected(position: Int, rootCategory: Products) {
    productSelected = rootCategory
    PcrAdapter.setItemSelected(position)

  }

  override fun onProductClicked(productDetails: Products, position: Int) {
//    findNavController().navigate(
//      R.id.pcrDetailsBottomSheet


//    )
  }

  override fun onResume() {
    super.onResume()
    Log.e(TAG, "onResume:")
    PcrAdapter.notifyDataSetChanged()

//    observers()
    refresh()
  }

  fun refresh() {
    binding.tvAmouncal.text = appManager.persistenceManager.offerPrice.currencyFormat()
    binding.tvSubtotal.text =
      (appManager.persistenceManager.subtotal.plus(appManager.persistenceManager.vattotal)).currencyFormat()
    binding.tvSubtotal.paintFlags =
      binding.tvSubtotal.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG)

  }


  override fun onPcrAddClicked(position: Int, rootCategory: Products) {
    binding.cvPricedetail.visibility = View.VISIBLE
    binding.selectpcrtxt.visibility = View.GONE

    appManager.persistenceManager.addProductfirst(rootCategory)
    refresh()
  }

  override fun onPcrAddbtnClicked(position: Int, rootCategory: Products) {
    appManager.persistenceManager.onPcrAddbtnClicked(position, rootCategory)
    refresh()

  }

  override fun onPcrMinusbtnClicked(position: Int, rootCategory: Products) {

    appManager.persistenceManager.onPcrMinusbtnClicked(position, rootCategory)

    if (appManager.persistenceManager.pcritemselected.isNullOrEmpty()) {
      Log.e(TAG, "onPcrMinusbtnClicked: ")
      binding.cvPricedetail.visibility = View.GONE
      binding.selectpcrtxt.visibility = View.VISIBLE
    } else {
      Log.e(TAG, "onPcrMinusbtnClicked: 11")
      binding.cvPricedetail.visibility = View.VISIBLE
      binding.selectpcrtxt.visibility = View.GONE
    }

    refresh()
  }

  override fun onPcrProductClicked(productDetails: Products, position: Int) {
    viewModel.position = position
    viewModel.previewProductMut.value = productDetails
//    findNavController().navigate(
//      R.id.pcrDetailsBottomSheet,
//      PcrDetailsBottomSheet.getBundle(productID = productDetails.id, position)
//    )
    findNavController().navigate(
      R.id.pcrDetailsBottomSheet
    )
  }

  override fun onClickAddProduct(productDetails: Products, position: Int) {

  }

  override fun onClickMinus(productDetails: Products, position: Int) {

  }

  override fun onClickPlus(productDetails: Products, position: Int) {

  }

  override fun onClickWishList(productDetails: Products, position: Int) {

  }

  override fun onClickNotify(productDetails: Products, position: Int) {

  }

  override fun onClickOrderOutOfStock(productDetails: Products, position: Int) {

  }

  fun updateBottomPriceValue() {
    var offerPrice: Double = 0.0
    var pricevalue: Double
    var vat_total: Double = 0.0
    var subtotal: Double = 0.0
    var discount_total: Double = 0.0

    appManager.persistenceManager.pcritemselected!!.forEach { product ->
      var regprice = product.prices[0].price.regularPrice
      var rp = PricesUtil.getUnitPcrPrice(product.prices, requireContext())
      if (product.offers != null) {
        discount_total += rp * (product.offers?.value!!).div(100).times(product.selected_qty)
      }

      subtotal += rp.times(product.selected_qty)

      if (product.offers != null) {
        vat_total += ((rp - (rp * (product.offers?.value!!).div(100)))
          .times(product.vat_percentage).div(100)).times(product.selected_qty)
      } else {
        vat_total += (rp.times(product.vat_percentage).div(100)).times(product.selected_qty)
      }

      if (product.offers != null && product.offers.type == "flat_percentage_discount") {
        var percentagePrice = (PricesUtil.getUnitPcrPrice(
          product.prices,
          requireContext()
        ) * product.offers.value!!) / 100
        var discountedPrice =
          PricesUtil.getUnitPcrPrice(product.prices, requireContext()) - percentagePrice

        if (product != null) {
          var amountAfterVat = CalculationUtil.getPcrPriceAfterVATOGive(discountedPrice, product)
          pricevalue = amountAfterVat
        } else {
          pricevalue = discountedPrice
        }
      } else {
        if (product != null) {
          var amountAfterVat = CalculationUtil.getPcrPriceAfterVATOGive(regprice, product)
          pricevalue = amountAfterVat
        } else {
          pricevalue = regprice
        }
      }
      offerPrice += pricevalue.times(product.selected_qty)
      Log.e(TAG, "updateBottomPriceValue: offerPrice$offerPrice")
    }
    binding.tvAmouncal.text = offerPrice.currencyFormat()
    appManager.persistenceManager.subtotal = subtotal.currencyFormat().toDouble()

    appManager.persistenceManager.discounttotal = discount_total.currencyFormat().toDouble()
    appManager.persistenceManager.vattotal = vat_total.currencyFormat().toDouble()
    appManager.persistenceManager.totalwithoutadditonal = (subtotal - discount_total + vat_total)
    binding.tvSubtotal.text = (subtotal + vat_total).currencyFormat()
    binding.tvSubtotal.paintFlags =
      binding.tvSubtotal.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG)


//    appManager.persistenceManager.pcritemselected!!.forEach { product ->
//

//
//      binding.tvAmouncal.text = offerPrice.toString()
//      appManager.persistenceManager.totalamount.equals(offerPrice)
//    }
  }

  override fun onNextPage(skip: Int, take: Int) {

  }
}


//  override fun onProductClicked(productDetails: ProductDetails, position: Int) {
//    viewModel.position = position
//    viewModel.previewProductMut.value = productDetails
//    findNavController().navigate(R.id.productPreviewBottomSheet)
////    findNavController().navigate(
////      R.id.productFragment,
////      getBundle(
////        productID = productDetails.id
////      )
////    )
//  }


//  ProductFragment.getBundle(productID = productDetails.id, position)

//  override fun onClickAddProduct(productDetails: ProductDetails, position: Int) {
//    viewModel.offersManagers.addProduct(requireActivity(), productDetails, position)
////    refreshItemProducts()
//  }