package com.lifepharmacy.application.utils.universal

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DebouncingFunctions(
  lifecycle: Lifecycle,
  private val executeDebounce: FunctionDebounceListener
) {
  var debouncePeriod: Long = 200

  private val coroutineScope = lifecycle.coroutineScope

  private var searchJob: Job? = null

}