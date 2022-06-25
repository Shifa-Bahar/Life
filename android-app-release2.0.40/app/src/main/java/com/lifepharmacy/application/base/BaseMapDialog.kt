package com.lifepharmacy.application.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.ViewDataBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.lifepharmacy.application.R
import com.lifepharmacy.application.managers.LocationCustomManager
import com.lifepharmacy.application.utils.PicassoMarker
import com.lifepharmacy.application.utils.universal.ConstantsUtil.DEFAULT_CAMERA_ZOOM
import com.lifepharmacy.application.utils.universal.GoogleUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseMapDialog<DB : ViewDataBinding> : BaseDialogFragment<DB>(),
  LocationCustomManager.CustomeLocationCallback {


  private var pickUpMarker: Marker? = null
  private var dropUpMarker: Marker? = null
  private var driverMarker: Marker? = null
  private var picasoDrivermaker: PicassoMarker? = null

  private var stopMakers: ArrayList<Marker> = ArrayList()
  var locationCustomManager: LocationCustomManager? = null

  var markerHeight = 100
  var markerWidth = 80

  var stopMakerHeight = 100
  var stopMakerWith = 80

  var baseMap: GoogleMap? = null
  private var userLocation: Location? = null
  var mapCenterMakerLocation: LatLng? = null

  var oldlatLng: LatLng? = null


  protected abstract fun onMapMove(address: Address?)

  protected abstract fun onMapTouch()
  protected abstract fun onMapMoveStarted()

  protected abstract fun getGoogleUtils(): GoogleUtils?


  fun initBaseMap(map: GoogleMap?) {
    map?.let {
      baseMap = map
      var address: Address? = null


      val style = MapStyleOptions.loadRawResourceStyle(
        requireContext(), R.raw.map_style
      )
      baseMap?.setMapStyle(style)
      baseMap?.setOnCameraIdleListener {
        mapCenterMakerLocation =
          LatLng(map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
        CoroutineScope(Dispatchers.IO).launch {
          mapCenterMakerLocation?.let {
            address = getGoogleUtils()?.getLocationAddress(it)
          }

          CoroutineScope(Dispatchers.Main.immediate).launch {

            onMapMove(address)
          }
        }
      }
      baseMap?.setOnCameraMoveStartedListener {
        onMapMoveStarted()
      }
      baseMap?.setOnMapClickListener() {
        onMapTouch()
      }
      if (ActivityCompat.checkSelfPermission(
          requireContext(),
          Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
          requireContext(),
          Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
      ) {

        return
      }
      baseMap?.isMyLocationEnabled = true
      baseMap?.uiSettings?.isMyLocationButtonEnabled = false
      baseMap?.uiSettings?.isCompassEnabled = false

    }
  }


  fun goToCurrentLocation() {

    if (locationCustomManager?.currentUserLatLong() != null) {
      locationCustomManager?.currentUserLatLong()?.let {
        moveCameraToLocation(it)
      }
    } else {
      if (ActivityCompat.checkSelfPermission(
          requireContext(),
          Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
          requireContext(),
          Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
      locationCustomManager?.getFusedLocation()?.lastLocation?.addOnSuccessListener { location: Location? ->
        location?.let {
          moveCameraToLocation(GoogleUtils.getLatLongFromLocation(location))
        } ?: kotlin.run {
        }
      }
    }


  }

  fun getCurrentLatLong(): LatLng? {
    var currentLatLng: LatLng? = null
    if (getUserLocation() != null) {
      getUserLocation()?.let {
        currentLatLng = LatLng(it.latitude, it.longitude)

      }
    }
    return currentLatLng
  }

  @SuppressLint("MissingPermission")
  fun getUserLocation(): Location? {
    return if (userLocation != null) {
      userLocation
    } else {
      null

    }
  }

  fun setUserLocation(location: Location?) {
    location?.let {
      userLocation = location
    }
  }

  fun moveCameraToLocation(latLng: LatLng?) {

    baseMap?.let { map ->
      latLng?.let {
        map.setPadding(0, 0, 0, 0)
        val location = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_CAMERA_ZOOM)
        map.moveCamera(location)
      }
    }


  }

  fun moveCameraToBoundLocation(pickupLatLng: LatLng?, dropOffLatLng: LatLng?, delay: Boolean) {
    if (delay) {
      CoroutineScope(Dispatchers.Main.immediate).launch {
        delay(300)
        baseMap?.let { map ->
          val builder = LatLngBounds.Builder()
          map.setPadding(200, 700, 200, 200)
//            val cameraBounds = LatLngBounds(pickupLatLng, dropOffLatLng)
          builder.include(pickupLatLng)
            .include(dropOffLatLng)
          val cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 0)

          map.moveCamera(cameraUpdate)
        }
      }
    } else {
      baseMap?.let { map ->
        val builder = LatLngBounds.Builder()
        map.setPadding(200, 700, 200, 200)
//            val cameraBounds = LatLngBounds(pickupLatLng, dropOffLatLng)
        builder.include(pickupLatLng)
          .include(dropOffLatLng)
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 0)

        map.moveCamera(cameraUpdate)
      }
    }

  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    if (ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      return
    }
    locationCustomManager = LocationCustomManager(requireActivity())
    locationCustomManager?.setLocationCallbackListener(this)

  }

  override fun onChange(location: Location?) {

  }

  override fun address(address: Address?) {

  }

  override fun onDestroy() {
    locationCustomManager?.stopLocationServices()
    super.onDestroy()
  }
}