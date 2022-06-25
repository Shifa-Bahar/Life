package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.filters.FilterMainRequest
import com.lifepharmacy.application.network.endpoints.ProductsApi
import com.lifepharmacy.application.utils.NetworkUtils
import javax.inject.Inject

class ProductListRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val productList: ProductsApi) :
  BaseRepository() {


  suspend fun getProducts(skip: String, take: String, filter: FilterMainRequest) =
    getResult({ productList.requestProducts(skip, take, filter) }, networkUtils)

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