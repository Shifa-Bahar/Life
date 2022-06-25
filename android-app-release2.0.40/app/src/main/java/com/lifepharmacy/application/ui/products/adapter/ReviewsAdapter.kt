package com.lifepharmacy.application.ui.products.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemReviewBinding
import com.lifepharmacy.application.model.product.ProductReview

import kotlin.collections.ArrayList

class ReviewsAdapter(context: Activity?, val defaultComment: String) :
  RecyclerView.Adapter<ReviewsAdapter.ItemViewHolder>() {
  private var arrayList: ArrayList<ProductReview>? = ArrayList()
  private var activity: Activity? = context
  private var rowIndex = 0
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemReviewBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_review,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemReviewBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<ProductReview>?) {
    arrayList?.clear()
    if (list != null) {
      arrayList?.addAll(list)
    }
    notifyDataSetChanged()
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