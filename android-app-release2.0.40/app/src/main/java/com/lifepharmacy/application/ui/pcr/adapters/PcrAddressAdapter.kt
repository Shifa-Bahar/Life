package com.lifepharmacy.application.ui.rewards.adapters


import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemPcrAddressBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.ui.pcr.adapters.ClickPcrDate

class PcrAddressAdapter(
  context: Activity?, private val onItemTapped: ClickPcrDate,
  private val appManager: AppManager
) :
  RecyclerView.Adapter<PcrAddressAdapter.ItemViewHolder>() {
// var rewardMain: GeneralResponseModel<RewardModel>? = null

  //  var arrayList: ArrayList<Addresses>? = ArrayList()
  var arrayList: ArrayList<AddressModel>? = ArrayList()
  var activity: Activity? = context
  var id: Int = 0
  var rowSelected = 0
  var oldSelection = 0
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemPcrAddressBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_pcr_address,
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
    var binding: ItemPcrAddressBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(reward: ArrayList<AddressModel>?) {
    arrayList?.clear()
    if (reward != null) {
      arrayList?.addAll(reward)
    }
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
//    var n = (arrayList!!.size)-1
//    rowSelected = n
//    val item = rewardMain?.data
    holder.binding?.item = item
    holder.binding?.position = position
    holder.binding?.tvPhone?.text = item.phone.toString()
    holder.binding?.isSelected = rowSelected == position
    appManager.persistenceManager.addressid = item.id
    appManager.persistenceManager.streetaddress = item.streetAddress.toString()
    appManager.persistenceManager.areaaddress = item.area.toString()
    appManager.persistenceManager.cityaddress = item.city.toString()
    appManager.persistenceManager.phoneaddress = item.phone.toString()
    if(!item.type.isNullOrBlank()){
      holder.binding?.tvAddressTitle?.text  = item.type
    }

    if(!item.type.isNullOrBlank()) {
      holder.binding?.click = onItemTapped
      holder.binding?.tvAddress?.text = "${item.streetAddress} + ${item.area} + ${item.city}"
      holder.binding?.tvPhone?.text = "${item.phone}"
    }
//    holder.binding?.tvAddress?.text = item.street_address
//    holder.binding?.tvPhone?.text = "99999999999"

    holder.binding?.cvAddress?.setOnClickListener {
      onItemTapped.onClickPcrAddress( position,item)
    }
//    holder.binding?.tvDay?.text = DateTimeUtil.getWeekStringDateFromStringWithoutTimeZone(item.date)
//    arrayListslots = item.slots
//   holder.binding?.tvMonth?.text = DateTimeUtil.getStringDateFromStringWithoutTimeZone(item.date)
//    holder.binding?.isSelected = rowSelected == position
//
//    holder.binding?.click = onItemTapped
//    holder.binding?.tvDay?.setOnClickListener {
//      onItemTapped.onClickPcrDateTime( position,item)
//    }
////    holder.binding?.tvMonth?.setOnClickListener {
////      onItemTapped.onClickPcrDateTime( position,item)
////    }
////    holder.binding?.isInCart = appManager.offersManagers.checkIfPcrProductAlreadyExistInCart(item)
////    holder.binding?.price?.countryCode
////    holder.binding?.tvTags?.text =
////      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
////        Html.fromHtml(item.short_description, Html.FROM_HTML_MODE_COMPACT)
////      } else {
////        Html.fromHtml(item.short_description)
////      }


  }

  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyItemChanged(oldSelection)
    notifyItemChanged(rowSelected)
    oldSelection = rowSelected
  }
  fun update(modelList:ArrayList<AddressModel>){
//    myList = modelList
//    myAdapter!!.notifyDataSetChanged()
    notifyDataSetChanged()
  }

}