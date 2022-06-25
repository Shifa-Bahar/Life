package com.lifepharmacy.application.utils

import android.view.View
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.lifepharmacy.application.enums.ShipmentType
import com.lifepharmacy.application.model.product.*

object LayoutBindingAdapters {
  @JvmStatic
  @BindingAdapter("setPriceLayoutVisibility")
  fun setPriceLayoutVisibility(
    view: LinearLayout,
    availability: ProductDetails?,
  ) {
    when (availability?.stockAvailability()) {
      ShipmentType.OUT_OF_STOCK -> {
        view.visibility = View.INVISIBLE
      }
      else -> {
        view.visibility = View.VISIBLE
      }
    }
  }

  @JvmStatic
  @BindingAdapter("setQuantityLayoutVisibility")
  fun setQuantityLayoutVisibility(
    view: ConstraintLayout,
    availability: ProductDetails?,
  ) {
    when (availability?.stockAvailability()) {
      ShipmentType.OUT_OF_STOCK -> {
        view.visibility = View.GONE
      }
      else -> {
        view.visibility = View.VISIBLE
      }
    }
  }

  @JvmStatic
  @BindingAdapter("setCardAlphaOnAvailability")
  fun setCardAlphaOnAvailability(button: CardView, availability: ProductDetails?) {

    when (availability?.stockAvailability()) {
      ShipmentType.OUT_OF_STOCK -> {
        button.alpha = 0.5F
      }
      else -> {
        button.alpha = 1.0F
      }
    }
//    if (enable == true) {
//      button.alpha = 1.0F
//      button.isEnabled = true
//    } else {
//      button.alpha = 0.5F
//      button.isEnabled = false
//    }
  }

  @JvmStatic
  @BindingAdapter("setConstraintLayoutAlpha")
  fun setConstraintLayoutAlpha(button: ConstraintLayout, alpha: Boolean?) {

    if (alpha == true) {
      button.alpha = 1.0F
    } else {
      button.alpha = 0.5F
    }

  }
}