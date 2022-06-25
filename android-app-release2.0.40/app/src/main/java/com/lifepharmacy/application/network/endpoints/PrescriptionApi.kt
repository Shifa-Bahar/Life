package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.prescription.PrescriptionRequestBody
import com.lifepharmacy.application.model.prescription.PrescriptionResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.lifepharmacy.application.utils.URLs
interface PrescriptionApi {
  @POST(URLs.CREATE_PRESCRIPTION)
  suspend fun uploadPrescription(@Body body: PrescriptionRequestBody): Response<GeneralResponseModel<PrescriptionResponseModel>>
}
