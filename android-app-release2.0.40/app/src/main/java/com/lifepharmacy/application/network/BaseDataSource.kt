package com.lifepharmacy.application.network

import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.general.GeneralResponseModelWithoutData
import com.lifepharmacy.application.model.googleplaces.GooglePlacesResponse
import com.lifepharmacy.application.model.search.custome.CustomQuery
import com.lifepharmacy.application.utils.NetworkUtils
import org.json.JSONObject
import retrofit2.Response

abstract class BaseDataSource {
  protected suspend fun <T> getResult(
    call: suspend () -> Response<GeneralResponseModel<T>>,
    networkUtils: NetworkUtils
  ): Result<GeneralResponseModel<T>> {
    if (networkUtils.isConnectedToInternet) {
      try {
        val response = call()
        if (response.isSuccessful && response.code() < 400) {
          val body = response.body()
          if (body?.success != null && body.success == true) {
            return Result.success(body)
          } else {
            body?.message?.let {
              return Result.error(it)
            }
          }

        }
        val errorBody = JSONObject(
          response.errorBody()!!
            .charStream()
            .readText()
        )
//        return error("Network Error")
        return error(errorBody.getString("message"))
      } catch (e: Exception) {
        e.printStackTrace()
        return error("Network Error")
//        return error(e.message ?: e.toString())
      }
    } else {
      return error("Network Error")
    }

  }


  protected suspend fun <T> getCustomSearchSuggestion(
    call: suspend () -> Response<T>,
    networkUtils: NetworkUtils
  ): Result<T> {
    if (networkUtils.isConnectedToInternet) {
      try {
        val response = call()
        if (response.isSuccessful && response.code() < 400) {
          val body = response.body()
          body?.let {
            return Result.success(body)
          }

        }
        val errorBody = JSONObject(
          response.errorBody()!!
            .charStream()
            .readText()
        )
//        return error("Network Error")
        return error(errorBody.getString("message"))
      } catch (e: Exception) {
        e.printStackTrace()
        return error("Network Error")
//        return error(e.message ?: e.toString())
      }
    } else {
      return error("Network Error")
    }

  }


  protected suspend fun getCustomSearchQuery(
    call: suspend () -> Response<CustomQuery>,
    networkUtils: NetworkUtils
  ): Result<CustomQuery> {
    if (networkUtils.isConnectedToInternet) {
      try {
        val response = call()
        if (response.isSuccessful && response.code() < 400) {
          val body = response.body()
          body?.let {
            return Result.success(body)
          }

        }
        val errorBody = JSONObject(
          response.errorBody()!!
            .charStream()
            .readText()
        )
//        return error("Network Error")
        return error(errorBody.getString("message"))
      } catch (e: Exception) {
        e.printStackTrace()
        return error("Network Error")
//        return error(e.message ?: e.toString())
      }
    } else {
      return error("Network Error")
    }

  }

  protected suspend fun <T> getResultWithoutGeneralResponseModel(
    call: suspend () -> Response<T>,
    networkUtils: NetworkUtils
  ): Result<T?> {
    if (networkUtils.isConnectedToInternet) {
      try {
        val response = call()
        if (response.isSuccessful && response.code() < 400) {
          val body = response.body()
          return Result.success(body)
        }
        val errorBody = JSONObject(
          response.errorBody()!!
            .charStream()
            .readText()
        )
        return error(errorBody.getString("message"))
//        return error("Network Error")
      } catch (e: Exception) {
        return error(e.message ?: e.toString())
      }
    } else {
      return error("Network Error")
    }

  }

  protected suspend fun getResultWithoutData(
    call: suspend () -> Response<GeneralResponseModelWithoutData>,
    networkUtils: NetworkUtils
  ): Result<GeneralResponseModelWithoutData> {
    if (networkUtils.isConnectedToInternet) {
      try {
        val response = call()
        if (response.isSuccessful && response.code() < 400) {
          val body = response.body()
          if (body?.success != null && body.success == true) {
            return Result.success(body)
          } else {
            body?.message?.let {
              return Result.error(it)
            }
          }

        }
        val errorBody = JSONObject(
          response.errorBody()!!
            .charStream()
            .readText()
        )
//         return error("Network Error")

        return error(errorBody.getString("message"))
      } catch (e: Exception) {
        return error("Network Error")
//        return error(e.message ?: e.toString())
      }
    } else {
      return error("Network Error")
    }

  }

  private fun <T> error(message: String): Result<T> {
    return Result.error(message)
  }

  protected suspend fun <T> getResultMock(call: suspend () -> GeneralResponseModel<T>): Result<T?> {
    try {
      val response = call()
      if (response.success != null && response.success == true) {
        var data = response.data
        return Result.success(data)
      }

      return error(response.message)
    } catch (e: Exception) {
      return error(e.message ?: e.toString())
    }
  }

  protected suspend fun <T> getResponseBodyResult(
    call: suspend () -> Response<T>,
    networkUtils: NetworkUtils
  ): Result<T> {
    if (networkUtils.isConnectedToInternet) {
      try {
        val response = call()
        if (response.isSuccessful && response.code() < 400) {
          val body = response.body()
          if (body != null) {
            return Result.success(body)
          }

        }
        val errorBody = JSONObject(
          response.errorBody()!!
            .charStream()
            .readText()
        )
//        return error("Network Error")
        return error(errorBody.getString("message"))
      } catch (e: Exception) {
        e.printStackTrace()
        return error("Network Error")
//        return error(e.message ?: e.toString())
      }
    } else {
      return error("Network Error")
    }

  }

  protected suspend fun <T> getResponseBodyResultWithOutReturn(
    call: suspend () -> Response<T>,
    networkUtils: NetworkUtils
  ) {
    if (networkUtils.isConnectedToInternet) {
      try {
        val response = call()
        if (response.isSuccessful && response.code() < 400) {
          val body = response.body()
          if (body != null) {

          }

        }
        val errorBody = JSONObject(
          response.errorBody()!!
            .charStream()
            .readText()
        )
      } catch (e: Exception) {
        e.printStackTrace()
//        return error(e.message ?: e.toString())
      }
    } else {
    }

  }

  protected suspend fun getResultForGoogleMapsApi(
    call: suspend () -> Response<GooglePlacesResponse>,
    networkUtils: NetworkUtils
  ): Result<GooglePlacesResponse> {
    if (networkUtils.isConnectedToInternet) {
      try {
        val response = call()
        if (response.isSuccessful) {
          val body = response.body()
          if (body?.status != null && body.status == "OK") {
            return Result.success(body)
          } else {
            body?.errorMessage?.let {
              return Result.error(it)
            }
          }

        }
        val errorBody = JSONObject(
          response.errorBody()!!
            .charStream()
            .readText()
        )
//        return error("Network Error")
        return error(errorBody.getString("message"))
      } catch (e: Exception) {
        e.printStackTrace()
        return error("Network Error")
//        return error(e.message ?: e.toString())
      }
    } else {
      return error("Network Error")
    }

  }
}
