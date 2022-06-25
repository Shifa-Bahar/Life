package com.lifepharmacy.application.ui.rewards.adapters


import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemTimeBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.address.AddressMainModel
import com.lifepharmacy.application.model.pcr.pcrdatetime.Slots
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.pcr.adapters.ClickPcrDate
import kotlinx.android.synthetic.main.layout_rewards_front.view.*

class PcrTimeAdapter(
  context: Activity?, private val onItemTapped: ClickPcrDate,
  private val appManager: AppManager
) :
  RecyclerView.Adapter<PcrTimeAdapter.ItemViewHolder>() {
// var rewardMain: GeneralResponseModel<RewardModel>? = null

  //  var arrayList: ArrayList<Booking_slots>? = ArrayList()
  var arrayList: ArrayList<Slots>? = ArrayList()

  //  var arrayListslots: ArrayList<Slots>? = ArrayList()
  var activity: Activity? = context
  var id: Int = 0
  var extra: Int = 0
  var rowSelected = 0
  var oldSelection = 0
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemTimeBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_time,
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
    var binding: ItemTimeBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(reward: ArrayList<Slots>) {
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

    holder.binding?.isSelected = rowSelected == position
    holder.binding?.tvTime?.text = item.time
    var value: Boolean
    value = item.additional_charges

    if (value) {
      holder.binding?.tvExtra?.visibility = View.VISIBLE
      extra = item.additional_charges_amount
      holder.binding?.tvExtra?.text = "NOW AED $extra EXTRA"
    } else {
      holder.binding?.tvExtra?.visibility = View.GONE
    }

    //    if (!id?.slots.isNullOrEmpty()) {
//      sectionAdapter.setDataChanged(mainModel.sections)
//      binding.rvSections.visibility = View.VISIBLE
//    } else {
//      binding.rvSections.visibility = View.GONE
//    }
    holder.binding?.click = onItemTapped
    holder.binding?.tvTime?.setOnClickListener {
      onItemTapped.onClickPcrTime(position, item)
      var addcharge = item.additional_charges_amount
      appManager.persistenceManager.additional_charges_amount = addcharge
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