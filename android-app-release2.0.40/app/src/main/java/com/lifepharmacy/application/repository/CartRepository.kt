package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.cart.CreateCartRequest
import com.lifepharmacy.application.model.orders.PlaceOrderRequest
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.model.cart.ValidateCouponRequestBody
import com.lifepharmacy.application.model.orders.outofstock.OutOfStockRequestBody
import com.lifepharmacy.application.network.endpoints.CartApi
import com.lifepharmacy.application.utils.NetworkUtils
import javax.inject.Inject

class CartRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val cartApi: CartApi) :
  BaseRepository() {

  suspend fun createOrder(body: PlaceOrderRequest) =
    getResult({ cartApi.createOrder(body) }, networkUtils)

  suspend fun createOutOfStockOrder(body: OutOfStockRequestBody) =
    getResult({ cartApi.createOutOfStockOrder(body) }, networkUtils)

  suspend fun createTransaction(body: TransactionModel) =
    getResult({ cartApi.createTransaction(body) }, networkUtils)

  suspend fun createTransactionNew(body: TransactionModel) =
    getResult({ cartApi.createTransactionNew(body) }, networkUtils)

  suspend fun validateCoupon(body: ValidateCouponRequestBody) =
    getResult ({cartApi.validateCoupon(body)},networkUtils)

  suspend fun createCart(body: CreateCartRequest) =
    getResult ({cartApi.createCart(body)},networkUtils)


  suspend fun updateCart(body: CreateCartRequest) =
    getResult ({cartApi.updateCart(body)},networkUtils)

  suspend fun getCartDetails(id:String) =
    getResult ({cartApi.getCartDetails(id)},networkUtils)
//  suspend fun getCoupons() =
//    getResultMock {
//      var couponModel = CouponModel()
//      var arrayList = ArrayList<CouponModel>()
//      arrayList.add(couponModel)
//      arrayList.add(couponModel)
//      arrayList.add(couponModel)
//      GeneralResponseModel(arrayList, "Please Check Your Phone for OTP", true)
//    }
//    suspend fun getProducts() =
//        getResultMock {
//            var productModel = ProductModel()
//            var arrayList = ArrayList<ProductModel>()
//            arrayList.add(productModel)
//            arrayList.add(productModel)
//            arrayList.add(productModel)
//            GeneralResponseModel(arrayList, "No result found", true)
//        }

//    suspend fun getDeliveryOptions() =
//        getResultMock {
//
//
//            var item = DeliveryOptionItemModel()
//            var itemsList = ArrayList<DeliveryOptionItemModel>()
//            itemsList.add(item)
//            itemsList.add(item)
//            itemsList.add(item)
//            var option = DeliveryOptionModel()
//            option.listItem = itemsList
//            var optionsList = ArrayList<DeliveryOptionModel>()
//            optionsList.add(option)
//            optionsList.add(option)
//            optionsList.add(option)
//            GeneralResponseModel(optionsList, "Please Check Your Phone for OTP", true)
//        }
}