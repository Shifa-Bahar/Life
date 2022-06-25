package com.lifepharmacy.application.model.general


data class GeneralResponseModel<T>(
  var data: T?,
  var message: String,
  var success: Boolean?
)