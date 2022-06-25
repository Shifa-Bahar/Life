package com.lifepharmacy.application.ui.dashboard.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.model.blog.BlogModel
import java.lang.Exception

import kotlin.collections.ArrayList

class BlogAdapter(context: Activity, private val onItemTapped: ClickBlog) :
  RecyclerView.Adapter<BlogAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<BlogModel>? = ArrayList()
  var activity: Activity = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemBlogBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_blog,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemBlogBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<BlogModel>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    holder.binding?.let { bind(it,position) }

  }
  private fun bind(binding: ItemBlogBinding,position: Int){
    val item = arrayList!![position]
    binding.clMain.animation =
      AnimationUtils.loadAnimation(activity, R.anim.mainfadein)
    binding.item = item
    binding.click = onItemTapped
    try {
      if(!item.embedded?.wpFeaturedmedia?.first()?.mediaDetails?.sizes?.full?.sourceUrl.isNullOrEmpty()){
        Glide.with(activity)
          .load(item.embedded?.wpFeaturedmedia?.first()?.mediaDetails?.sizes?.full?.sourceUrl)
          .error(R.drawable.image_place_holder)
          .fallback(R.drawable.image_place_holder)
          .placeholder(R.drawable.image_place_holder)
          .into(binding.appCompatImageView)
      }
    }catch (e:Exception){
      e.printStackTrace()
    }
  }

}