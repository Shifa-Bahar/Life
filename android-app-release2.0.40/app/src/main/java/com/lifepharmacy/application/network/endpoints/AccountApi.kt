package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.profile.UpdateUserRequestModel
import com.lifepharmacy.application.model.request.NumberOTPRequest
import com.lifepharmacy.application.model.request.NumberOTPVerifyRequest
import com.lifepharmacy.application.model.response.VerifyOTPResponse
import com.lifepharmacy.application.utils.URLs
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {

  @POST(URLs.CHECK_TOKEN)
  suspend fun checkToken(): Response<GeneralResponseModel<String>>

  @POST(URLs.REQUEST_OTP)
  suspend fun requestOTP(@Body body: NumberOTPRequest): Response<GeneralResponseModel<String>>

  @POST(URLs.VERIFY_OTP)
  suspend fun verifyNumberOTP(@Body body: NumberOTPVerifyRequest): Response<GeneralResponseModel<VerifyOTPResponse>>

  @POST(URLs.UPDATE_USER)
  suspend fun updateUser(@Body body: UpdateUserRequestModel): Response<GeneralResponseModel<VerifyOTPResponse>>

  @POST(URLs.UPDATE_USER_AND_SEND_OTP)
  suspend fun updateUserAndSendOTP(@Body body: NumberOTPRequest): Response<GeneralResponseModel<String>>

  @POST(URLs.CHECK_TOKEN)
  fun updateUser(): Call<GeneralResponseModel<VerifyOTPResponse>>
}
