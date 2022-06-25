package com.lifepharmacy.application.utils

import com.lifepharmacy.application.model.search.agolia.Hits
import retrofit2.Response


interface HandleAlgoliaCallBack {
  fun handleWebserviceCallBackSuccess(hits: ArrayList<Hits>?)
}