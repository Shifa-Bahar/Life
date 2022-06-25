package com.lifepharmacy.application.ui.products.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemReviewBinding
import com.lifepharmacy.application.databinding.ItemReviewCardBinding
import com.lifepharmacy.application.model.product.ProductReview

import kotlin.collections.ArrayList

class ReviewsCardsAdapter(context: Activity?, val defaultComment: String) :
  RecyclerView.Adapter<ReviewsCardsAdapter.ItemViewHolder>() {
  private var arrayList: ArrayList<ProductReview>? = ArrayList()
  private var activity: Activity? = context
  private var rowIndex = 0
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemReviewCardBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_review_card,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemReviewCardBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<ProductReview>?) {
    if (arrayList.isNullOrEmpty()) {
      arrayList = ArrayList()
    }
    val oldSize = arrayList?.size ?: 0
    var rangeRemove = 0
    if (list != null) {
      arrayList?.addAll(list)
      notifyItemRangeInserted(oldSize, list.size)
//      notifyDataSetChanged()
    }
  }


  fun refreshData(list: ArrayList<ProductReview>?) {
    arrayList?.clear()
    if (arrayList.isNullOrEmpty()) {
      arrayList = ArrayList()
    }
    if (list != null) {
      arrayList?.addAll(list)
      notifyDataSetChanged()
    }
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]
    holder.binding?.item = item
    holder.binding?.position = position
    holder.binding?.selected = rowIndex == position
    holder.binding?.simpleRatingBar?.rating = item.rating
    holder.binding?.defaultComment = defaultComment

  }

  fun selectedItem(position: Int?) {
    if (position != null) {
      rowIndex = position
      notifyDataSetChanged()
    }

  }

}