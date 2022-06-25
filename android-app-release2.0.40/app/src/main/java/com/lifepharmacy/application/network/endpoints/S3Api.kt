package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.config.Config
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.profile.UpdateUserRequestModel
import com.lifepharmacy.application.model.request.NumberOTPRequest
import com.lifepharmacy.application.model.request.NumberOTPVerifyRequest
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import com.lifepharmacy.application.utils.URLs
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface S3Api {

  @GET(URLs.CONFIG_FILE)
  fun getSettings(@Query("timeStamp") timeStamp: String): Call<GeneralResponseModel<Config>>

}
