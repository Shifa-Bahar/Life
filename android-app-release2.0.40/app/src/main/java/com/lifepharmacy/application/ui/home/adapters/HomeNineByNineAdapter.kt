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
import com.lifepharmacy.application.databinding.ItemNineByNineTilesBinding
import com.lifepharmacy.application.databinding.ItemOffersTagsBinding
import com.lifepharmacy.application.databinding.ItemProductsVerticalHomeBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.home.SectionData
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeProduct
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeSubItem
import com.lifepharmacy.application.utils.PricesUtil
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDouble
import kotlin.math.roundToInt


class HomeNineByNineAdapter(
  context: Activity,
  private val onItemTapped: ClickHomeSubItem
) :
  RecyclerView.Adapter<HomeNineByNineAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<SectionData>? = ArrayList()
  var activity: Activity = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemNineByNineTilesBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_nine_by_nine_tiles,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemNineByNineTilesBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<SectionData>?) {
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
    val withPers = screenWidth * 0.33333333334
    holder.binding?.clMain?.layoutParams?.width = withPers.roundToInt()
    val layoutParams = holder.binding?.image?.layoutParams as ConstraintLayout.LayoutParams
    layoutParams.dimensionRatio = "108:59"
    holder.binding?.image?.layoutParams = layoutParams
    val item = (arrayList ?: return)[position]
    holder.binding?.item = item
    holder.binding?.click = onItemTapped
  }

}