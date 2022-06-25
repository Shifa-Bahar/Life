package com.lifepharmacy.application.utils

import retrofit2.Response


interface HandleNetworkCallBack<T> {
  fun handleWebserviceCallBackSuccess(response: Response<T>)
  fun handleWebserviceCallBackFailure(error: String?)
}