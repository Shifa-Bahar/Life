package com.lifepharmacy.application.base

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.ViewDataBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.lifepharmacy.application.managers.LocationCustomManager

import com.lifepharmacy.application.utils.universal.ConstantsUtil.DEFAULT_CAMERA_ZOOM
import com.lifepharmacy.application.utils.universal.GoogleUtils
import com.lifepharmacy.application.utils.PicassoMarker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseMapFragment<DB : ViewDataBinding> : BaseFragment<DB>(),
  LocationCustomManager.CustomeLocationCallback {


  private var pickUpMarker: Marker? = null
  private var dropUpMarker: Marker? = null
  private var driverMarker: Marker? = null
  private var picasoDrivermaker: PicassoMarker? = null

  private var stopMakers: ArrayList<Marker> = ArrayList<Marker>()


  private lateinit var locationCustomManager: LocationCustomManager


  var markerHeight = 100
  var markerWidth = 80

  var stopMakerHeight = 100
  var stopMakerWith = 80

  var baseMap: GoogleMap? = null
  private var userLocation: Location? = null
  var mapCenterMakerLocation: LatLng? = null

  var oldlatLng: LatLng? = null


  abstract fun onBaseActivityCreated(savedInstanceState: Bundle?)


//    //Getting map Layout Instance
//    abstract fun getMapLayout():Int

  protected abstract fun onMapMove(address: Address?)

  protected abstract fun onMapTouch()


  fun initBaseMap(map: GoogleMap?) {
    map?.let {
      baseMap = map
//      var address: Address? = null

//            val style = MapStyleOptions.loadRawResourceStyle(
//                requireContext(), R.raw.mapstyle_grayscale
//            )
//            baseMap?.setMapStyle(style)
      baseMap?.setOnCameraIdleListener {
        mapCenterMakerLocation =
          LatLng(map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
        CoroutineScope(Dispatchers.IO).launch {
//                    mapCenterMakerLocation?.let {
//                        address = viewModel.appManager.googleUtils?.getLocationAddress(it)
//                    }
//
//                    CoroutineScope(Dispatchers.Main.immediate).launch {
//                        onMapMove(address)
//                    }
        }
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
    if (locationCustomManager.currentUserLatLong() != null) {
      locationCustomManager.currentUserLatLong()?.let {
        moveCameraToLocation(it, false)
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
      locationCustomManager.getFusedLocation()?.lastLocation?.addOnSuccessListener { location: Location? ->
        location?.let {
          moveCameraToLocation(GoogleUtils.getLatLongFromLocation(location), false)
        } ?: kotlin.run {
        }
      }
    }

  }


  fun moveCameraToLocation(latLng: LatLng?, delay: Boolean) {
    if (delay) {
      CoroutineScope(Dispatchers.Main.immediate).launch {
        delay(300)
        baseMap?.let { map ->
          latLng?.let {
            map.setPadding(0, 0, 0, 0)
            val location =
              CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_CAMERA_ZOOM)
            map.moveCamera(location)
          }
        }
      }
    } else {
      baseMap?.let { map ->
        latLng?.let {
          map.setPadding(0, 0, 0, 0)
          val location = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_CAMERA_ZOOM)
          map.moveCamera(location)
        }
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


  protected open fun drawPolyline(result: List<List<HashMap<String?, String>>>) {
//        try {
//            // Simple two color line.
//            var points: ArrayList<LatLng?>? = null
//            var lineOptions: PolylineOptions? = null
//
//            // Traversing through all the routes
//            for (i in result.indices) {
//                points = ArrayList()
//                lineOptions = PolylineOptions()
//
//                // Fetching i-th route
//                val path =
//                    result[i]
//
//                // Fetching all the points in i-th route
//                for (j in path.indices) {
//                    val point = path[j]
//                    val lat = point["lat"]!!.toDouble()
//                    val lng = point["lng"]!!.toDouble()
//                    val position = LatLng(lat, lng)
//                    points.add(position)
//                }
//                with(listLatLng){
//                    addAll(points)
//                }
//            }
//            val greyOptions = PolylineOptions()
//            greyOptions.width(12f)
//            greyOptions.color(ContextCompat.getColor(activity!!, R.color.colorAccent))
//            greyOptions.startCap(SquareCap())
//            greyOptions.endCap(SquareCap())
//            greyOptions.geodesic(true)
//            greyOptions.jointType(JointType.ROUND)
//            greyPolyLine = mMap.addPolyline(greyOptions)
//            animatePolyLine()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//    open fun animatePolyLine() {
//        try {
//            animator = ValueAnimator.ofInt(0, 100)
//            animator?.duration = 3000
//            animator?.interpolator = LinearInterpolator()
//            animator?.addUpdateListener { animator ->
//                val latLngList =
//                    blackPolyLine!!.points
//                val initialPointSize = latLngList.size
//                val animatedValue = animator.animatedValue as Int
//                val newPoints = animatedValue * listLatLng.size / 100
//                if (initialPointSize < newPoints) {
//                    latLngList.addAll(listLatLng.subList(initialPointSize, newPoints))
//                    blackPolyLine!!.points = latLngList
//                }
//            }
//            animator?.addListener(polyLineAnimationListener)
//            animator?.start()
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//    }
//    var polyLineAnimationListener: Animator.AnimatorListener =
//        object : Animator.AnimatorListener {
//            override fun onAnimationStart(animator: Animator) {}
//            override fun onAnimationEnd(animator: Animator) {
//                try {
//                    val blackLatLng =
//                        blackPolyLine!!.points
//                    val greyLatLng =
//                        greyPolyLine!!.points
//                    greyLatLng.clear()
//                    greyLatLng.addAll(blackLatLng)
//                    blackLatLng.clear()
//                    blackPolyLine!!.points = blackLatLng
//                    greyPolyLine!!.points = greyLatLng
//                    blackPolyLine!!.zIndex = 2f
//                    animator.start()
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//            override fun onAnimationCancel(animator: Animator) {}
//            override fun onAnimationRepeat(animator: Animator) {}
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
    locationCustomManager.setLocationCallbackListener(this)
    onBaseActivityCreated(savedInstanceState)
  }

  override fun onChange(location: Location?) {

  }

  override fun address(address: Address?) {

  }
}