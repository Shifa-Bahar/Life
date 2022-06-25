package com.lifepharmacy.application.ui.rewards.adapters


import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.DeliveryInstructionBinding
import com.lifepharmacy.application.databinding.ItemTimeBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.address.AddressMainModel
import com.lifepharmacy.application.model.cart.DeliveryInstructions
import com.lifepharmacy.application.model.pcr.pcrdatetime.Slots
import com.lifepharmacy.application.ui.cart.fragmets.ClickPaymentFragment
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.pcr.adapters.ClickPcrDate
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDouble
import kotlinx.android.synthetic.main.layout_rewards_front.view.*
import kotlin.math.roundToInt

class DeliveryInsAdapter(
  context: Activity?, private val onItemTapped: ClickPaymentFragment,
  private val appManager: AppManager
) :
  RecyclerView.Adapter<DeliveryInsAdapter.ItemViewHolder>() {
// var rewardMain: GeneralResponseModel<RewardModel>? = null

  //  var arrayList: ArrayList<Booking_slots>? = ArrayList()
  var arrayList: ArrayList<DeliveryInstructions>? = ArrayList()

  //  var arrayListslots: ArrayList<Slots>? = ArrayList()
  var activity: Activity? = context
  var id: Int = 0
  var extra: Int = 0
  var rowSelected = 0
  var oldSelection = 0
  var boolvalue = false
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: DeliveryInstructionBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.delivery_instruction,
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
    var binding: DeliveryInstructionBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(reward: ArrayList<DeliveryInstructions>) {
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
    holder.binding?.position = position
    holder.binding?.items = item.icon_unselected.toString()
    holder.binding?.itemselected = item.icon_selected.toString()

    holder.binding?.tvDelivery?.text = item.instruction.toString()
//    holder.binding?.isSelected = false
//    holder.binding?.isSelected = rowSelected == position

    holder.binding?.click = onItemTapped

    holder.binding?.clDelivery?.setOnClickListener {
      if (appManager.storageManagers.deliveryinsselected.isNullOrEmpty()) {

        holder.binding?.clDelivery?.setBackgroundResource(R.drawable.bg_square_blue_border_bluefill)
        holder.binding?.items = item.icon_selected.toString()
        holder.binding?.imgCross?.visibility = View.VISIBLE
        onItemTapped.onClickDeliveryIns(position, item, true)
        Log.e(TAG, "onBindViewHolder: 1")
//        holder.binding?.isSelected = true
      } else if (appManager.storageManagers.deliveryinsselected.contains(item)) {
        holder.binding?.clDelivery?.setBackgroundResource(R.drawable.bg_rect_no_border)
        holder.binding?.items = item.icon_unselected.toString()
        holder.binding?.imgCross?.visibility = View.GONE
        onItemTapped.onClickDeliveryIns(position, item, false)
//          holder.binding?.isSelected = false
      } else {
        Log.e(TAG, "onBindViewHoldereee:$item")
//        if (appManager.storageManagers.deliveryinsselected.contains(item.value == 2)) {
//            appManager.storageManagers.deliveryinsselected!!.forEachIndexed { index, element ->
//              // ...
//            }
//        if (item.value == 2) {

        val sameinstruction =
          appManager.storageManagers.deliveryinsselected?.firstOrNull { delvery -> delvery.value == item.value }
        if (sameinstruction != null) {
          appManager.storageManagers.deliveryinsselected.remove(sameinstruction)
        }
//          appManager.storageManagers.deliveryinsselected!!.forEach { delvery ->
//            if (delvery.value == 2) {
////              Log.e(TAG, "onBindViewHolderrrr:$index is $element ")
//              appManager.storageManagers.deliveryinsselected.remove(delvery)
////              notifyItemChanged(1)
//            }
//          }
//          for ((index, element) in appManager.storageManagers.deliveryinsselected.withIndex()) {
//            if (element.value == 2) {
//              Log.e(TAG, "onBindViewHolderrrr:$index is $element ")
//              appManager.storageManagers.deliveryinsselected.remove(element)
//            }
//          }

//        }

//            appManager.storageManagers.deliveryinsselected!!.forEach { delveryins ->
//              if (delveryins?.value == 2) {
//
//              }
//            }

//            appManager.storageManagers.deliveryinsselected.remove(item)
//            Log.e(TAG, "onBindViewHolder:$item")
//            onItemTapped.onClickDeliveryIns(position, item, false)


        holder.binding?.clDelivery?.setBackgroundResource(R.drawable.bg_square_blue_border_bluefill)
        holder.binding?.items = item.icon_selected.toString()
        holder.binding?.imgCross?.visibility = View.VISIBLE
        onItemTapped.onClickDeliveryIns(position, item, true)
//          holder.binding?.isSelected = true

      }

    }

    if (appManager.storageManagers.deliveryinsselected.contains(item)) {
      holder.binding?.clDelivery?.setBackgroundResource(R.drawable.bg_square_blue_border_bluefill)
      holder.binding?.items = item.icon_selected.toString()
      holder.binding?.imgCross?.visibility = View.VISIBLE
    } else {
      holder.binding?.clDelivery?.setBackgroundResource(R.drawable.bg_rect_no_border)
      holder.binding?.items = item.icon_unselected.toString()
      holder.binding?.imgCross?.visibility = View.GONE
    }
//    activity?.let {
//      holder.binding?.imgIcon?.let { it1 ->
//        Glide.with(it)
//          .load(item.icon_selected)
//          .override(25, 15)
//          .into(it1)
//      }
//    }
//    Glide.with(context).load(R.drawable.ic_card).override(25, 15).into(binding.imgType)
//      onItemTapped.onClickDeliveryIns(position, item, false)
//          holder.binding?.isSelected = false

//      if (rowSelected == position) {
//        holder.binding?.clDelivery?.setBackgroundResource(R.drawable.bg_square_blue_border_bluefill)
//        holder.binding?.items = item.icon_selected.toString()
//      } else {
//        holder.binding?.clDelivery?.setBackgroundResource(R.drawable.bg_rect_no_border)
//        holder.binding?.items = item.icon_unselected.toString()
//      }
//      if (rowSelected == position) {
//        if (boolvalue) {
//          boolvalue = false
//          holder.binding?.isSelected = boolvalue
//          onItemTapped.onClickDeliveryIns(position, item, false)
//        } else {
//          boolvalue = true
//          holder.binding?.isSelected = boolvalue
//        }
//      }

//      onItemTapped.onClickDeliveryIns(position, item)


  }

  //  fun setItemSelected(position: Int) {
//    rowSelected = position
//    notifyDataSetChanged()
//  }
  fun setItemSelected(position: Int) {
    Log.e(TAG, "setItemSelected: $position")
    rowSelected = position
//    notifyItemChanged(oldSelection)
//    notifyItemChanged(rowSelected)
    oldSelection = rowSelected
  }


}
