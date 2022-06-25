package com.lifepharmacy.application.repository

import android.util.Log
import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.NotifyRequestModel
import com.lifepharmacy.application.network.endpoints.ProductApi
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet
import com.lifepharmacy.application.utils.NetworkUtils
import javax.inject.Inject

class ProductRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val productApi: ProductApi) :
  BaseRepository() {

  suspend fun getProductDetails(productId: String) =
    getResult({ productApi.requestProduct(productId) }, networkUtils)

  suspend fun notifyProduct(body: NotifyRequestModel) =
    getResultWithoutData({ productApi.notifyProduct(body) }, networkUtils)

  suspend fun getProductReviews(id:String,skip:String,take:String) =
    getResult ({productApi.getProductReviews(id,skip,take)},networkUtils)

//    suspend fun getCarousalImages() = getResultMock {
//        var list = arrayListOf<CarouselItem>()
//        list.add(
//            CarouselItem(
//                imageDrawable = R.mipmap.dummy_image
//            )
//        )
//        list.add(
//            CarouselItem(
//                imageDrawable = R.mipmap.dummy_image
//            )
//        )
//        list.add(
//            CarouselItem(
//                imageDrawable = R.mipmap.dummy_image
//
//            )
//        )
//        GeneralResponseModel(list.toList(), "No result found", true)
//    }
//
//    suspend fun getAddOns() =
//        getResultMock {
//            var addOns = ProductModel()
//            var arrayList = ArrayList<ProductModel>()
//            arrayList.add(addOns)
//            arrayList.add(addOns)
//            arrayList.add(addOns)
//            GeneralResponseModel(arrayList, "No result found", true)
//        }
//
//    suspend fun getReviews() =
//        getResultMock {
//            var reviewModel = ReviewModel()
//            var arrayList = ArrayList<ReviewModel>()
//            arrayList.add(reviewModel)
//            arrayList.add(reviewModel)
//            arrayList.add(reviewModel)
//            GeneralResponseModel(arrayList, "No result found", true)
//        }
//
//    suspend fun getProducts() =
//        getResultMock {
//            var productModel = ProductModel()
//            var arrayList = ArrayList<ProductModel>()
//            arrayList.add(productModel)
//            arrayList.add(productModel)
//            arrayList.add(productModel)
//            GeneralResponseModel(arrayList, "No result found", true)
//        }
//
//    suspend fun getRatings() =
//        getResultMock {
//            var ratingModel5 = RatingModel("5", 99, "99%")
//            var ratingModel4 = RatingModel("4", 60, "60%")
//            var ratingModel3 = RatingModel("3", 50, "50%")
//            var ratingModel2 = RatingModel("2", 10, "10%")
//            var ratingModel1 = RatingModel("1", 5, "5%")
//            var arrayList = ArrayList<RatingModel>()
//            arrayList.add(ratingModel5)
//            arrayList.add(ratingModel4)
//            arrayList.add(ratingModel3)
//            arrayList.add(ratingModel2)
//            arrayList.add(ratingModel1)
//            GeneralResponseModel(arrayList, "No result found", true)
//        }
//
//    suspend fun getDiscounts() =
//        getResultMock {
//            var discountModel = DiscountModel("DSF Deals")
//            var arrayList = ArrayList<DiscountModel>()
//            arrayList.add(discountModel)
//            arrayList.add(discountModel)
//            arrayList.add(discountModel)
//            GeneralResponseModel(arrayList, "No result found", true)
//        }

}