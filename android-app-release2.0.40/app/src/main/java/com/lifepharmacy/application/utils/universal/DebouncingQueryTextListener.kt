package com.lifepharmacy.application.utils.universal

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class DebouncingQueryTextListener(
  lifecycle: Lifecycle,
  private val onDebouncingQueryTextChange: SearchDebounceListener,
  private val debouncePeriod: Long = 750
) : SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {

  private val coroutineScope = lifecycle.coroutineScope

  private var searchJob: Job? = null


  override fun onQueryTextSubmit(query: String?): Boolean {
    return false
  }

  override fun onQueryTextChange(newText: String?): Boolean {
    try {
      onDebouncingQueryTextChange.onSimpleTextChange(newText)
      searchJob?.cancel()
      searchJob = coroutineScope.launch {
        newText?.let {
          delay(debouncePeriod)
          onDebouncingQueryTextChange.onDebouncingQueryTextChange(newText)
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    return false
  }
}