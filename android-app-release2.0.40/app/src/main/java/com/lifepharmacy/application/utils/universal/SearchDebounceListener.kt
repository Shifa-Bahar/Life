package com.lifepharmacy.application.utils.universal

/**
 * Created by Zahid Ali
 */
interface SearchDebounceListener {
  fun onDebouncingQueryTextChange(text:String?)
  fun onSimpleTextChange(text:String?)
}