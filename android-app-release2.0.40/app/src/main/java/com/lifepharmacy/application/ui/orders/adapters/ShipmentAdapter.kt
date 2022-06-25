package com.lifepharmacy.application.ui.orders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemOrderShipmentBinding
import com.lifepharmacy.application.model.orders.SubOrderDetail
import com.lifepharmacy.application.model.orders.SubOrderItem
import com.lifepharmacy.application.utils.MapUtils
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import com.lifepharmacy.application.utils.universal.GoogleUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ShipmentAdapter(
  var context: Activity,
  private val onItemTapped: ClickShipmentProduct,
  private val shipmentItem: ClickShipmentItem
) :
  RecyclerView.Adapter<ShipmentAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<SubOrderItem>? = ArrayList()
  var activity: Activity = context
  lateinit var orderProductsAdapter: ShipmentProductAdapter
  lateinit var shipmentStatusAdapter: ShipmentStatusAdapter

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemOrderShipmentBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      com.lifepharmacy.application.R.layout.item_order_shipment,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView), OnMapReadyCallback {
    var binding: ItemOrderShipmentBinding? = null
    var mapCurrent: GoogleMap? = null
    var map: MapView? = null

    init {
      binding = DataBindingUtil.bind(itemView)
      map = binding?.lyShipmentHeader?.mapView
      if (map != null) {
        map!!.onCreate(null)
        map!!.onResume()
        map!!.getMapAsync(this)
      }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
      MapsInitializer.initialize(itemView.context.applicationContext)
      mapCurrent = googleMap
      mapCurrent?.uiSettings?.setAllGesturesEnabled(false)
    }
  }

  fun setDataChanged(order: ArrayList<SubOrderItem>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }

  fun setOneItemDataChanged(order: SubOrderItem?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.add(order)
    }
    notifyDataSetChanged()
  }

  override fun onViewRecycled(holder: ItemViewHolder) {
//     Cleanup MapView here?
    if (holder.mapCurrent != null) {
      holder.mapCurrent?.clear()
      holder.mapCurrent?.mapType = GoogleMap.MAP_TYPE_NONE
    }
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    CoroutineScope(Dispatchers.Main.immediate).launch {
      delay(300)
      holder.mapCurrent?.let {
        val pickupLatLng = LatLng(
          item.shipment?.pickUpAddress?.latitude?.stringToNullSafeDouble() ?: 0.0,
          item.shipment?.pickUpAddress?.longitude?.stringToNullSafeDouble() ?: 0.0
        )
        val dropLatLng = LatLng(
          item.shipment?.dropOffAddress?.latitude?.stringToNullSafeDouble() ?: 0.0,
          item.shipment?.dropOffAddress?.longitude?.stringToNullSafeDouble() ?: 0.0
        )

        MapUtils.setPickUpMarker(activity, pickupLatLng, it)
        MapUtils.setDropOffMarker(activity, dropLatLng, it)
//        val currentPlace = CameraPosition.Builder()
//          .target(pickupLatLng)
//          .bearing(GoogleUtils.getBearing(pickupLatLng, dropLatLng) - 90).build()
////          .bearing(GoogleUtils.getBearing(pickupLatLng, dropLatLng)+90).tilt(65.5f).zoom(18f).build()
//        it.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace))
        MapUtils.moveCameraToBoundLocation(
          activity, pickupLatLng, dropLatLng, it,
        )

      }
    }
    holder.binding?.let { bind(it, item) }
  }

  fun bind(binding: ItemOrderShipmentBinding, item: SubOrderItem) {

    binding.lyShipmentHeader.isInstant = item.fulfilmentType.equals("instant")
    binding.item = item
    binding.lyShipmentHeader.item = item
    binding.lyShipmentHeader.click = shipmentItem

    binding.lyShipmentHeader.pickLatLong = LatLng(
      item.shipment?.pickUpAddress?.latitude?.stringToNullSafeDouble() ?: 0.0,
      item.shipment?.pickUpAddress?.longitude?.stringToNullSafeDouble() ?: 0.0
    )
    binding.lyShipmentHeader.dropLatLong = LatLng(
      item.shipment?.dropOffAddress?.latitude?.stringToNullSafeDouble() ?: 0.0,
      item.shipment?.dropOffAddress?.longitude?.stringToNullSafeDouble() ?: 0.0
    )
    binding.lyShipmentHeader.llShipmentMain.isOldType = item.timeLines.isNullOrEmpty()
    bindFirstTypeStatus(binding, item)
    bindShipmentStatus(binding, item)
    binding.lyItems.title = activity.getString(R.string.item_details)
    orderProductsAdapter =
      ShipmentProductAdapter(activity, onItemTapped, item.id ?: 0, item.status ?: 0)
    binding.lyItems.recyclerView.adapter = orderProductsAdapter
    orderProductsAdapter.setDataChanged(item.items)
//    binding.lyShipmentHeader.simpleRatingBar.isScrollable = false;

  }

  private fun bindFirstTypeStatus(binding: ItemOrderShipmentBinding, item: SubOrderItem) {
    binding.lyShipmentHeader.llShipmentMain.llFirstType.item = item
    if (item?.rating != null && item?.rating != 0.0F) {
      binding.lyShipmentHeader.llShipmentMain.llFirstType.simpleRatingBar.rating =
        item?.rating ?: 0.0F
      binding.lyShipmentHeader.llShipmentMain.llFirstType.simpleRatingBar.isClickable = false;
      binding.lyShipmentHeader.llShipmentMain.llFirstType.simpleRatingBar.isScrollable = false;
      binding.lyShipmentHeader.llShipmentMain.llFirstType.simpleRatingBar.isEnabled = false
    } else {
      binding.lyShipmentHeader.llShipmentMain.llFirstType.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
        shipmentItem.onClickShipmentRating(rating, item.id ?: 0, item.shipment?.id ?: 0)

      }
    }
  }

  private fun bindShipmentStatus(binding: ItemOrderShipmentBinding, item: SubOrderItem) {
    binding.lyShipmentHeader.llShipmentMain.item = item
    binding.lyShipmentHeader.llShipmentMain.llRating.item = item
    binding.lyShipmentHeader.llShipmentMain.llStatus.item = item
    binding.lyShipmentHeader.llShipmentMain.llStatus.click = shipmentItem

    if (item?.rating != null && item?.rating != 0.0F) {
      binding.lyShipmentHeader.llShipmentMain.llRating.simpleRatingBar.rating =
        item?.rating ?: 0.0F
      binding.lyShipmentHeader.llShipmentMain.llRating.simpleRatingBar.isClickable = false;
      binding.lyShipmentHeader.llShipmentMain.llRating.simpleRatingBar.isScrollable = false;
      binding.lyShipmentHeader.llShipmentMain.llRating.simpleRatingBar.isEnabled = false

//      binding.lyShipmentHeader.llShipmentMain.llRating.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
//        shipmentItem.onClickShipmentRating(rating, item.id ?: 0, item.shipment?.id ?: 0)
//
//      }
    } else {
      binding.lyShipmentHeader.llShipmentMain.llRating.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
        shipmentItem.onClickShipmentRating(rating, item.id ?: 0, item.shipment?.id ?: 0)

      }
    }
    shipmentStatusAdapter = ShipmentStatusAdapter(
      activity,
      subOrderItem = item,
      filter = true
    )
    binding.lyShipmentHeader.llShipmentMain.llStatus.rvStatus.adapter = shipmentStatusAdapter
    shipmentStatusAdapter.setDataChanged(item.timeLines)
  }

}
