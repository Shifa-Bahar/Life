package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.googleplaces.GooglePlacesResponse
import com.lifepharmacy.application.model.profile.UpdateUserRequestModel
import com.lifepharmacy.application.model.request.NumberOTPRequest
import com.lifepharmacy.application.model.request.NumberOTPVerifyRequest
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import com.lifepharmacy.application.utils.URLs
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GoogleMapApi {

  @GET(URLs.GOOGLE_NEAR_BY_PLACES)
  suspend fun getNearByPlaces(
    @Query("location") location: String,
    @Query("radius") radius: String,
    @Query("key") key: String,
  ): Response<GooglePlacesResponse>

}
