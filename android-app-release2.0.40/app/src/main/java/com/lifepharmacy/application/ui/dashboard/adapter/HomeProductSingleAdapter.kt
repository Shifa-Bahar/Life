package com.lifepharmacy.application.ui.dashboard.adapter

import android.app.Activity
import android.graphics.Paint
import android.transition.Slide
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemProductsVerticalSingleHomeBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.managers.CartManager
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.utils.PricesUtil
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDouble
import kotlin.math.roundToInt


class HomeProductSingleAdapter(
  context: Activity,
  private val onItemTapped: ClickHomeProduct,
  private val appManager: AppManager,
  val perOrderTitle: String
) :
  RecyclerView.Adapter<HomeProductSingleAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<ProductDetails>? = ArrayList()
  var activity: Activity = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemProductsVerticalSingleHomeBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_products_vertical_single_home,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemProductsVerticalSingleHomeBinding? = DataBindingUtil.bind(itemView)
  }


  fun setDataChanged(order: ArrayList<ProductDetails>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    holder.binding?.isBorder = false
    val displayMetrics = DisplayMetrics()
    activity.windowManager?.getDefaultDisplay()?.getMetrics(displayMetrics)
    val screenWidth = displayMetrics.widthPixels.intToNullSafeDouble()
    val screenheight = displayMetrics.heightPixels.intToNullSafeDouble()
//    recyclerViewProducts
    val withPers = screenWidth / 3
    val heightPers = screenheight / 3
//    holder.binding?.clMain?.layoutParams?.width = withPers.roundToInt()
//    holder.binding?.clMain?.layoutParams?.height = heightPers.roundToInt()
//      (withPers * 0.4).roundToInt()
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
      if (it >= 1) {
        holder.binding?.isBorder = true
      }
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
//              holder.binding?.isBorder = false
            }
            .show()
        } else {
          onItemTapped.onClickMinus(item, position)
          notifyItemChanged(position)
        }

      }

    }

//    holder.binding?.btnNotify?.setOnClickListener {
//      onItemTapped.onClickNotify(item, position)
//    }
//    holder.binding?.btnOrder?.setOnClickListener {
//      onItemTapped.onClickOrderOutOfStock(item, position)
//    }

    holder.binding?.btnPlus?.setOnClickListener {
      holder.binding?.isBorder = true
      onItemTapped.onClickPlus(item, position)
      notifyItemChanged(position)
    }

    holder.binding?.isInWishList = appManager.wishListManager.checkIfItemExistInWishList(item)
//    holder.binding?.imageViewHeart?.setOnClickListener {
//      onItemTapped.onClickWishList(item, position)
//      notifyItemChanged(position)
//    }
    holder.binding?.imgCart?.setOnClickListener {
      Log.e(TAG, "onBindViewHolder: ${item.title}")
//      holder.binding?.clMain?.animation =
//        AnimationUtils.loadAnimation(activity, R.anim.mainfadein)
//      val rotate = AnimationUtils.loadAnimation(activity,R.anim.rotateborder)
      holder.binding?.isBorder = true
//      holder.binding?.cardView4?.setBackground(activity.getDrawable(R.drawable.white_with_round_corner_blue_border));

//      holder.binding?.isSelector?.equals(true)

      val rotate =
        AnimationUtils.loadAnimation(activity, R.anim.rotateborder)

//      holder.binding?.imageViewIcon?.startAnimation(rotate)
//      holder.binding?.cardView4?.startAnimation(rotate)

      val slide = Slide()
      slide.slideEdge = Gravity.END
      TransitionManager.beginDelayedTransition(holder.binding!!.constraintLayoutQuantity, slide)
      holder.binding!!.constraintLayoutQuantity.visibility = View.VISIBLE
      onItemTapped.onClickAddProduct(item, position)
      notifyItemChanged(position)
    }
  }

}