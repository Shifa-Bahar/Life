package com.lifepharmacy.application.utils

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lifepharmacy.application.model.payment.TransactionMainModel

object ButtonBindingAdapters {
  @JvmStatic
  @BindingAdapter("setAlpha")
  fun bindAlpha(button: Button, enable: Boolean?) {
    if (enable == true) {
      button.background.alpha = 255
      button.isEnabled = true
    } else {
      button.background.alpha = 128
      button.isEnabled = false
    }
  }

  @JvmStatic
  @BindingAdapter(
    value = ["btnTextColor", "defaultColor", "bgColor", "defaultBgColor"],
    requireAll = false
  )
  fun setButtonTextColor(
    view: Button,
    btnTextColor: String?,
    defaultColor: String?,
    bgColor: String?,
    defaultBgColor: String?,
  ) {
    try {
      view.setTextColor(
        Color.parseColor(
          btnTextColor ?: "#FFFFFFFF"
        )
      )
      view.setBackgroundColor(
        Color.parseColor(
          bgColor ?: "#365FC9"
        )
      )
    } catch (e: Exception) {
      view.setTextColor(
        Color.parseColor(
          defaultColor
        )
      )
      view.setBackgroundColor(
        Color.parseColor(
          defaultBgColor
        )
      );
    }
  }

  @JvmStatic
  @BindingAdapter("bindButtonLoading")
  fun bindButtonLoading(button: Button, enable: Boolean?) {
    if (enable == true) {
      button.background.alpha = 255
      button.isEnabled = true
    } else {
      button.background.alpha = 128
      button.isEnabled = false
    }
  }

  @JvmStatic
  @BindingAdapter("setTextAlpha")
  fun setTextAlpha(button: TextView, enable: Boolean?) {
    if (enable == true) {
      button.alpha = 1.0F
      button.isEnabled = true
    } else {
      button.alpha = 0.5F
      button.isEnabled = false
    }
  }

  @JvmStatic
  @BindingAdapter("setImageAlpha")
  fun setImageAlpha(button: ImageView, enable: Boolean?) {
    if (enable == true) {
      button.alpha = 1.0F
      button.isEnabled = true
    } else {
      button.alpha = 0.5F
      button.isEnabled = false
    }
  }

  @JvmStatic
  @BindingAdapter("updateProfileButtonAlpha")
  fun updateProfileButtonAlpha(button: Button, enable: Boolean?) {
    if (enable == true) {
      button.background.alpha = 255
      button.isEnabled = true
    } else {
      button.background.alpha = 128
      button.isEnabled = false
    }
  }

  @JvmStatic
  @BindingAdapter("transactionOrderDetailButtonVisibility")
  fun transactionOrderDetailButtonVisibility(button: Button, transaction: TransactionMainModel?) {
    button.visibility = View.GONE
    if (transaction?.purpose == "order_creation" && transaction.type == "charge") {
      if (transaction.method == "cash") {
        button.visibility = View.VISIBLE
      } else if (transaction.method == "card" && transaction.status == 1) {
        button.visibility = View.VISIBLE
      }
    }
  }
}