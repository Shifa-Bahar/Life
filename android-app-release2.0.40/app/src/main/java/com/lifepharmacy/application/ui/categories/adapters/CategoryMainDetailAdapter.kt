package com.lifepharmacy.application.ui.categories.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.model.category.CategoryMainModel
import com.lifepharmacy.application.ui.categories.fragments.ClickCategoriesFragment
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeSubItem

import kotlin.collections.ArrayList

class CategoryMainDetailAdapter(
  context: Activity?,
  private val onItemTapped: ClickSubCategory,
  private val onSectionProduct: ClickSectionProductsCategory,
  private val homeSubItem: ClickHomeSubItem,
  private val categoryClick: ClickCategoriesFragment
) :
  RecyclerView.Adapter<CategoryMainDetailAdapter.ItemViewHolder>() {
  var mainCategoryModel: CategoryMainModel = CategoryMainModel()
  var activity: Activity? = context
  var viewType = 0
  override fun getItemViewType(position: Int): Int {
    return when (position) {
      0 -> {
        0
      }
      1 -> {
        1
      }
      else -> {
        2
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    this.viewType = viewType
    return when (viewType) {
      0 -> {
        val binding: LayoutBannerWithTextBinding = DataBindingUtil.inflate(
          LayoutInflater.from(activity),
          R.layout.layout_banner_with_text,
          parent, false
        )
        ItemViewHolder(binding.root, viewType)
      }
      1 -> {
        val binding: LayoutSimpleRecyclerViewBinding = DataBindingUtil.inflate(
          LayoutInflater.from(activity),
          R.layout.layout_simple_recycler_view,
          parent, false
        )
        ItemViewHolder(binding.root, viewType)
      }
      else -> {
        val binding: LayoutSimpleRecyclerViewBinding = DataBindingUtil.inflate(
          LayoutInflater.from(activity),
          R.layout.layout_simple_recycler_view,
          parent, false
        )
        ItemViewHolder(binding.root, viewType)
      }
    }
  }


  override fun getItemCount(): Int {
    return 3
  }

  class ItemViewHolder internal constructor(itemView: View, int: Int) :
    RecyclerView.ViewHolder(itemView) {
    var bindingBanner: LayoutBannerWithTextBinding? = null
    var bindingSectionLayout: LayoutSimpleRecyclerViewBinding? = null
    var bindingChildLayout: LayoutSimpleRecyclerViewBinding? = null

    init {
      when (int) {
        0 -> {
          bindingBanner = DataBindingUtil.bind(itemView)
        }
        1 -> {
          bindingSectionLayout = DataBindingUtil.bind(itemView)
        }
        else -> {
          bindingChildLayout = DataBindingUtil.bind(itemView)
        }
      }
    }
  }

  fun setDataChanged(data: CategoryMainModel) {
    mainCategoryModel = data
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    when (position) {
      0 -> {
        holder.bindingBanner?.let { bindBanner(it) }
      }
      1 -> {
        holder.bindingSectionLayout?.let { bindingSection(it) }
      }
      else -> {
        holder.bindingChildLayout?.let { bindChild(it) }
      }
    }
  }

  private fun bindBanner(binding: LayoutBannerWithTextBinding) {
    binding.image = mainCategoryModel.images
    binding.click = categoryClick

  }

  private fun bindingSection(binding: LayoutSimpleRecyclerViewBinding) {
    binding.isThereAnyItems = !mainCategoryModel.sections.isNullOrEmpty()
    val sectionAdapter =
      SectionCategoryAdapter(activity, onSectionProduct, homeSubItem)
    binding.rvSections.adapter = sectionAdapter
    val animator: RecyclerView.ItemAnimator? = binding.rvSections.itemAnimator
    if (animator is SimpleItemAnimator) {
      animator.supportsChangeAnimations = false
    }
    binding.rvSections.itemAnimator?.changeDuration = 0

  }

  private fun bindChild(binding: LayoutSimpleRecyclerViewBinding) {
    binding.isThereAnyItems = !mainCategoryModel.sections.isNullOrEmpty()
    val childAdapter = CategoryChildsAdapter(activity, onItemTapped, homeSubItem)
    binding.rvSections.adapter = childAdapter
    val animator: RecyclerView.ItemAnimator? = binding.rvSections.itemAnimator
    if (animator is SimpleItemAnimator) {
      animator.supportsChangeAnimations = false
    }
    binding.rvSections.itemAnimator?.changeDuration = 0
  }

}