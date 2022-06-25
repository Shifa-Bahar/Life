package com.lifepharmacy.application.ui.rating.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemOrderRatingProductBinding
import com.lifepharmacy.application.databinding.ItemOrdersProductBinding
import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.SubOrderItem
import com.lifepharmacy.application.model.orders.SubOrderProductItem
import com.lifepharmacy.application.ui.orders.adapters.ClickShipmentProduct
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeInt

import kotlin.collections.ArrayList

class RatingProductsAdapter(
  context: Activity?,
  private val onItemTapped: ClickProductRating,
  private val subOrderId: String
) : RecyclerView.Adapter<RatingProductsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<SubOrderProductItem>? = ArrayList()
  var commentes: ArrayList<String?>? = ArrayList()
  var ratedValue: ArrayList<Float>? = ArrayList()
  var isAnonymous: ArrayList<Boolean>? = ArrayList()
  var activity: Activity? = context
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemOrderRatingProductBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_order_rating_product,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemOrderRatingProductBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<SubOrderProductItem>?) {
    arrayList?.clear()
    ratedValue?.clear()
    commentes?.clear()
    isAnonymous?.clear()
    if (list != null) {
      for (item in list) {
        ratedValue?.add(0F)
        commentes?.add("")
        isAnonymous?.add(false)
      }
      arrayList?.addAll(list)
    }
    notifyDataSetChanged()
  }

  fun setRatingValues(rating: Float, reviewValue: String, position: Int) {
    ratedValue?.set(position, rating)
    commentes?.set(position, reviewValue)
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    holder.binding?.let { bind(it, position) }
  }

  fun bind(binding: ItemOrderRatingProductBinding, position: Int) {
    val item = (arrayList ?: return)[position]
    val comment = (commentes ?: return)[position]
    binding.ratedValue = 0F
    var ratingValue = ratedValue?.get(position) ?: 0F

    binding.position = position
    binding.comments = comment
    binding.click = onItemTapped
    binding.subOrderId = subOrderId
    binding.btnWrite.visibility = View.GONE
    item.rating?.let {
      if (it > 0) {
        binding.btnWrite.visibility = View.VISIBLE
        ratingValue = it
      }
    }
    binding.isRated = ratingValue > 0F
    binding.item = item
    if (item.rating != null) {
      if (ratedValue?.get(position) ?: 0F > 0) {
        binding.simpleRatingBar.rating = ratedValue?.get(position) ?: 0F
      } else {
        binding.simpleRatingBar.rating = item.rating ?: 0.0F
      }

    } else {
      binding.simpleRatingBar.rating = 0.0F
    }
    if (ratingValue > 0F) {
      binding.simpleRatingBar.setRating(ratingValue)
      binding.ratedValue = ratingValue
    }

    binding.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
      binding.ratedValue = rating
      binding.btnWrite.visibility = View.VISIBLE
      if (rating > 0) {
        ratedValue?.set(position, rating)
      }
      if (rating != ratedValue?.get(position) ?: 0F && rating > 0) {

        onItemTapped.onClickProductRating(item.productDetails.id, rating, subOrderId)
      }

    }

  }
}