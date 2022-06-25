package com.lifepharmacy.application.ui.search.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemSearchSuggestionsBinding
import com.lifepharmacy.application.model.search.agolia.Hits

import kotlin.collections.ArrayList

class SearchSuggestionsAdapter(
  context: Activity?,
  val onItemTapped: ClickSearchSuggestionItem
) : RecyclerView.Adapter<SearchSuggestionsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<Hits>? = ArrayList()
  var activity: Activity? = context
  var terms: String = ""
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemSearchSuggestionsBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_search_suggestions,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size + 1 else 1
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemSearchSuggestionsBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<Hits>?, string: String) {
    arrayList?.clear()
    if (list != null) {
      arrayList?.addAll(list)
    }
    terms = string
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    if (position == arrayList?.size ?: 1) {

      val hits = Hits(
        query = terms
      )
      holder.binding?.item = hits
      //  holder.binding?.term = activity?.getString(R.string.searched_term)
      // holder.binding?.afterPrefix = " \"" + terms + "\""

      holder.binding?.click = onItemTapped
    } else {
      val item = (arrayList ?: return)[position]
      holder.binding?.item = item
      // holder.binding?.term = terms
      holder.binding?.afterPrefix = item.getString()
      holder.binding?.click = onItemTapped
    }


  }

}