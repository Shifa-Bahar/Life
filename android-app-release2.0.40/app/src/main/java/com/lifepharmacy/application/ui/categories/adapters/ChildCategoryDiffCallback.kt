package com.lifepharmacy.application.ui.categories.adapters

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.lifepharmacy.application.model.category.Children

/**
 * Created by Zahid Ali
 */
class ChildCategoryDiffCallback (private val oldList: ArrayList<Children>, private val newList: ArrayList<Children>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id === newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        val (_, value, description) = oldList[oldPosition]
        val (_, value1, name1) = newList[newPosition]

        return description == name1 && value == value1
    }

    @Nullable
    override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
        return super.getChangePayload(oldPosition, newPosition)
    }
}