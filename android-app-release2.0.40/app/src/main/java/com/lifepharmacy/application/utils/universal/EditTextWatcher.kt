package com.lifepharmacy.application.utils.universal

import android.text.Editable
import android.text.TextWatcher

class EditTextWatcher(
  private val textCallBack: EditTextCallBack,
) : TextWatcher {
  override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

  }

  override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    textCallBack.onTextChange(s.toString())
  }

  override fun afterTextChanged(s: Editable?) {
  }
}