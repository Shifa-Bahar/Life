package com.lifepharmacy.application.network.endpoints

import com.lifepharmacy.application.model.FileResponse
import com.lifepharmacy.application.model.general.GeneralResponseModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.lifepharmacy.application.utils.URLs
interface FileApi {

  @Multipart
  @POST(URLs.UPLOAD_FILE)
  suspend fun uploadFile(@Part image: MultipartBody.Part): Response<GeneralResponseModel<FileResponse>>
}
