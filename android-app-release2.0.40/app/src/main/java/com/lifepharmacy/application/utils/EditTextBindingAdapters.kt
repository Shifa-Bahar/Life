package com.lifepharmacy.application.utils

import android.annotation.SuppressLint
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.lifepharmacy.application.R

object EditTextBindingAdapters {
    @JvmStatic
    @BindingAdapter("textChangedListener")
    fun bindTextWatcher(editText: EditText, textWatcher: TextWatcher?) {
        editText.addTextChangedListener(textWatcher)
    }

    @BindingAdapter("isBold")
    fun setBold(view: TextView?, isBold: Boolean) {
        if (!isBold) {
            val typeface = view?.let { ResourcesCompat.getFont(it.context, R.font.opensans_bold) }
            view?.typeface = typeface
        } else {
            val typeface = view?.let { ResourcesCompat.getFont(it.context, R.font.opensans_regular) }
            view?.typeface = typeface
        }
    }

  @BindingAdapter("isBoldOrSemiBold")
  fun isBoldOrSemiBold(view: TextView?, isBold: Boolean) {
    if (!isBold) {
      val typeface = view?.let { ResourcesCompat.getFont(it.context, R.font.opensans_bold) }
      view?.typeface = typeface
    } else {
      val typeface = view?.let { ResourcesCompat.getFont(it.context, R.font.opensans_semi_bold) }
      view?.typeface = typeface
    }
  }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("android:text")
    fun setText(view: TextView, value: Int) {
        view.text = Integer.toString(value)
    }
}