package com.lifepharmacy.application.ui.rating

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.RatingFragmentState
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.orders.*
import com.lifepharmacy.application.model.orders.suborder.SubOrderItemWithJustItemOfRating
import com.lifepharmacy.application.model.review.*
import com.lifepharmacy.application.model.review.Driver
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.OrderRepository
import com.lifepharmacy.application.repository.RatingRepository
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeInt
import com.lifepharmacy.application.utils.universal.InputEditTextValidator
import java.lang.Exception

/**
 * Created by Zahid Ali
 */
class RatingsViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: OrderRepository,
  private val ratingRepository: RatingRepository,
  application: Application
) : BaseViewModel(application) {

  var ratingState = MutableLiveData<RatingFragmentState>()
  var selectedSubOrderItem = MutableLiveData<SubOrderItemWithJustItemOfRating>()
  var selectedSubOrderProductItem = MutableLiveData<SubOrderProductItem>()
  var mainRatingTag = MutableLiveData<String>()
  var showProducts = MutableLiveData<Boolean>()
  var isDriverRated = MutableLiveData<Boolean>()
  var isReviewDriver = MutableLiveData<Boolean>()

  var selectedTagPositions = ArrayList<Int>()

  var subOrderId: String? = ""
  var subordernumber: String? = ""
  var ratingMain: Float? = null
  var shipmentId: String? = ""
  var selectedRatingValue = MutableLiveData<RatingTages>()


  fun getSubOrderDetail(suborderIds: String) =
    performNwOperation { repository.getSubOrderDetail(suborderIds ?: "") }


  var review: InputEditTextValidator =
    InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      null
    )
  var suOrderReview: InputEditTextValidator =
    InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      null
    )
  var driverReview: InputEditTextValidator =
    InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      null
    )

  var subOrderID: Int? = null
  var orderID: Int? = null

  var rating: Float? = null
  var driverRating: Float? = null

  var selectedTags = ArrayList<String>()

  fun rateDriverShipment() =
    performNwOperation {
      ratingRepository.rateShipment(
        shipmentId.toString() ?: "0",
        makeShipmentRatingRequestBody()
      )
    }


  fun rateSubOrderShipment() =
    performNwOperation {
      ratingRepository.rateSubOrder(
        subOrderId.toString() ?: "0",
        makeSubOrderRatingRequestBody()
      )
    }

  fun getRatingsTags() =
    performNwOperation { repository.getRatingTags() }

  fun setRatingTagSelectedItems(item: String) {
    if (selectedTags.contains(item)) {
      selectedTags.remove(item)
    } else {
      selectedTags.add(item)
    }
  }

  private fun makeShipmentRatingRequestBody(): RateShipmentRequestBody {
    return RateShipmentRequestBody(
      rating = driverRating,
      review = driverReview.getValue(),
      subOrderId = subOrderId?.stringToNullSafeInt(),
//      response = "new"
    )
  }

  private fun makeSubOrderRatingRequestBody(): RatingSubOrderRequest {
    var selectedTage: List<String>? = ArrayList()
    try {
      selectedTage = selectedRatingValue.value?.tags?.filterIndexed { index, s ->
        selectedTagPositions.contains(index)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    return RatingSubOrderRequest(
      rating = ratingMain ?: 0F,
      review = suOrderReview.getValue(),
      subOrderId = subOrderId?.stringToNullSafeInt(),
      tags = selectedTage
    )
  }

  fun setCorrespondingTag(rating: Float) {
    selectedTagPositions.clear()
    val ratingInInt = rating.toInt()
    ratingMain = rating
    appManager.storageManagers.config.reviewTags?.firstOrNull { tag -> tag.value == ratingInInt }
      ?.let {
        selectedRatingValue.value = it
      }
  }


  fun rateProduct(productID: String, rating: Float, review: String, isAnonymous: Boolean = false) =
    performNwOperation {
      ratingRepository.rateProduct(
        productID ?: "0",
        makeProductRatingBody(rating, review, isAnonymous = isAnonymous)
      )
    }

  private fun makeProductRatingBody(
    rating: Float,
    review: String,
    isAnonymous: Boolean = false
  ): RateProductRequestBody {
    return RateProductRequestBody(
      rating = rating,
      review = review,
      subOrderId = subOrderId?.stringToNullSafeInt(),
      isAnonymous = isAnonymous,
//      response = "new"
    )
  }

  fun rateOverAll(
    productArrayList: ArrayList<SubOrderProductItem>?,
    comments: ArrayList<String?>?,
    ratedValue: ArrayList<Float>?,
    isAnonymous: ArrayList<Boolean>?
  ) =
    performNwOperation {
      ratingRepository.rateOverAll(
        makeAllReviewsRequestModel(
          productArrayList = productArrayList,
          comments = comments,
          ratedValue = ratedValue,
          isAnonymous = isAnonymous
        )
      )
    }

  private fun makeAllReviewsRequestModel(
    productArrayList: ArrayList<SubOrderProductItem>?,
    comments: ArrayList<String?>?,
    ratedValue: ArrayList<Float>?,
    isAnonymous: ArrayList<Boolean>?
  ): AllRatingRequestBody {
    var selectedTage: List<String>? = ArrayList()
    var productList: List<Product>? = ArrayList()
    try {
      selectedTage = selectedRatingValue.value?.tags?.filterIndexed { index, s ->
        selectedTagPositions.contains(index)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    try {
      productList = productArrayList?.mapIndexed { index, item ->
        Product(
          productId = item.id,
          feedback = comments?.get(index),
          rating = ratedValue?.get(index),
          isAnonymous = isAnonymous?.get(index)
        )
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    return AllRatingRequestBody(
      driver = Driver(
        feedback = driverReview.getValue(),
        rating = driverRating ?: 0F,
        shipmentId = shipmentId?.stringToNullSafeInt()
      ),
      overAll = OverAll(
        feedback = suOrderReview.getValue(),
        rating = ratingMain ?: 0F,
        subOrderId = subOrderId?.stringToNullSafeInt(),
        tags = selectedTage
      ),
      products = productList,
      subOrderId = subOrderId?.stringToNullSafeInt(),
    )


  }

}