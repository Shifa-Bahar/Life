package com.lifepharmacy.application.ui.address

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.model.address.AddressTypeModel
import java.util.*


/**
 * Created by Zahid Ali
 */
class AddressTypeSpinnerAdapter(val context: Context, var dataSource: ArrayList<AddressTypeModel>) :
  BaseAdapter() {

  private val inflater: LayoutInflater =
    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

    val view: View
    val vh: ItemHolder
    if (convertView == null) {
      view = inflater.inflate(R.layout.item_address_spinner, parent, false)
      vh = ItemHolder(view)
      view?.tag = vh
    } else {
      view = convertView
      vh = view.tag as ItemHolder
    }
    vh.label.text = dataSource[position].name

    return view
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
    val view: View
    val vh: ItemHolder
    if (convertView == null) {
      view = inflater.inflate(R.layout.item_address_spinner_drop_down, parent, false)
      vh = ItemHolder(view)
      view?.tag = vh
    } else {
      view = convertView
      vh = view.tag as ItemHolder
    }
    vh.label.text = dataSource[position].name

    return view
  }

  override fun getItem(position: Int): Any? {
    return dataSource[position]
  }

  override fun getCount(): Int {
    return dataSource.size
  }

  override fun getItemId(position: Int): Long {
    return position.toLong();
  }

  private class ItemHolder(row: View?) {
    val label: TextView = row?.findViewById(R.id.ed_type) as TextView

  }

}