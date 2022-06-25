package com.lifepharmacy.application.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.lifepharmacy.application.network.Result.Status.ERROR
import com.lifepharmacy.application.network.Result.Status.SUCCESS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <A> performNwOperation(networkCall: suspend () -> Result<A>): LiveData<Result<A>> =
  liveData(Dispatchers.IO) {
    emit(Result.loading())
    val responseStatus = networkCall.invoke()
    if (responseStatus.status == SUCCESS) {
      emit(Result.success(responseStatus.data!!))
    } else if (responseStatus.status == ERROR) {
      emit(Result.error(responseStatus.message!!))
    }
  }


fun <T, A> performNwAndSaveOperation(
  databaseQuery: () -> LiveData<T>,
  networkCall: suspend () -> Result<A>,
  saveCallResult: suspend (A) -> Unit
): LiveData<Result<A>> =
  liveData(Dispatchers.IO) {
    emit(Result.loading())
    val responseStatus = networkCall.invoke()
    if (responseStatus.status == SUCCESS) {
      emit(Result.success(responseStatus.data!!))
      saveCallResult(responseStatus.data)
    } else if (responseStatus.status == ERROR) {
      emit(Result.error(responseStatus.message!!))
    }
  }

fun performNwOperationWithoutReturn(
  networkCall: suspend () -> Unit,
  coroutineScope: CoroutineScope
) =
  coroutineScope.launch {
    val responseModel = networkCall.invoke()
  }
