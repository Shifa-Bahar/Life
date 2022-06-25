package com.lifepharmacy.application.ui.offers.adapters

import android.app.Activity
import android.graphics.Paint
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemOfferProductBinding
import com.lifepharmacy.application.databinding.ItemProductsVerticalBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.ui.cart.adapter.ClickCartProduct
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeProduct
import com.lifepharmacy.application.utils.PricesUtil
import kotlin.math.roundToInt

class OfferBottomProductAdapter(
  context: Activity,
  private val onItemTapped: ClickCartProduct,
  private val appManager: AppManager
) :
  RecyclerView.Adapter<OfferBottomProductAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<ProductDetails>? = ArrayList()
  var activity: Activity = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemOfferProductBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_offer_product,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemOfferProductBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<ProductDetails>?) {
//        arrayList?.clear()
    var oldSize = arrayList?.size ?: 0
    var rangeRemove = 0
    if (order != null) {
      arrayList?.addAll(order)
      notifyItemRangeInserted(oldSize, order.size)
    }
  }

  fun refreshData(list: ArrayList<ProductDetails>) {
    arrayList?.clear()
    arrayList?.addAll(list)
    notifyDataSetChanged()
  }

  fun removeItems() {
    arrayList?.clear()
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    holder.binding?.item = item
    holder.binding?.price = PricesUtil.getRelativePrice(item.prices, activity)
    holder.binding?.isInCart = appManager.offersManagers.checkIfProductAlreadyExistInCart(item)

    appManager.offersManagers.checkAndGetExistingQTY(item)?.let {
      holder.binding?.qty = it.toString()
    }
    holder.binding?.btnMinus?.setOnClickListener {
      holder.binding?.qty?.let {
        if (it.toInt() == 1) {
          MaterialAlertDialogBuilder(
            activity,
            R.style.ThemeOverlay_App_MaterialAlertDialog
          )
            .setTitle(activity.resources.getString(R.string.remove_product_title))
            .setMessage(activity.resources.getString(R.string.remove_product_descr))
            .setNegativeButton(activity.resources.getString(R.string.cancel)) { dialog, which ->
            }
            .setPositiveButton(activity.resources.getString(R.string.remove)) { dialog, which ->
              onItemTapped.onClickMinus(item,position)
              notifyItemChanged(position)
            }
            .show()
        } else {
          onItemTapped.onClickMinus(item,position)
          notifyItemChanged(position)
        }

      }

    }
    holder.binding?.clMain?.setOnClickListener {
      onItemTapped.onClickProduct(item,position)
    }
    holder.binding?.btnPlus?.setOnClickListener {
      onItemTapped.onClickPlus(item,position)
      notifyItemChanged(position)
    }
    holder.binding?.imgCart?.setOnClickListener {
      onItemTapped.onClickPlus(item,position)
      notifyItemChanged(position)
    }
    holder.binding?.btnNotify?.setOnClickListener {
      onItemTapped.onClickNotify(item,position)
    }
  }

}