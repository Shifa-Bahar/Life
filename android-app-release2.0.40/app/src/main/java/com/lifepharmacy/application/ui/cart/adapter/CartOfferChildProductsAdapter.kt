package com.lifepharmacy.application.ui.cart.adapter

import android.app.Activity
import android.graphics.Paint
import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemCartOffersSubBinding
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.cart.OffersCartModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.utils.PricesUtil
import com.lifepharmacy.application.utils.universal.CyclicTransitionDrawable


class CartOfferChildProductsAdapter(
  context: Activity,
  private val onItemTapped: ClickCartProduct,
  private val count: Int = 0,
) : RecyclerView.Adapter<CartOfferChildProductsAdapter.ItemViewHolder>() {
  var mainItem = OffersCartModel()
  var arrayList: ArrayList<CartModel>? = ArrayList()
  var activity: Activity = context
  var localCount = count


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemCartOffersSubBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_cart_offers_sub,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return localCount
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemCartOffersSubBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(mainItem: OffersCartModel) {
    localCount = mainItem.getSlotsCount()
    this.mainItem = mainItem
    arrayList?.clear()
    arrayList?.addAll(mainItem.products)
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    holder.binding?.clMain?.animation =
      AnimationUtils.loadAnimation(activity, R.anim.mainfadein)
    if (position >= arrayList!!.size) {
      holder.binding?.let { bindEmptyProduct(it, position) }
    } else {
      val item = arrayList!![position]
      holder.binding?.let { bindProducts(it, item, position) }
    }

//    holder.binding?.imgChecked?.setOnClickListener {
//      onItemTapped.onClickChecked(item)
//      notifyItemChanged(position)
//    }


  }

  private fun bindEmptyProduct(binding: ItemCartOffersSubBinding, position: Int) {
    binding.isEmpty = true
    if (mainItem.freeIndexes.contains(position)) {
      binding.isFree = true
    }
    binding.clMain.setOnClickListener {
      onItemTapped.onClickEmptyClicked(mainItem,position)
    }
//    val transition: TransitionDrawable = binding.clEmpty.background as TransitionDrawable
//    transition.startTransition(1000)
    val drawable1 = ContextCompat.getDrawable(activity, R.drawable.grey_with_round_corner)
    val drawable2 = ContextCompat.getDrawable(activity, R.drawable.bg_info_border_grey_solid)

    val ctd = CyclicTransitionDrawable(arrayOf(drawable1, drawable2))
    binding.clEmpty.background = ctd
    ctd.startTransition(1000,50)
  }

  private fun bindProducts(binding: ItemCartOffersSubBinding, item: CartModel, position: Int) {
    binding.item = item
    binding.price = PricesUtil.getRelativePrice(item.productDetails.prices, activity)
//    holder.binding?.isChecked = cartUtil.isItemChecked(item)
    binding.click = onItemTapped
    if (mainItem.freeIndexes.contains(position)) {
      binding.isFree = true
      binding.tvPrice.paintFlags
        .or(Paint.STRIKE_THRU_TEXT_FLAG).let {
          binding.tvPrice.paintFlags = it
        }
    } else {
      binding.isEmpty = false
    }
//    holder.binding?.tvOrignalPrice?.paintFlags
//      ?.or(Paint.STRIKE_THRU_TEXT_FLAG)?.let {
//        holder.binding?.tvOrignalPrice?.setPaintFlags(
//          it
//        )
//      }
    binding.btnMinus.setOnClickListener {
      if (item.qty.toInt() == 1) {
        MaterialAlertDialogBuilder(activity, R.style.ThemeOverlay_App_MaterialAlertDialog)
          .setTitle(activity.resources.getString(R.string.remove_product_title))
          .setMessage(activity.resources.getString(R.string.remove_product_descr))
          .setNegativeButton(activity.resources.getString(R.string.cancel)) { _, _ ->
          }
          .setPositiveButton(activity.resources.getString(R.string.remove)) { _, _ ->
            onItemTapped.onClickMinus(item.productDetails,position)
            try {
              arrayList?.removeAt(position)
              notifyItemRemoved(position)
            } catch (e: Exception) {

            }

          }
          .show()
      } else {
        onItemTapped.onClickMinus(item.productDetails,position)
        notifyItemChanged(position)
      }


    }
    binding.btnPlus.setOnClickListener {
      onItemTapped.onClickPlus(item.productDetails,position)
      notifyItemChanged(position)
    }
    binding.tvRemove.setOnClickListener {
      MaterialAlertDialogBuilder(activity, R.style.ThemeOverlay_App_MaterialAlertDialog)
        .setTitle(activity.resources.getString(R.string.remove_product_title))
        .setMessage(activity.resources.getString(R.string.remove_product_descr))
        .setNegativeButton(activity.resources.getString(R.string.cancel)) { _, _ ->
        }
        .setPositiveButton(activity.resources.getString(R.string.remove)) { _, _ ->
          onItemTapped.onClickRemove(item.productDetails,position)
          arrayList?.removeAt(position)
          notifyItemRemoved(position)
          notifyDataSetChanged()
        }
        .show()

    }
  }

  fun removeItem(productDetails: ProductDetails) {
    var product =
      arrayList?.firstOrNull { cartModel -> cartModel.productDetails.id == productDetails.id }
    if (product != null) {
      var index: Int? = arrayList?.indexOf(product)
      index?.let {
        arrayList?.removeAt(index)
        notifyItemRemoved(index)
      }
    }

  }

}