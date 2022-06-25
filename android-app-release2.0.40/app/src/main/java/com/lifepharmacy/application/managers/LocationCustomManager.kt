package com.lifepharmacy.application.managers

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Address
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.lifepharmacy.application.utils.universal.GoogleUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Zahid Ali
 */
@SuppressLint("MissingPermission")
class LocationCustomManager constructor(var activity: Activity) {
  var googleUtils: GoogleUtils? = null
  lateinit var fusedLocationClient: FusedLocationProviderClient
  private var userLocation: Location? = null
  private var onLocationCall: CustomeLocationCallback? = null
  lateinit var mLocationRequest: LocationRequest
  private lateinit var locationCallBack: LocationCallback
  private var address: Address? = null
  private val INTERVAL = 1000 * 10 //15 sec
    .toLong()
  private val FASTEST_INTERVAL = 1000 * 10 // 15 sec
    .toLong()
  private val SMALLEST_DISPLACEMENT = 0.25f //quarter of a meter

  //    private val fusedLocationClient: FusedLocationProviderClient? = null
  init {
    googleUtils =
      GoogleUtils(activity.applicationContext)
    startLocationServices()

  }

  fun setUserLocation(location: Location?) {
    location?.let {
      userLocation = location
      onLocationCall?.onChange(location)
      CoroutineScope(Dispatchers.IO).launch {
        currentUserLatLong()?.let {
          address = googleUtils?.getLocationAddress(it)
          CoroutineScope(Dispatchers.Main.immediate).launch {
            onLocationCall?.address(address)
          }
        }
      }
    }
  }

  private fun createLocationRequest() {
    mLocationRequest = LocationRequest()
    mLocationRequest.interval = INTERVAL
    mLocationRequest.fastestInterval = FASTEST_INTERVAL
    mLocationRequest.smallestDisplacement = SMALLEST_DISPLACEMENT //added
    mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

  }

  fun startLocationServices() {
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    locationCallBack = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult?) {
        locationResult ?: return
        for (location in locationResult.locations) {
          setUserLocation(location)
        }
      }
    }

    createLocationRequest()
    fusedLocationClient.requestLocationUpdates(
      mLocationRequest,
      locationCallBack,
      Looper.getMainLooper() /* Looper */
    )
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
      location?.let { it: Location ->
        setUserLocation(it)
      } ?: kotlin.run {
      }
    }
  }

  fun getUserLocation(): Location? {
    return if (userLocation != null) {
      userLocation
    } else {
      null

    }
  }

  fun getFusedLocation(): FusedLocationProviderClient? {
    return fusedLocationClient
  }

  fun stopLocationServices() {
    fusedLocationClient.removeLocationUpdates(locationCallBack)
  }

  fun currentUserLatLong(): LatLng? {
    return userLocation?.latitude?.let {
      userLocation?.longitude?.let { it1 ->
        LatLng(
          it,
          it1
        )
      }
    }
  }

  interface CustomeLocationCallback {
    fun onChange(location: Location?)
    fun address(address: Address?)
  }

  fun setLocationCallbackListener(locationResult: CustomeLocationCallback) {
    onLocationCall = locationResult
  }


}