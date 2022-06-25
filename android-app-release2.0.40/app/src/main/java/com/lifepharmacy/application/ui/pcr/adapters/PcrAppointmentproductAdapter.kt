package com.lifepharmacy.application.ui.pcr.adapters

import android.app.Activity
import android.graphics.Paint
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemAppointmentProductBinding
import com.lifepharmacy.application.databinding.ItemPcrListBinding
import com.lifepharmacy.application.databinding.ItemPcrPaymentListBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.pcr.appointmentdetailnew.Products
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.pcr.adapters.ClickPcrProduct

class PcrAppointmentproductAdapter(
  context: Activity?,
  private val appManager: AppManager
) :
  RecyclerView.Adapter<PcrAppointmentproductAdapter.ItemViewHolder>() {
// var rewardMain: GeneralResponseModel<RewardModel>? = null

  var arrayList: ArrayList<Products>? = ArrayList()
  var activity: Activity? = context
  var id: Int = 0
  var rowSelected = 0
  var oldSelection = 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemAppointmentProductBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_appointment_product,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }

  override fun getItemCount(): Int {
    //    return if (null != rewardMain) rewardMain!!.data?.data?.size!!
    return if (null != arrayList) arrayList!!.size else 0

  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemAppointmentProductBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(reward: ArrayList<Products>?) {
//    if (reward != null) {
//      rewardMain = reward
//    }
//    notifyDataSetChanged()
    arrayList?.clear()
    if (reward != null) {
      arrayList?.addAll(reward)
    }
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    holder.binding?.item = item
    holder.binding?.itemp = item.product_details
    holder.binding?.tvProductName?.text = item.product_details.title
    holder.binding?.tvQty?.text = "x${item.qty}"
    holder.binding?.tvPrice?.text = item.line_total.toString()
  }

  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyItemChanged(oldSelection)
    notifyItemChanged(rowSelected)
    oldSelection = rowSelected
  }


}