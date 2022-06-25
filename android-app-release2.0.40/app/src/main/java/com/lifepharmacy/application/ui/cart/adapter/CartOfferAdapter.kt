package com.lifepharmacy.application.ui.cart.adapter

import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemCartBinding
import com.lifepharmacy.application.databinding.ItemCartFreeGiftBinding
import com.lifepharmacy.application.databinding.ItemCartNormalBinding
import com.lifepharmacy.application.databinding.ItemCartOffersBinding
import com.lifepharmacy.application.managers.CartManager
import com.lifepharmacy.application.managers.OffersManagers
import com.lifepharmacy.application.model.cart.OffersCartModel
import com.lifepharmacy.application.model.product.OffersType
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.utils.PricesUtil

import kotlin.collections.ArrayList

class CartOfferAdapter(
  context: Activity,
  private val onItemTapped: ClickOfferCartProduct,
  private val onCartProduct: ClickCartProduct
) : RecyclerView.Adapter<CartOfferAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<OffersCartModel>? = ArrayList()
  var activity: Activity = context
  var viewType = 0

  //  var cartOfferChildProductsAdapter = CartOfferChildProductsAdapter(activity,onCartProduct)
  override fun getItemViewType(position: Int): Int {
    var returnInt = 0
    arrayList?.let {
      returnInt = when (OffersType.BXGY) {
        it[position].offers?.getTypeEnum() -> {
          1
        }
        else -> {
          0
        }
      }
    }
    return returnInt
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    this.viewType = viewType
    return when (viewType) {
      1 -> {
        val binding: ItemCartOffersBinding = DataBindingUtil.inflate(
          LayoutInflater.from(activity),
          R.layout.item_cart_offers,
          parent, false
        )
        ItemViewHolder(binding.root, viewType)
      }
      else -> {
        val binding: ItemCartNormalBinding = DataBindingUtil.inflate(
          LayoutInflater.from(activity),
          R.layout.item_cart_normal,
          parent, false
        )
        ItemViewHolder(binding.root, viewType)
      }
    }
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View, int: Int) :
    RecyclerView.ViewHolder(itemView) {
    var bindingCart: ItemCartNormalBinding? = null
    var bindingCartOffers: ItemCartOffersBinding? = null
    var bindCartFreeGiftBinding: ItemCartFreeGiftBinding? = null

    init {
      when (int) {
        1 -> {
          bindingCartOffers = DataBindingUtil.bind(itemView)
        }
        else -> {
          bindingCart = DataBindingUtil.bind(itemView)
        }
      }
    }
  }

  fun setDataChanged(order: ArrayList<OffersCartModel>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }

  fun clear() {
    arrayList?.clear()
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = (arrayList ?: return)[position]
    when (viewType) {
      1 -> {
        holder.bindingCartOffers?.let {
          bindCartOffers(item, position, it)
        }
      }

      else -> {
        holder.bindingCart?.let {
          bindCartProducts(item, position, it)
        }
      }
    }

  }

  private fun bindFreeGift(
    item: OffersCartModel,
    position: Int,
    bindings: ItemCartFreeGiftBinding
  ) {
    val internalItem = item.products[0]
    bindings.item = internalItem

  }


  private fun bindCartOffers(
    item: OffersCartModel,
    position: Int,
    bindingCartOffers: ItemCartOffersBinding
  ) {
    bindingCartOffers.click = onItemTapped
    bindingCartOffers.position = position
    bindingCartOffers.item = item
    val cartOfferChildProductsAdapter =
      CartOfferChildProductsAdapter(activity, onCartProduct, item.getSlotsCount())
    bindingCartOffers.rvSubItems.adapter = cartOfferChildProductsAdapter
    val animator: RecyclerView.ItemAnimator? = bindingCartOffers.rvSubItems.itemAnimator
    if (animator is SimpleItemAnimator) {
      animator.supportsChangeAnimations = false
    }
    bindingCartOffers.rvSubItems.itemAnimator?.changeDuration = 0
    cartOfferChildProductsAdapter.setDataChanged(item)

//    bindingCartOffers.tvTotal.paintFlags
//      .or(Paint.STRIKE_THRU_TEXT_FLAG).let {
//        bindingCartOffers.tvTotal.paintFlags = it
//      }
//    bindingCartOffers.btnMinus.setOnClickListener {
//      if (item.parentProduct?.qty?.toInt() == 1) {
//        MaterialAlertDialogBuilder(activity, R.style.ThemeOverlay_App_MaterialAlertDialog)
//          .setTitle(activity.resources.getString(R.string.remove_product_title))
//          .setMessage(activity.resources.getString(R.string.remove_product_descr))
//          .setNegativeButton(activity.resources.getString(R.string.cancel)) { _, _ ->
//          }
//          .setPositiveButton(activity.resources.getString(R.string.remove)) { _, _ ->
//            item.parentProduct?.let {
//              onItemTapped.onClickMinus(it.productDetails)
//            }
//
//            try {
//              arrayList?.removeAt(position)
//              notifyItemRemoved(position)
//            } catch (e: Exception) {
//
//            }
//
//          }
//          .show()
//      } else {
//        item.parentProduct?.let {
//          onItemTapped.onClickMinus(it.productDetails)
//        }
//        notifyItemChanged(position)
//      }
//
//
//    }
//    bindingCartOffers.btnPlus.setOnClickListener {
//      item.parentProduct?.productDetails?.let { it1 -> onItemTapped.onClickPlus(it1) }
//      notifyItemChanged(position)
//    }
//    bindingCartOffers.tvRemove.setOnClickListener {
//      MaterialAlertDialogBuilder(activity, R.style.ThemeOverlay_App_MaterialAlertDialog)
//        .setTitle(activity.resources.getString(R.string.remove_product_title))
//        .setMessage(activity.resources.getString(R.string.remove_product_descr))
//        .setNegativeButton(activity.resources.getString(R.string.cancel)) { _, _ ->
//        }
//        .setPositiveButton(activity.resources.getString(R.string.remove)) { _, _ ->
//          item.parentProduct?.productDetails?.let { it1 -> onItemTapped.onClickRemove(it1) }
//          arrayList?.removeAt(position)
//          notifyItemRemoved(position)
//          notifyDataSetChanged()
//        }
//        .show()
//
//    }
//    bindingCartOffers.imgChecked.setOnClickListener {
//      onItemTapped.onClickChecked(item)
//      notifyItemChanged(position)
//    }
  }

  private fun bindCartProducts(
    item: OffersCartModel,
    position: Int,
    bindingCart: ItemCartNormalBinding
  ) {
    val internalItem = item.products[0]
    bindingCart.item = internalItem
    bindingCart.offers = item.offers
    bindingCart.price =
      internalItem.productDetails.prices.let { PricesUtil.getRelativePrice(it, activity) }
    bindingCart.click = onCartProduct
    bindingCart.tvOrignalPrice.paintFlags
      .or(Paint.STRIKE_THRU_TEXT_FLAG).let {
        bindingCart.tvOrignalPrice.paintFlags = it
      }
    bindingCart.btnMinus.setOnClickListener {
      if (internalItem.qty.toInt() == 1) {
        MaterialAlertDialogBuilder(activity, R.style.ThemeOverlay_App_MaterialAlertDialog)
          .setTitle(activity.resources.getString(R.string.remove_product_title))
          .setMessage(activity.resources.getString(R.string.remove_product_descr))
          .setNegativeButton(activity.resources.getString(R.string.cancel)) { _, _ ->
          }
          .setPositiveButton(activity.resources.getString(R.string.remove)) { _, _ ->
            internalItem.productDetails.let { it1 -> onItemTapped.onClickMinus(it1, position) }
            try {
              arrayList?.removeAt(position)
              notifyItemRemoved(position)
            } catch (e: Exception) {

            }

          }
          .show()
      } else {
        internalItem.productDetails.let { it1 -> onItemTapped.onClickMinus(it1, position) }
        notifyDataSetChanged()
      }


    }
    bindingCart.btnPlus.setOnClickListener {
      internalItem.productDetails.let { it1 -> onItemTapped.onClickPlus(it1, position) }
      notifyItemChanged(position)
    }
    bindingCart.tvRemove.setOnClickListener {
      MaterialAlertDialogBuilder(activity, R.style.ThemeOverlay_App_MaterialAlertDialog)
        .setTitle(activity.resources.getString(R.string.remove_product_title))
        .setMessage(activity.resources.getString(R.string.remove_product_descr))
        .setNegativeButton(activity.resources.getString(R.string.cancel)) { _, _ ->
        }
        .setPositiveButton(activity.resources.getString(R.string.remove)) { _, _ ->
          internalItem.productDetails.let { it1 -> onItemTapped.onClickRemove(it1, position) }
          arrayList?.removeAt(position)
          notifyItemRemoved(position)
          notifyDataSetChanged()
        }
        .show()
    }
    bindingCart.clMain.setOnClickListener {
      onItemTapped.onClickProduct(internalItem.productDetails, position)
    }
    bindingCart.qty = internalItem.qty.toString()
//    bindingCart.imgChecked.setOnClickListener {
//      onItemTapped.onClickChecked(item)
//      notifyItemChanged(position)
//    }
  }
//  fun removeItem(productDetails: ProductDetails){
//    var product=
//      arrayList?.firstOrNull { cartModel -> cartModel.productDetails.id == productDetails.id }
//    if (product!=null){
//      var index: Int? = arrayList?.indexOf(product)
//      index?.let {
//        arrayList?.removeAt(index)
//        notifyItemRemoved(index)
//      }
//    }
//
//  }

}