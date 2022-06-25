package com.lifepharmacy.application.repository

import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.*
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.orders.RateProductRequestBody
import com.lifepharmacy.application.model.orders.RateShipmentRequestBody
import com.lifepharmacy.application.model.orders.ReturnOrderRequestBody
import com.lifepharmacy.application.model.review.RatingSubOrderRequest
import com.lifepharmacy.application.network.endpoints.OrdersApi
import com.lifepharmacy.application.utils.NetworkUtils
import javax.inject.Inject

class OrderRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val ordersApi: OrdersApi) :
  BaseRepository() {


  suspend fun getOrders(skip: String, take: String) =
    getResult({ ordersApi.getOrdersList(skip, take) }, networkUtils)

  suspend fun getPrescriptionOrders(skip: String, take: String) =
    getResult({ ordersApi.getPrescriptionOrderList(skip, take) }, networkUtils)

  suspend fun getOrderDetails(id: String) =
    getResult({ ordersApi.getOrderDetail(id) }, networkUtils)

  suspend fun getSubOrderDetail(id: String) =
    getResult({ ordersApi.getSubOrderDetail(id) }, networkUtils)

  suspend fun getReturnOrder(body: ReturnOrderRequestBody) =
    getResultWithoutData({ ordersApi.returnOrder(body) }, networkUtils)

  suspend fun rateShipment(orderID: String, body: RateShipmentRequestBody) =
    getResult({ ordersApi.rateShipment(orderID, body) }, networkUtils)

  suspend fun rateSubOrder(orderID: String, body: RatingSubOrderRequest) =
    getResult({ ordersApi.rateSubOrder(orderID, body) }, networkUtils)

  suspend fun rateProduct(orderID: String, body: RateProductRequestBody) =
    getResult({ ordersApi.rateProduct(orderID, body) }, networkUtils)

  suspend fun getReturnOrderList(skip: String, take: String, status: String) =
    getResult({ ordersApi.getReturnOrderList(skip, take) }, networkUtils)

  suspend fun getBuyAgainProducts() =
    getResult({ ordersApi.getProductsBuyAgain() }, networkUtils)

  suspend fun downlaodInvoice(file: String) =
    getResponseBodyResult({ ordersApi.downloadInvoice(file) }, networkUtils)

  //    suspend fun getOrders() =
//        getResultMock {
//            var productModel = ProductModel()
//            var productModelArray = ArrayList<ProductModel>()
//            productModelArray.add(productModel)
//            productModelArray.add(productModel)
//            productModelArray.add(productModel)
//            var orderModel = OrderModel()
//            orderModel.listOfProducts = productModelArray
//            var arrayList = ArrayList<OrderModel>()
//            arrayList.add(orderModel)
//            arrayList.add(orderModel)
//            arrayList.add(orderModel)
//            arrayList.add(orderModel)
//            arrayList.add(orderModel)
//            arrayList.add(orderModel)
//            GeneralResponseModel(arrayList, "No result found", true)
//        }
  suspend fun getShipment() =
    getResultMock {
      var productModel = ProductModel()
      var productModelArray = ArrayList<ProductModel>()
      productModelArray.add(productModel)
      productModelArray.add(productModel)
      productModelArray.add(productModel)
      var orderModel = ShipementModel()
      orderModel.listOfProducts = productModelArray
      var arrayList = ArrayList<ShipementModel>()
      arrayList.add(orderModel)
      arrayList.add(orderModel)
      GeneralResponseModel(arrayList, "No result found", true)
    }

  suspend fun getReturnReasons() =
    getResultMock {
      val productModelArray = ArrayList<String>()
      productModelArray.add("Bad Quality")
      productModelArray.add("Damage")
      productModelArray.add("Wrong Product")
      productModelArray.add("Expired")
      productModelArray.add("Others")
      GeneralResponseModel(productModelArray, "No result found", true)
    }

  suspend fun getRatingTags() =
    getResultMock {
      var productModelArray = ArrayList<String>()
      productModelArray.add(networkUtils.context.getString(R.string.bad))
//      productModelArray.add(networkUtils.context.getString(R.string.rude_driver))
      productModelArray.add(networkUtils.context.getString(R.string.average))
      productModelArray.add(networkUtils.context.getString(R.string.good))
      productModelArray.add(networkUtils.context.getString(R.string.excellent))
      GeneralResponseModel(productModelArray, "No result found", true)
    }
}