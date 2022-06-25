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
import com.lifepharmacy.application.databinding.ItemPcrListBinding
import com.lifepharmacy.application.databinding.ItemPcrPaymentListBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.pcr.pcrlist.Products
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.pcr.adapters.ClickPcrProduct

class PcrProductPaymentAdapter(
  context: Activity?, private val onItemTapped: ClickPcrProduct,
  private val appManager: AppManager
) :
  RecyclerView.Adapter<PcrProductPaymentAdapter.ItemViewHolder>() {
// var rewardMain: GeneralResponseModel<RewardModel>? = null

  var arrayList: ArrayList<Products>? = ArrayList()
  var activity: Activity? = context
  var id: Int = 0
  var rowSelected = 0
  var oldSelection = 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemPcrPaymentListBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_pcr_payment_list,
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
    var binding: ItemPcrPaymentListBinding? = DataBindingUtil.bind(itemView)
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
//    val item = rewardMain?.data
    holder.binding?.item = item
    holder.binding?.position = position
    holder.binding?.isSelected = rowSelected == position
    holder.binding?.click = onItemTapped
    holder.binding?.tvAed?.text = "AED"
    //price calculation regular with vat and offer checked
    var pricevalue: String
    var regprice = item.prices[0].price.regularPrice

    pricevalue = appManager.persistenceManager.calculatevaluesproduct(item, regprice)
    holder.binding?.tvOfferpr?.text = pricevalue
    if (item.offers != null && item.offers.type == "flat_percentage_discount") {
      holder.binding?.tvCur?.visibility = View.VISIBLE
    } else {
      holder.binding?.tvCur?.visibility = View.GONE
    }
//price calculation regular with vat
    var priceoffervalue: String
    priceoffervalue = appManager.persistenceManager.calculateregularwithvat(item, regprice)
    holder.binding?.tvCur?.text = priceoffervalue

//    holder.binding?.tvOfferpr?.text = item.prices[0].price.offerPrice.toString()
//    holder.binding?.tvCur?.text = item.prices[0].price.regularPrice.toString()
    holder.binding?.tvQty?.text = item.selected_qty.toString()
    holder.binding?.tvCur?.paintFlags =
      holder.binding?.tvCur?.paintFlags!!.or(Paint.STRIKE_THRU_TEXT_FLAG)
    holder.binding?.imgProduct?.setOnClickListener {
      onItemTapped.onPcrProductClicked(item, position)
    }
    holder.binding?.imgCart?.setOnClickListener {
      onItemTapped.onPcrAddClicked( position,item)
      holder.binding?.tvQty?.text = item.selected_qty.toString()
      holder.binding?.imgCart?.visibility = View.GONE
      holder.binding?.adddelbtn?.visibility = View.VISIBLE
    }

    holder.binding?.btnPlus?.setOnClickListener {
      onItemTapped.onPcrAddbtnClicked( position,item)
      holder.binding?.tvQty?.text = item.selected_qty.toString()
    }

    holder.binding?.btnMinus?.setOnClickListener {
      if (item.selected_qty == 1 || item.selected_qty == item.min_salable_qty) {
        holder.binding?.adddelbtn?.visibility = View.GONE
        holder.binding?.imgCart?.visibility = View.VISIBLE
        onItemTapped.onPcrMinusbtnClicked(position, item)
        holder.binding?.tvQty?.text = item.selected_qty.toString()
      } else {
        onItemTapped.onPcrMinusbtnClicked(position, item)
        holder.binding?.tvQty?.text = item.selected_qty.toString()
      }

    }

//    holder.binding?.clMain?.setOnClickListener {
//      onItemTapped.onClickPcrListSelected( position,item)
//    }
    holder.binding?.isInCart = appManager.offersManagers.checkIfPcrProductAlreadyExistInCart(item)
    holder.binding?.price?.countryCode
    holder.binding?.tvTags?.text =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(item.short_description, Html.FROM_HTML_MODE_COMPACT)
      } else {
        Html.fromHtml(item.short_description)
      }


  }
  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyItemChanged(oldSelection)
    notifyItemChanged(rowSelected)
    oldSelection = rowSelected
  }


}