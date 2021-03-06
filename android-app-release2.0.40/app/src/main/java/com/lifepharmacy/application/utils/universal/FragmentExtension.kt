package com.lifepharmacy.application.utils.universal

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.getNavigationResult(key: String = "result") =
  findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(key)

fun Fragment.setNavigationResult(result: String, key: String = "result") {
  findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}