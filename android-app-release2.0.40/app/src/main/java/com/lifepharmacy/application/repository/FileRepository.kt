package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.network.endpoints.FileApi
import com.lifepharmacy.application.utils.NetworkUtils
import okhttp3.MultipartBody
import javax.inject.Inject


class FileRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val fileApi: FileApi) :
  BaseRepository() {


  suspend fun uploadImage(image: MultipartBody.Part) =
    getResult({ fileApi.uploadFile(image) }, networkUtils)


//  suspend fun uploadImage():Response{
//        CoroutineScope(Dispatchers.IO).launch {
//          val client = OkHttpClient().newBuilder()
//            .build()
//          val mediaType: MediaType? = "text/plain".toMediaTypeOrNull()
//          val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
//            .addFormDataPart(
//              "file",
//              "/C:/Users/zahidaz/Desktop/future-technology-background-abstract-digitally-generated-imag-R0H9GD.jpg",
//              create(
//                "application/octet-stream".toMediaTypeOrNull(),
//                File("/C:/Users/zahidaz/Desktop/future-technology-background-abstract-digitally-generated-imag-R0H9GD.jpg")
//              )
//            )
//            .build()
//          val request: Request = Request.Builder()
//            .url("https://lifeadmin.lifepharmacy.com/api/upload-file")
//            .method("POST", body)
//            .build()
//          client.newCall(request).execute()
//        }
//            delay(1000)

//  }

//    suspend fun getTransactions() =
//        getResultMock {
//            var cardModel = TransactionModel()
//            var arrayList = ArrayList<TransactionModel>()
//            arrayList.add(cardModel)
//            arrayList.add(cardModel)
//            arrayList.add(cardModel)
//            arrayList.add(cardModel)
//            arrayList.add(cardModel)
//            GeneralResponseModel(arrayList, "Please Check Your Phone for OTP", true)
//        }
}