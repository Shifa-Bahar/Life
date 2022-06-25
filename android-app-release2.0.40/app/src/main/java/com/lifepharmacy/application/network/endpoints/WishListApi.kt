package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.wishlist.AddWishListRequestBody
import com.lifepharmacy.application.model.wishlist.DeleteWishListBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import com.lifepharmacy.application.utils.URLs
interface WishListApi {

  @GET(URLs.GET_WISH_LIST)
  fun getWishList(): Call<GeneralResponseModel<ArrayList<ProductDetails>>>

  @POST(URLs.ADD_TO_WISH_LIST)
  fun addToWishList(@Body body: AddWishListRequestBody): Call<GeneralResponseModel<ArrayList<ProductDetails>>>

  @POST(URLs.DELETE_FROM_WISH_LIST)
  fun deleteFromWishList(@Body body: DeleteWishListBody): Call<GeneralResponseModel<ArrayList<ProductDetails>>>


  @POST("https://mp.lifepharmacy.com/api/event")
  fun eventcallList(@Body params: Any): Call<GeneralResponseModel<ProductDetails>>

}
