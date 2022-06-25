package com.lifepharmacy.application.ui.products.viewmodel

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.ProductDetailSelectedState
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.product.ProductDetail
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.ProductRepository
import com.lifepharmacy.application.model.NotifyRequestModel
import com.lifepharmacy.application.model.home.Price
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet
import com.lifepharmacy.application.utils.DateTimeUtil
import com.lifepharmacy.application.utils.PricesUtil
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import kotlin.math.roundToInt

/**
 * Created by Zahid Ali
 */
class ProductViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val productRepository: ProductRepository,
  application: Application
) : BaseViewModel(application) {

  var skip = 0
  var take = 10

  var mainProductMut = MutableLiveData<ProductDetail>()

  var totalAmount = ObservableField<Double>()

  var previewProductMut = MutableLiveData<ProductDetails>()
  var isInWishList = MutableLiveData<Boolean>()
  var isPreviewProductCart = MutableLiveData<Boolean>()
  var previewPrice = MutableLiveData<Price>()
  var previewQTY = MutableLiveData<String>()
  var countDown = MutableLiveData<String>()
  var selectedDetails = MutableLiveData<ProductDetailSelectedState>()

  var addedProducts = ArrayList<ProductDetails>()

  var unitPrice: Double = 0.0
  var qty = ObservableField<Double>()

  var productId: String = ""
  var position: Int = 0

  init {
    timerCountDown()
  }

  fun getProductDetails(productId: String) =
    performNwOperation { productRepository.getProductDetails(productId) }

  fun notifyProduct(productDetails: ProductDetails) =
    performNwOperation {
      productRepository.notifyProduct(
        makeNotifyProductRequestModel(
          productDetails
        )
      )
    }

  private fun makeNotifyProductRequestModel(productDetails: ProductDetails): NotifyRequestModel {
    return NotifyRequestModel(
      productId = productDetails.id,
      userId = appManager.persistenceManager.getLoggedInUser()?.id.toString()
    )
  }

  fun getReviews() =
    performNwOperation {
      productRepository.getProductReviews(
        productId,
        skip.toString(),
        take.toString()
      )
    }

  fun getProductSliderList(productDetail: ProductDetails): List<CarouselItem> {
    val list = arrayListOf<CarouselItem>()
    list.add(
      CarouselItem(
        imageUrl = productDetail.images.featuredImage

      )
    )
    for (item in productDetail.images.galleryImages) {
      list.add(
        CarouselItem(
          imageUrl = item.medium

        )
      )
    }
    return list
  }

  fun initAmount(productDetails: ArrayList<ProductDetails>) {
    qty.set(1.0)
    addedProducts = productDetails
    calculateTotalAmount()
  }

  fun changAddOns(productDetails: ProductDetails) {
    if (addedProducts.contains(productDetails)) {
      addedProducts.remove(productDetails)
    } else {
      addedProducts.add(productDetails)
    }
    calculateTotalAmount()
  }

  fun minusQTY() {
    if (qty.get() == null) {
      qty.set(1.0)
    }
    if (qty.get()!! > 1) {
      var temp = qty.get()!! - 1
      qty.set(temp)
    }
//        calculateTotalAmount()
  }

  fun plusQTY() {
    if (qty.get() == null) {
      qty.set(1.0)
    }
    var temp = qty.get()!! + 1
    qty.set(temp)
//        calculateTotalAmount()
  }

  private fun calculateSinglePrice(): Double {
    var returnDoube = 0.0
    returnDoube = unitPrice * qty.get()!!
    return returnDoube
  }

  private fun calculateAddOnsProducts(): Double {
    var returnType = 0.0
    for (item in addedProducts) {
      returnType += PricesUtil.getUnitPrice(item.prices, viewModelContext)
    }
    return returnType
  }

  private fun calculateTotalAmount() {
    totalAmount.set(calculateAddOnsProducts())
  }

  private fun timerCountDown() {
    val duration: Long = DateTimeUtil.getTimeDiffInMils(
      appManager.storageManagers.config.sameDayEndTime?.roundToInt() ?: 14
    ) //6 hours
    object : CountDownTimer(duration, 1000) {
      override fun onTick(millisUntilFinished: Long) {
        var millisUntilFinished = millisUntilFinished
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val elapsedHours = millisUntilFinished / hoursInMilli
        millisUntilFinished = millisUntilFinished % hoursInMilli
        val elapsedMinutes = millisUntilFinished / minutesInMilli
        millisUntilFinished = millisUntilFinished % minutesInMilli
        val elapsedSeconds = millisUntilFinished / secondsInMilli
        if (elapsedHours > 0) {
          val yy = String.format("%02d hours & %02d minutes", elapsedHours, elapsedMinutes)
          countDown.value = yy
        } else {
          val yy = String.format("%02d minutes", elapsedMinutes)
          countDown.value = yy
        }

//        :%02d secs
      }

      override fun onFinish() {

      }
    }.start()
  }

}