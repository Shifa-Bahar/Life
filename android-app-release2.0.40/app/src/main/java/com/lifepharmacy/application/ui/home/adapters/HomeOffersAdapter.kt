package com.lifepharmacy.application.ui.home.adapters

import android.app.Activity
import android.graphics.Paint
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemHomeOfferBinding
import com.lifepharmacy.application.databinding.ItemProductsVerticalHomeBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.managers.CartManager
import com.lifepharmacy.application.utils.PricesUtil
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDouble
import kotlin.math.roundToInt


class HomeOffersAdapter(
  context: Activity,
  private val onItemTapped: ClickHomeOffer,
) :
  RecyclerView.Adapter<HomeOffersAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<ProductDetails>? = ArrayList()
  var activity: Activity = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemHomeOfferBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_home_offer,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return 5
//    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemHomeOfferBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<ProductDetails>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val displayMetrics = DisplayMetrics()
    activity.windowManager?.getDefaultDisplay()?.getMetrics(displayMetrics)
    val screenWidth = displayMetrics.widthPixels.intToNullSafeDouble()
    val withPers = screenWidth * 0.85
    holder.binding?.clMain?.layoutParams?.width = withPers.roundToInt()
//    val layoutParams = holder.binding?.imgBanner?.layoutParams as ConstraintLayout.LayoutParams
//    layoutParams.dimensionRatio = "$imageWidth:$imageHeight"
//    holder.binding?.imgBanner?.layoutParams = layoutParams
//    val item = arrayList!![position]

  }

}