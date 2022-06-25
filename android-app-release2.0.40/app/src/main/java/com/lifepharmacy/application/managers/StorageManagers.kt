package com.lifepharmacy.application.managers

import android.app.DownloadManager
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.lifepharmacy.application.Constants
import com.lifepharmacy.application.model.cart.AlertMessage
import com.lifepharmacy.application.model.cart.AlertMessages
import com.lifepharmacy.application.model.cart.DeliveryInstructions
import com.lifepharmacy.application.model.category.RootCategory
import com.lifepharmacy.application.model.category.RootCategoryMainModel
import com.lifepharmacy.application.model.config.Config
import com.lifepharmacy.application.model.config.DeliverySlot
import com.lifepharmacy.application.model.config.SlotsMainModel
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.home.HomeMainModel
import com.lifepharmacy.application.model.home.HomeResponseItem
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.vouchers.VoucherMainResponse
import com.lifepharmacy.application.network.endpoints.S3Api
import com.lifepharmacy.application.network.endpoints.SplashApi
import com.lifepharmacy.application.network.endpoints.VoucherApi
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog.Companion.TAG
import com.lifepharmacy.application.utils.DateTimeUtil
import com.lifepharmacy.application.utils.NetworkUtils
import ir.siaray.downloadmanagerplus.classes.Downloader
import ir.siaray.downloadmanagerplus.classes.Downloader.getInstance
import ir.siaray.downloadmanagerplus.enums.DownloadReason
import ir.siaray.downloadmanagerplus.enums.Storage
import ir.siaray.downloadmanagerplus.interfaces.DownloadListener
import kotlinx.coroutines.*
//import org.json.JSONObject
//import org.json.simple.parser.JSONParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class StorageManagers
@Inject constructor(
  var persistenceManager: PersistenceManager,
  var networkUtils: NetworkUtils,
  var splashApi: SplashApi,
  var voucherApi: VoucherApi,
  var s3Api: S3Api,
) {
  var job: CompletableJob? = null
  var isinstant: Boolean = false
  var config = Config()
  var firstActiveSlot = DeliverySlot()
  var voucherMainResponseMut = MutableLiveData<VoucherMainResponse>()
  var homeMainResponseMut = MutableLiveData<HomeMainModel>()
  var rootCategoryMainModelMut = MutableLiveData<RootCategoryMainModel>()
  var lastURl = ""
  var token = ""
  var isAlreadyDownloading = false

  var selectedOutOfstockProductItem = ProductDetails()

  var queryId: String? = null
  var instantDeliverySlots = ArrayList<DeliverySlot>()
  var standardDeliverySlots = ArrayList<DeliverySlot>()
  var deliveryins = ArrayList<DeliveryInstructions>()
  var alertmessage = AlertMessages()
  var deliveryinsselected = ArrayList<DeliveryInstructions>()

  var chatInitiatedMut = MutableLiveData<Boolean>()

  companion object {
    var userLatLng: LatLng = LatLng(0.0, 0.0)
  }

  fun getDownloadedFile(): String? {
    token = persistenceManager.getCurrentFileToken().toString()
    val downloader: Downloader = getInstance(persistenceManager.application)
    return downloader.getDownloadedFilePath(token)
  }

  fun getVouchers(skip: String, take: String): MutableLiveData<VoucherMainResponse> {
    voucherMainResponseMut.value = persistenceManager.getVouchers()

    getVouchersFromNetwork(skip, take)
    return voucherMainResponseMut
  }


  fun getHomeData(): MutableLiveData<HomeMainModel> {
    homeMainResponseMut.value = persistenceManager.getHomeData()
    getHomeDataFromNetwork()
    return homeMainResponseMut
  }

  fun getRootCategories(): MutableLiveData<RootCategoryMainModel> {
    rootCategoryMainModelMut.value = persistenceManager.getCategoryRoot()
    getRootCategoriesFromNetwork()
    return rootCategoryMainModelMut
  }

//  fun getDownloadJsonFileString():String?{
//    if (getDownloadedFile()!=null){
//      var file = File(getDownloadedFile()!!)
//      val parser = JSONParser()
//      try {
//        val obj: Any = parser.parse(FileReader(getDownloadedFile()))
//        val jsonObject: JSONObject = obj as JSONObject
//        return jsonObject.toString()
//      } catch (e: java.lang.Exception) {
//        e.printStackTrace()
//      }
//    }
//    return null
//
//  }


  fun downloadLottieFile(url: String) {
    isAlreadyDownloading = true
    var newToken = DateTimeUtil.getCurrentDateAndTime().timeInMillis.toString()
    val downloader: Downloader = getInstance(persistenceManager.application)
      .setUrl(url)
      .setListener(object : DownloadListener {
        override fun onComplete(totalBytes: Int) {
          config.splashFileLinke?.let {
            persistenceManager.saveLottieURL(it)
          }
          persistenceManager.saveCurrentFileToken(newToken)
          isAlreadyDownloading = false
        }

        override fun onPause(
          percent: Int,
          reason: DownloadReason?,
          totalBytes: Int,
          downloadedBytes: Int
        ) {
        }

        override fun onPending(percent: Int, totalBytes: Int, downloadedBytes: Int) {
        }

        override fun onFail(
          percent: Int,
          reason: DownloadReason?,
          totalBytes: Int,
          downloadedBytes: Int
        ) {
        }

        override fun onCancel(totalBytes: Int, downloadedBytes: Int) {
          isAlreadyDownloading = false
        }

        override fun onRunning(
          percent: Int,
          totalBytes: Int,
          downloadedBytes: Int,
          downloadSpeed: Float
        ) {

        }

      })
      .setToken(newToken)
      .setAllowedOverRoaming(false)
      .setAllowedOverMetered(true) //Api 16 and higher
      .setVisibleInDownloadsUi(false)
      .setKeptAllDownload(true)
      .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
      .setDestinationDir(Storage.DIRECTORY_DOWNLOADS, "animation.json")
    downloader.start()
  }


  fun saveLatLng(latLng: LatLng) {
    latLng.let {
      persistenceManager.saveLatLong(
        latLng
      )
      userLatLng = latLng
    }
  }

  fun getLatLong(): LatLng {
    return userLatLng
  }


  fun getSettings() {
    lastURl = persistenceManager.getLottieUrl().toString()
    job = Job()
    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val getSession = splashApi.getSettings()
          getSession.enqueue(object :
            Callback<GeneralResponseModel<Config>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<Config>>,
              response: Response<GeneralResponseModel<Config>>
            ) {
              if (response.isSuccessful && response.code() < 400) {
                config = response.body()?.data ?: Config()
//                config.activeSlots?.let {
//                  deliverySlots = it
//                }
                //adding the check value for create transaction api selecton
                persistenceManager.defaultpg = config.DEFAULT_PG
                persistenceManager.ckopk = config.CKO_PK.toString()
                Log.e(TAG, "onResponse:CKO_PK ${config.CKO_PK}")
                Log.e(TAG, "onResponse: ${persistenceManager.defaultpg}")
                persistenceManager.saveConfig(config)
                if (config.splashFileLinke != lastURl && !isAlreadyDownloading) {
                  if (ContextCompat.checkSelfPermission(
                      persistenceManager.application,
                      android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                  ) {
                    config.splashFileLinke?.let { downloadLottieFile(it) }
                  }
                }
              } else {
                // Handle error returned from server
//                getSettings()
                getSettingFromS3()
              }

            }

            override fun onFailure(
              call: Call<GeneralResponseModel<Config>>,
              t: Throwable
            ) {
              t.printStackTrace()
              getSettingFromS3()
//              getSettings()
            }
          })
        } catch (e: Exception) {
          e.printStackTrace()
          getSettingFromS3()
//          getSettings()
        }
        withContext(Dispatchers.Main) {
          theJob.complete()
        }
      }

    }
  }

//  fun getAvailableSlots() {
//    job = Job()
//    job?.let { theJob ->
//      CoroutineScope(Dispatchers.IO + theJob).launch {
//        try {
//          val getSession = splashApi.getAvailableSlots()
//          getSession.enqueue(object :
//            Callback<GeneralResponseModel<ArrayList<DeliverySlot>>> {
//            override fun onResponse(
//              call: Call<GeneralResponseModel<ArrayList<DeliverySlot>>>,
//              response: Response<GeneralResponseModel<ArrayList<DeliverySlot>>>
//            ) {
//              if (response.isSuccessful && response.code() < 400) {
//                response.body()?.data?.let {
//                  instantDeliverySlots = it
//                  instantDeliverySlots.firstOrNull { it.isActive == true }
//                    ?.let { firstActiveSlotInternal ->
//                      firstActiveSlot = firstActiveSlotInternal
//                    }
//                }
//
//              }
//
//            }
//
//            override fun onFailure(
//              call: Call<GeneralResponseModel<ArrayList<DeliverySlot>>>,
//              t: Throwable
//            ) {
//              t.printStackTrace()
//              getSettingFromS3()
////              getSettings()
//            }
//          })
//        } catch (e: Exception) {
//          e.printStackTrace()
//          getSettingFromS3()
////          getSettings()
//        }
//        withContext(Dispatchers.Main) {
//          theJob.complete()
//        }
//      }
//
//    }
//  }

  fun getNewAvailableSlots() {
    job = Job()
    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val getSession = splashApi.getNewAvailableSlots()
          getSession.enqueue(object :
            Callback<GeneralResponseModel<SlotsMainModel>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<SlotsMainModel>>,
              response: Response<GeneralResponseModel<SlotsMainModel>>
            ) {
              if (response.isSuccessful && response.code() < 400) {
                response.body()?.data?.instantSlots?.let {
                  instantDeliverySlots = it
                  instantDeliverySlots.firstOrNull { it.isActive == true }
                    ?.let { firstActiveSlotInternal ->
                      firstActiveSlot = firstActiveSlotInternal
                    }
                }
                response.body()?.data?.standardSlots?.let {
                  standardDeliverySlots = it
                  standardDeliverySlots.firstOrNull { it.isActive == true }
                    ?.let { firstActiveSlotInternal ->
                      firstActiveSlot = firstActiveSlotInternal
                    }
                }
                if (instantDeliverySlots.firstOrNull()?.isActive == true && standardDeliverySlots.firstOrNull()?.isActive == true) {
                  response.body()?.data?.instantSlots?.let {
                    it.firstOrNull { it.isActive == true }
                      ?.let { firstActiveSlotInternal ->
                        firstActiveSlot = firstActiveSlotInternal
                      }
                  }
                }

              }

            }

            override fun onFailure(
              call: Call<GeneralResponseModel<SlotsMainModel>>,
              t: Throwable
            ) {
              t.printStackTrace()
              getSettingFromS3()
//              getSettings()
            }
          })
        } catch (e: Exception) {
          e.printStackTrace()
          getSettingFromS3()
//          getSettings()
        }
        withContext(Dispatchers.Main) {
          theJob.complete()
        }
      }

    }
  }

  private fun getSettingFromS3() {
    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val currentTimestamp = System.currentTimeMillis()
          val getSession = s3Api.getSettings(currentTimestamp.toString())
          getSession.enqueue(object :
            Callback<GeneralResponseModel<Config>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<Config>>,
              response: Response<GeneralResponseModel<Config>>
            ) {
              if (response.isSuccessful && response.code() < 400) {
                config = response.body()?.data ?: Config()
                persistenceManager.saveConfig(config)
//                config.activeSlots?.let {
//                  deliverySlots = it
//                }
                if (config.splashFileLinke != lastURl && !isAlreadyDownloading) {
                  if (ContextCompat.checkSelfPermission(
                      persistenceManager.application,
                      android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                  ) {
                    config.splashFileLinke?.let { downloadLottieFile(it) }
                  }
                }
              } else {
                // Handle error returned from server
//                getSettings()
              }

            }

            override fun onFailure(
              call: Call<GeneralResponseModel<Config>>,
              t: Throwable
            ) {
              t.printStackTrace()
//              getSettings()
            }
          })
        } catch (e: Exception) {
          e.printStackTrace()
//          getSettings()
        }
        withContext(Dispatchers.Main) {
          theJob.complete()
        }
      }

    }
  }


  private fun getVouchersFromNetwork(skip: String, take: String) {
    job = Job()
    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val getSession = voucherApi.getVouchersListForCache(skip, take)
          getSession.enqueue(object :
            Callback<GeneralResponseModel<VoucherMainResponse>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<VoucherMainResponse>>,
              response: Response<GeneralResponseModel<VoucherMainResponse>>
            ) {
              if (response.isSuccessful && response.code() < 400) {
                voucherMainResponseMut.value = response.body()?.data ?: VoucherMainResponse()
                if (voucherMainResponseMut.value != null) {
                  persistenceManager.saveVouchers(voucherMainResponseMut.value!!)
                }

              } else {
                // Handle error returned from server
//                getSettings()
              }

            }

            override fun onFailure(
              call: Call<GeneralResponseModel<VoucherMainResponse>>,
              t: Throwable
            ) {
              t.printStackTrace()
//              getSettings()
            }
          })
        } catch (e: Exception) {
          e.printStackTrace()
//          getSettings()
        }
        withContext(Dispatchers.Main) {
          theJob.complete()
        }
      }

    }

  }

  private fun getHomeDataFromNetwork() {

    job = Job()

    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val getSession = splashApi.getHomeData(Constants.HOME_PAGE_ID)
          getSession.enqueue(object :
            Callback<GeneralResponseModel<ArrayList<HomeResponseItem>>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<ArrayList<HomeResponseItem>>>,
              response: Response<GeneralResponseModel<ArrayList<HomeResponseItem>>>
            ) {
              if (response.isSuccessful && response.code() < 400) {
                homeMainResponseMut.value = HomeMainModel(homeItems = response.body()?.data)
                homeMainResponseMut.value?.let {
                  persistenceManager.saveHomeData(it)
                }

              } else {
                // Handle error returned from server
//                getSettings()
              }

            }

            override fun onFailure(
              call: Call<GeneralResponseModel<ArrayList<HomeResponseItem>>>,
              t: Throwable
            ) {
              t.printStackTrace()
//              getSettings()
            }
          })
        } catch (e: Exception) {
          e.printStackTrace()
//          getSettings()
        }
        withContext(Dispatchers.Main) {
          theJob.complete()
        }
      }

    }

  }

  private fun getRootCategoriesFromNetwork() {
    job = Job()
    job?.let { theJob ->
      CoroutineScope(Dispatchers.IO + theJob).launch {
        try {
          val getSession = splashApi.requestRootCategories()
          getSession.enqueue(object :
            Callback<GeneralResponseModel<ArrayList<RootCategory>>> {
            override fun onResponse(
              call: Call<GeneralResponseModel<ArrayList<RootCategory>>>,
              response: Response<GeneralResponseModel<ArrayList<RootCategory>>>
            ) {
              if (response.isSuccessful && response.code() < 400) {
                rootCategoryMainModelMut.value =
                  RootCategoryMainModel(rootCategory = response.body()?.data)
                rootCategoryMainModelMut.value?.let {
                  persistenceManager.saveCategoryRoot(it)
                }
              }
            }

            override fun onFailure(
              call: Call<GeneralResponseModel<ArrayList<RootCategory>>>,
              t: Throwable
            ) {
              t.printStackTrace()

            }
          })
        } catch (e: Exception) {
          e.printStackTrace()
        }
        withContext(Dispatchers.Main) {
          theJob.complete()
        }
      }

    }

  }

}