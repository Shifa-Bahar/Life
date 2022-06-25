package com.lifepharmacy.application.ui.utils

import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.utils.universal.Logger
import javax.inject.Inject

class LoadingState
@Inject constructor() {
  private var scrollingStateMut: MutableLiveData<Boolean> =
    MutableLiveData()
  var bottomReselected: MutableLiveData<Boolean> = MutableLiveData()
  var homeReselected: MutableLiveData<Boolean> = MutableLiveData()
  var categoryReselected: MutableLiveData<Boolean> = MutableLiveData()
  var prescriptionReselected: MutableLiveData<Boolean> = MutableLiveData()
  var profileReselected: MutableLiveData<Boolean> = MutableLiveData()
  var cartReselected: MutableLiveData<Boolean> = MutableLiveData()
  var animation = "loading_main.json"
  var amountSaved = 0.0

  private var showAnimation: MutableLiveData<Boolean> =
    MutableLiveData()

  fun setAnimationState(state: Boolean,animation:String ="loading_main.json",amountSaved:Double=0.0) {
    this.animation = animation
    if (state){
      Logger.d("Saved_Amount",amountSaved.toString())
      this.amountSaved = amountSaved
    }
    showAnimation.value = state

  }
  fun getAnimationState(): MutableLiveData<Boolean> {
    return showAnimation
  }
  fun setLoadingState(state: Boolean) {
    scrollingStateMut.value = state
  }

  fun getLoadingState(): MutableLiveData<Boolean> {
    return scrollingStateMut
  }
}