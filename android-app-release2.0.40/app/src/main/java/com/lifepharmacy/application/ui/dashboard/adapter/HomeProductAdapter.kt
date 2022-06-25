package com.lifepharmacy.application.ui.dashboard.adapter

import android.app.Activity
import android.graphics.Paint
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemProductsVerticalHomeBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.managers.CartManager
import com.lifepharmacy.application.utils.PricesUtil
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDouble
import kotlin.math.roundToInt


class HomeProductAdapter(
  context: Activity,
  private val onItemTapped: ClickHomeProduct,
  private val appManager: AppManager,
  val perOrderTitle: String
) :
  RecyclerView.Adapter<HomeProductAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<ProductDetails>? = ArrayList()
  var activity: Activity = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemProductsVerticalHomeBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_products_vertical_home,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemProductsVerticalHomeBinding? = DataBindingUtil.bind(itemView)
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
    holder.binding?.clMain?.layoutParams?.height = (withPers * 0.4).roundToInt()
    val item = arrayList!![position]
    holder.binding?.tvOrignalPrice?.paintFlags =
      holder.binding?.tvOrignalPrice?.paintFlags?.or(Paint.STRIKE_THRU_TEXT_FLAG)!!
    holder.binding?.item = item
    holder.binding?.price = PricesUtil.getRelativePrice(item.prices, activity)
    holder.binding?.isInCart = appManager.offersManagers.checkIfProductAlreadyExistInCart(item)

    holder.binding?.preOder = perOrderTitle
    holder.binding?.clMain?.setOnClickListener {
      onItemTapped.onProductClicked(item, position)
    }
    appManager.offersManagers.checkAndGetExistingQTY(item)?.let {
      holder.binding?.qty = it.toString()
    }
    holder.binding?.btnMinus?.setOnClickListener {
      holder.binding?.qty?.let {
        if (it.toInt() == 1) {
          MaterialAlertDialogBuilder(activity, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(activity.resources.getString(R.string.remove_product_title))
            .setMessage(activity.resources.getString(R.string.remove_product_descr))
            .setNegativeButton(activity.resources.getString(R.string.cancel)) { dialog, which ->
            }
            .setPositiveButton(activity.resources.getString(R.string.remove)) { dialog, which ->
              onItemTapped.onClickMinus(item, position)
              notifyItemChanged(position)
            }
            .show()
        } else {
          onItemTapped.onClickMinus(item, position)
          notifyItemChanged(position)
        }

      }

    }

    holder.binding?.btnNotify?.setOnClickListener {
      onItemTapped.onClickNotify(item, position)
    }
    holder.binding?.btnOrder?.setOnClickListener {
      onItemTapped.onClickOrderOutOfStock(item, position)
    }

    holder.binding?.btnPlus?.setOnClickListener {
      onItemTapped.onClickPlus(item, position)
      notifyItemChanged(position)
    }

    holder.binding?.isInWishList = appManager.wishListManager.checkIfItemExistInWishList(item)
    holder.binding?.imageViewHeart?.setOnClickListener {
      onItemTapped.onClickWishList(item, position)
      notifyItemChanged(position)
    }
    holder.binding?.imgCart?.setOnClickListener {
      onItemTapped.onClickAddProduct(item, position)
      notifyItemChanged(position)
    }
  }

}