package com.lifepharmacy.application.ui.rewards.adapters


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
import com.lifepharmacy.application.databinding.ItemDateBinding
import com.lifepharmacy.application.databinding.ItemPcrListBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.pcr.pcrdatetime.Booking_slots
import com.lifepharmacy.application.model.pcr.pcrdatetime.Slots
import com.lifepharmacy.application.model.pcr.pcrlist.Products
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.pcr.adapters.ClickPcrDate
import com.lifepharmacy.application.ui.pcr.adapters.ClickPcrDateTime
import com.lifepharmacy.application.ui.pcr.fragments.PcrDateFragment
import com.lifepharmacy.application.utils.DateTimeUtil
import kotlinx.android.synthetic.main.layout_rewards_front.view.*

class PcrDateAdapter(
  context: Activity?, private val onItemTapped: ClickPcrDate,
  private val appManager: AppManager
) :
  RecyclerView.Adapter<PcrDateAdapter.ItemViewHolder>() {
// var rewardMain: GeneralResponseModel<RewardModel>? = null

  var arrayList: ArrayList<Booking_slots>? = ArrayList()
  var arrayListslots: ArrayList<Slots>? = ArrayList()
  var activity: Activity? = context
  var id: Int = 0
  var rowSelected = 0
  var oldSelection = 0
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemDateBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_date,
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
    var binding: ItemDateBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(reward: ArrayList<Booking_slots>?) {
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
//    val item = rewardMain?.data
    holder.binding?.item = item
    holder.binding?.position = position
    holder.binding?.tvDay?.text = DateTimeUtil.getWeekStringDateFromStringWithoutTimeZone(item.date)
    arrayListslots = item.slots
    holder.binding?.tvMonth?.text = DateTimeUtil.getStringDateFromStringWithoutTimeZone(item.date)
    holder.binding?.isSelected = rowSelected == position

    holder.binding?.click = onItemTapped
    holder.binding?.tvDay?.setOnClickListener {
      onItemTapped.onClickPcrDateTime(position, item)
    }
    holder.binding?.tvMonth?.setOnClickListener {
      onItemTapped.onClickPcrDateTime(position, item)
    }
//    holder.binding?.isInCart = appManager.offersManagers.checkIfPcrProductAlreadyExistInCart(item)
//    holder.binding?.price?.countryCode
//    holder.binding?.tvTags?.text =
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//        Html.fromHtml(item.short_description, Html.FROM_HTML_MODE_COMPACT)
//      } else {
//        Html.fromHtml(item.short_description)
//      }


  }

  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyItemChanged(oldSelection)
    notifyItemChanged(rowSelected)
    oldSelection = rowSelected
  }


}