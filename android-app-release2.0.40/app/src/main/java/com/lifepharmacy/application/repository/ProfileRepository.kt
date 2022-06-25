package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.model.address.AddressTypeModel
import com.lifepharmacy.application.model.address.AddressDeleteRequest
import com.lifepharmacy.application.model.address.AddressMainModel
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.docs.CreateDocRequestBody
import com.lifepharmacy.application.model.docs.DeleteDocRequestBody
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.googleplaces.NearByPlacesRequestModel
import com.lifepharmacy.application.network.endpoints.GoogleMapApi
import com.lifepharmacy.application.network.endpoints.ProfileApi
import com.lifepharmacy.application.utils.HandleNetworkCallBack
import com.lifepharmacy.application.utils.NetworkUtils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query
import javax.inject.Inject

class ProfileRepository
@Inject constructor(
  private val networkUtils: NetworkUtils,
  private val profileApi: ProfileApi,
  private val googleMapApi: GoogleMapApi
) :
  BaseRepository() {
  suspend fun saveAddress(body: AddressModel) =
    getResult({ profileApi.saveAddress(body) }, networkUtils)

  suspend fun getNotifications(
    @Query("skip") skip: String,
    @Query("take") take: String
  ) = getResult({ profileApi.getNotifications(skip, take) }, networkUtils)


  suspend fun getNearByPlaces(
    nearByPlacesRequestModel: NearByPlacesRequestModel
  ) = getResultForGoogleMapsApi({
    googleMapApi.getNearByPlaces(
      location = nearByPlacesRequestModel.location ?: "",
      radius = nearByPlacesRequestModel.radius ?: "",
      key = nearByPlacesRequestModel.key ?: ""
    )
  }, networkUtils)

  suspend fun getOutlet() = getResult({ profileApi.outletDetails() }, networkUtils)

  suspend fun getDocs() = getResult({ profileApi.getDocuments() }, networkUtils)

  suspend fun createDocs(body: CreateDocRequestBody) =
    getResult({ profileApi.createDoc(body) }, networkUtils)

  suspend fun deleteDoc(body: DeleteDocRequestBody) =
    getResult({ profileApi.deleteDoc(body) }, networkUtils)

  fun getAddress(
    handler: HandleNetworkCallBack<GeneralResponseModel<AddressMainModel>>
  ) {

    if (!networkUtils.isConnectedToInternet) {
      handler.handleWebserviceCallBackFailure(networkUtils.networkErrorMessage)
      return
    }

    job = Job()

    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val getSession = profileApi.getUserAddress()
          getSession.enqueue(object :
            Callback<GeneralResponseModel<AddressMainModel>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<AddressMainModel>>,
              response: Response<GeneralResponseModel<AddressMainModel>>
            ) {
              if (response.isSuccessful && response.code() < 400) {

                handler.handleWebserviceCallBackSuccess(response)


              } else {
                // Handle error returned from server
                handler.handleWebserviceCallBackFailure(
                  response.errorBody().toString()
                )
              }

            }

            override fun onFailure(
              call: Call<GeneralResponseModel<AddressMainModel>>,
              t: Throwable
            ) {
              t.printStackTrace()
              handler.handleWebserviceCallBackFailure("Internal Server Error")
            }
          })
        } catch (e: Exception) {
          e.printStackTrace()
          handler.handleWebserviceCallBackFailure("Internal Server Error")
        }
        withContext(Dispatchers.Main) {
          theJob.complete()
        }
      }

    }

  }
//
//    suspend fun getAddress() =
//        getResult ({profileApi.getUserAddress()},networkUtils)

  suspend fun deleteAddress(body: AddressDeleteRequest) =
    getResult({ profileApi.deleteAddress(body) }, networkUtils)

  //    suspend fun getCards() =
//        getResultMock {
//            var cardModel = CardModel()
//            var arrayList = ArrayList<CardModel>()
//            arrayList.add(cardModel)
//            arrayList.add(cardModel)
//            arrayList.add(cardModel)
//            GeneralResponseModel(arrayList, "Please Check Your Phone for OTP", true)
//        }
//    suspend fun getAddress() =
//        getResultMock {
//            var addressModel = AddressModel(type = "Work")
//            var addressModel2 = AddressModel(type = "Home")
//            var addressModel3 = AddressModel(type = "Others")
//            var arrayList = ArrayList<AddressModel>()
//            arrayList.add(addressModel)
//            arrayList.add(addressModel2)
//            arrayList.add(addressModel3)
//            GeneralResponseModel(arrayList, "Please Check Your Phone for OTP", true)
//        }
  fun getSpinnerItems(): ArrayList<AddressTypeModel> {
    val addressTypeModelHome =
      AddressTypeModel(1, "Home")
    val addressTypeModelWork =
      AddressTypeModel(2, "Work")
    val addressTypeModelOthers =
      AddressTypeModel(
        3,
        "Others"
      )
    val arrayListTypes = ArrayList<AddressTypeModel>()
    arrayListTypes.add(addressTypeModelHome)
    arrayListTypes.add(addressTypeModelWork)
    arrayListTypes.add(addressTypeModelOthers)
    return arrayListTypes
  }

  fun getTimeItems(): ArrayList<AddressTypeModel> {
    val addressTypeModelHome =
      AddressTypeModel(
        0,
        "Morning"
      )
    val addressTypeModelWork =
      AddressTypeModel(
        1,
        "Afternoon"
      )
    val addressTypeModelOthers =
      AddressTypeModel(
        2,
        "Evening"
      )
    val arrayListTypes = ArrayList<AddressTypeModel>()
    arrayListTypes.add(addressTypeModelHome)
    arrayListTypes.add(addressTypeModelWork)
    arrayListTypes.add(addressTypeModelOthers)
    return arrayListTypes
  }
}