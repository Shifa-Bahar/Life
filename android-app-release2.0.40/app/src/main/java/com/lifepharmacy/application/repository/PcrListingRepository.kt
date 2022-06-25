package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.cart.CreateCartRequest
import com.lifepharmacy.application.model.filters.FilterMainRequest
import com.lifepharmacy.application.model.orders.PlaceOrderRequest
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.model.pcr.pcrrequestmodel.PlacePcrOrderRequest
import com.lifepharmacy.application.network.endpoints.PcrlistingApi
import com.lifepharmacy.application.network.endpoints.ProductsApi
import com.lifepharmacy.application.utils.NetworkUtils
import javax.inject.Inject

class PcrListingRepository
@Inject constructor(
  private val networkUtils: NetworkUtils,
  private val pcrproductList: PcrlistingApi
) :
  BaseRepository() {


  suspend fun getPcrProducts(id: String?) =
    getResult({ pcrproductList.requestPcrProducts(id!!) }, networkUtils)

  suspend fun getPcrDate(id: String?) =
    getResult({ pcrproductList.requestPcrDatetime(id!!) }, networkUtils)

  suspend fun getPcrAddress() =
    getResult({ pcrproductList.requestPcrAddress() }, networkUtils)

  suspend fun createPcrOrder(id: String?, body: PlacePcrOrderRequest) =
    getResult({ pcrproductList.createPcrOrder(id!!,body) }, networkUtils)

  suspend fun createPcrTransaction(body: TransactionModel) =
    getResult({ pcrproductList.createPcrTransaction(body) }, networkUtils)

  suspend fun getAppoinmentList(skip: String, take: String) =
    getResult({ pcrproductList.getAppoinmentList(skip, take) }, networkUtils)

  suspend fun getPcrAppoinmentDetail(id: Int) =
    getResult({ pcrproductList.getPcrAppoinmentDetail(id) }, networkUtils)

//    suspend fun getProducts() =
//        getResultMock {
//            var productModel = ProductModel()
//            var arrayList = ArrayList<ProductModel>()
//            arrayList.add(productModel)
//            arrayList.add(productModel)
//            arrayList.add(productModel)
//            arrayList.add(productModel)
//            arrayList.add(productModel)
//            arrayList.add(productModel)
//            GeneralResponseModel(arrayList, "No result found", true)
//        }


//    suspend fun getDiscounts() =
//        getResultMock {
//            var discountModel = DiscountModel("DSF Deals")
//            var arrayList = ArrayList<DiscountModel>()
//            arrayList.add(discountModel)
//            arrayList.add(discountModel)
//            arrayList.add(discountModel)
//            GeneralResponseModel(arrayList, "No result found", true)
//        }
//    suspend fun getFilters() =
//        getResultMock {
//            var filterModel = FilterModel("DSF Deals")
//            var filterModel2 = FilterModel("DSF Deals2")
//            var filterModel3 = FilterModel("DSF Deals3")
//            var arrayList = ArrayList<FilterModel>()
//            arrayList.add(filterModel)
//            arrayList.add(filterModel2)
//            arrayList.add(filterModel3)
//            GeneralResponseModel(arrayList, "No result found", true)
//        }
//    suspend fun getQuickOptions() =
//        getResultMock {
//            var quickOptionModel = QuickOptionModel("Express Shipping")
//            var quickOptionModel2 = QuickOptionModel("Express Shipping2")
//            var quickOptionModel3 = QuickOptionModel("Express Shipping3")
//            var arrayList = ArrayList<QuickOptionModel>()
//            arrayList.add(quickOptionModel)
//            arrayList.add(quickOptionModel2)
//            arrayList.add(quickOptionModel3)
//            GeneralResponseModel(arrayList, "No result found", true)
//        }
}