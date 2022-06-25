package com.lifepharmacy.application.ui.offers.adapters

import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemCartOfferBottomSheetBinding
import com.lifepharmacy.application.databinding.ItemCartOffersSubBinding
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.managers.CartManager
import com.lifepharmacy.application.managers.OffersManagers
import com.lifepharmacy.application.model.cart.OffersCartModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.ui.cart.adapter.ClickCartProduct
import com.lifepharmacy.application.utils.PricesUtil
import java.lang.Exception

import kotlin.collections.ArrayList

class CartOfferBottomChildProductsAdapter(
  context: Activity,
  private val onItemTapped: ClickCartProduct,
  private val count: Int = 0,
) : RecyclerView.Adapter<CartOfferBottomChildProductsAdapter.ItemViewHolder>() {
  private var mainItem = OffersCartModel()
  var arrayList: ArrayList<CartModel>? = ArrayList()
  var activity: Activity = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemCartOfferBottomSheetBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_cart_offer_bottom_sheet,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return count
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemCartOfferBottomSheetBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(mainItem: OffersCartModel) {
    this.mainItem = mainItem
    arrayList?.clear()
    arrayList?.addAll(mainItem.products)
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    if (position >= arrayList!!.size) {
      holder.binding?.let { bindEmptyProduct(it, position) }
    } else {
      val item = arrayList!![position]
      holder.binding?.let { bindProducts(it, item, position) }
    }
  }

  private fun bindEmptyProduct(binding: ItemCartOfferBottomSheetBinding, position: Int) {
    binding.isEmpty = true
    if (mainItem.freeIndexes.contains(position)) {
      binding.isFree = true

    }
    binding.isFirstEmpty = position == arrayList?.lastIndex?.plus(1) ?: 0
    binding.clMain.setOnClickListener {
      onItemTapped.onClickEmptyClicked(mainItem,position)
    }

  }

  private fun bindProducts(
    binding: ItemCartOfferBottomSheetBinding,
    item: CartModel,
    position: Int
  ) {
    binding.item = item
    binding.price = PricesUtil.getRelativePrice(item.productDetails.prices, activity)
    if (mainItem.freeIndexes.contains(position)) {
      binding.isFree = true
      binding.tvPrice.paintFlags
        .or(Paint.STRIKE_THRU_TEXT_FLAG).let {
          binding.tvPrice.paintFlags = it
        }
    } else {
      binding.isEmpty = false
    }
    binding.ivCross.setOnClickListener {
      MaterialAlertDialogBuilder(
        activity,
        R.style.ThemeOverlay_App_MaterialAlertDialog
      )
        .setTitle(activity.resources.getString(R.string.remove_product_title))
        .setMessage(activity.resources.getString(R.string.remove_product_descr))
        .setNegativeButton(activity.resources.getString(R.string.cancel)) { dialog, which ->
        }
        .setPositiveButton(activity.resources.getString(R.string.remove)) { dialog, which ->
          onItemTapped.onClickMinus(item.productDetails,position)
          notifyItemChanged(position)
        }
        .show()
    }
  }
}