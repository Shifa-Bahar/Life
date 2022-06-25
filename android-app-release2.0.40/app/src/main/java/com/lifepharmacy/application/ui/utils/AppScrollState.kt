package com.lifepharmacy.application.ui.utils

import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.enums.ScrollingState
import javax.inject.Inject

class AppScrollState
@Inject constructor() {
  private var scrollingStateMut: MutableLiveData<ScrollingState> =
    MutableLiveData()

  fun setScrollDirection(state: Int) {
    val scrollingState = ScrollingState.fromId(state)
    scrollingStateMut.value = scrollingState
  }

  fun getScrollingState(): MutableLiveData<ScrollingState> {
    return scrollingStateMut
  }
}