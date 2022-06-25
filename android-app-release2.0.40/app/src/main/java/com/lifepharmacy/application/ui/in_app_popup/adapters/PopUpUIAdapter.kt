package com.lifepharmacy.application.ui.in_app_popup.adapters

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.model.notifications.InAppNotificationDataModel

import kotlin.collections.ArrayList

class PopUpUIAdapter(context: Activity, private val onItemTapped: ClickPopUpUI) :
  RecyclerView.Adapter<PopUpUIAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<InAppNotificationDataModel>? = ArrayList()
  private var activity: Activity? = context
  var viewType = 1
  override fun getItemViewType(position: Int): Int {
    var returnInt = 1
    arrayList?.let {
      when (it[position].type) {
        "title" -> {
          returnInt = 1
        }
        "paragraph" -> {
          returnInt = 2
        }

        "image" -> {
          returnInt = 3
        }
        "button" -> {
          returnInt = 4
        }
        else -> {
          returnInt = 1
        }
      }
    }

    return returnInt
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopUpUIAdapter.ItemViewHolder {

    this.viewType = viewType

    if (viewType == 1) {
      val binding: LayoutTitleBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_title,
        parent, false
      )
      return PopUpUIAdapter.ItemViewHolder(binding.root, viewType)
    } else if (viewType == 2) {
      val binding: LayoutInAppDescriptionBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_in_app_description,
        parent, false
      )
      return PopUpUIAdapter.ItemViewHolder(binding.root, viewType)
    } else
      if (viewType == 3) {
        val binding: LayoutImageBinding = DataBindingUtil.inflate(
          LayoutInflater.from(activity),
          R.layout.layout__image,
          parent, false
        )
        return PopUpUIAdapter.ItemViewHolder(binding.root, viewType)
      } else if (viewType == 4) {
        val binding: LayoutInAppButtonBinding = DataBindingUtil.inflate(
          LayoutInflater.from(activity),
          R.layout.layout_in_app_button,
          parent, false
        )
        return PopUpUIAdapter.ItemViewHolder(binding.root, viewType)
      } else {
        val binding: LayoutTitleBinding = DataBindingUtil.inflate(
          LayoutInflater.from(activity),
          R.layout.layout_title,
          parent, false
        )
        return PopUpUIAdapter.ItemViewHolder(binding.root, viewType)
      }

  }

  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View, int: Int) :
    RecyclerView.ViewHolder(itemView) {
    var bindTitle: LayoutTitleBinding? = null
    var bindDescription: LayoutInAppDescriptionBinding? = null
    var bindImage: LayoutImageBinding? = null
    var bindButton: LayoutInAppButtonBinding? = null

    init {
      when (int) {
        1 -> {
          bindTitle = DataBindingUtil.bind(itemView)
        }
        2 -> {
          bindDescription = DataBindingUtil.bind(itemView)
        }
        3 -> {
          bindImage = DataBindingUtil.bind(itemView)
        }
        4 -> {
          bindButton = DataBindingUtil.bind(itemView)
        }
        else -> {
          bindTitle = DataBindingUtil.bind(itemView)
        }
      }


    }
  }

  fun setDataChanged(list: ArrayList<InAppNotificationDataModel>?) {
    arrayList?.clear()
    if (list != null) {
      arrayList?.addAll(list)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    arrayList?.let {
      when (it[position].type) {
        "title" -> {
          holder.bindTitle?.let { bindTitle(it, position) }
        }
        "image" -> {
          holder.bindImage?.let { bindImage(it, position) }
        }
        "paragraph" -> {
          holder.bindDescription?.let { bindDescription(it, position) }

        }
        "button" -> {
          holder.bindButton?.let { bindButton(it, position) }
        }
        else -> {
          holder.bindTitle?.let { bindTitle(it, position) }
        }
      }
    }

  }

  private fun bindTitle(binding: LayoutTitleBinding, position: Int) {
    val item = arrayList?.get(position)
    binding.item = item
    when (item?.alignment) {
      "left" -> {
        binding.tvTitle.apply {
          textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }
      }
      "right" -> {
        binding.tvTitle.apply {
          textAlignment = View.TEXT_ALIGNMENT_TEXT_END
        }
      }
      "center" -> {
        binding.tvTitle.apply {
          textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
      }
      else -> {
        binding.tvTitle.apply {
          textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }
      }
    }
  }

  private fun bindDescription(binding: LayoutInAppDescriptionBinding, position: Int) {
    val item = arrayList?.get(position)
    binding.item = item
    when (item?.alignment) {
      "left" -> {
        binding.tvTitle.apply {
          textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }
      }
      "right" -> {
        binding.tvTitle.apply {
          textAlignment = View.TEXT_ALIGNMENT_TEXT_END
        }
      }
      "center" -> {
        binding.tvTitle.apply {
          textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
      }
      else -> {
        binding.tvTitle.apply {
          textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }
      }
    }

  }

  private fun bindImage(binding: LayoutImageBinding, position: Int) {
    val item = arrayList?.get(position)
    binding.item = item

    val layoutParams = binding.imageView.layoutParams as ConstraintLayout.LayoutParams
    layoutParams.dimensionRatio =
      "${item?.content?.imageWidth ?: 1}:${item?.content?.imageHeight ?: 1}"
    when (item?.content?.fullWidth) {
      true -> {
        layoutParams.marginEnd = 0
        layoutParams.marginStart = 0
      }
      else -> {
        layoutParams.marginEnd = 20
        layoutParams.marginStart = 20
      }
    }
    binding.imageView?.layoutParams = layoutParams
    binding.imageView?.scaleType = ImageView.ScaleType.FIT_XY
  }

  private fun bindButton(binding: LayoutInAppButtonBinding, position: Int) {
    val item = arrayList?.get(position)
    binding.item = item
    binding.click = onItemTapped
//    try {
//      binding.btnTitle.setBackgroundColor(
//        Color.parseColor(
//          item?.content?.buttonBackground ?: "#365FC9"
//        )
//      );
//    } catch (e: Exception) {
//      e.printStackTrace()
//      binding.btnTitle.setBackgroundColor(
//        Color.parseColor(
//          "#365FC9"
//        )
//      );
//    }

//    binding.btnTitle.setTextColor(
//      Color.parseColor(
//        item?.button?.textColor ?: "#FFFFFF"
//      )
//    );
  }
}