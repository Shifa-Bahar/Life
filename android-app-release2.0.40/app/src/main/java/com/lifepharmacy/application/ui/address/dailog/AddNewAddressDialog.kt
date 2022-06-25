package com.lifepharmacy.application.ui.address.dailog

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.IntentSender.SendIntentException
import android.location.Address
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseMapDialog
import com.lifepharmacy.application.databinding.DailogNewAddressBinding
import com.lifepharmacy.application.managers.analytics.newAddressScreenOpen
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.address.ClickNewAddressDialog
import com.lifepharmacy.application.ui.address.adapters.AddressAdapter
import com.lifepharmacy.application.ui.address.adapters.ClickItemNearByAddress
import com.lifepharmacy.application.ui.address.adapters.NearByAddressAdapter
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.GoogleUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlin.Result


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class AddNewAddressDialog : BaseMapDialog<DailogNewAddressBinding>(), OnMapReadyCallback,
  ClickNewAddressDialog, ClickItemNearByAddress, ClickTool {
  private val viewModel: AddressViewModel by activityViewModels()
  private val AUTOCOMPLETE_REQUEST_CODE = 1
  var placesClient: PlacesClient? = null
  private var searchedName: String? = null
  private var isAlreadySetToPrevious = false
  private val REQUEST_CHECK_SETTINGS = 0x1

  lateinit var addressAdapter: NearByAddressAdapter
  private var inView = false
  private val requestLocationPermission =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
      var result = true
      granted.entries.forEach {
        if (!it.value) {
          result = false
          return@forEach
        }
      }
      if (result) {
        displayLocationSettingsRequest()
      }

    }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    super.onViewCreated(view, savedInstanceState)
    viewModel.appManager.analyticsManagers.newAddressScreenOpen()
    binding.mapView.onCreate(savedInstanceState)


    binding.mapView.onResume() // needed to get the map to display immediately
    try {
      MapsInitializer.initialize(requireActivity().applicationContext)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    binding.mapView.getMapAsync(this)
    initUI()
    observers()
    requestLocationPermission.launch(ConstantsUtil.RequiredPermissionsLocations)
    inView = true
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  private fun initUI() {
    binding.tollBar.tvToolbarTitle.text = getString(R.string.add_a_new_address)
    binding.tollBar.click = this
    binding.click = this
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.address = viewModel.currentAddress
    initNearByAddressRV()

  }

  private fun initNearByAddressRV() {
    addressAdapter = NearByAddressAdapter(requireActivity(), this)
    binding.rvNearBy.adapter = addressAdapter
  }


  private fun observers() {
    viewModel.nearByPlaces.observe(viewLifecycleOwner, {
      it?.let {
        addressAdapter.setDataChanged(it)
      }
    })
  }

  private fun getNearByAddress() {
    viewModel.getNewByPlaces().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          com.lifepharmacy.application.network.Result.Status.SUCCESS -> {
            it.data?.results?.let { it1 -> viewModel.setNearByPlaceArray(it1) }
          }
          com.lifepharmacy.application.network.Result.Status.ERROR -> {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
          }
          com.lifepharmacy.application.network.Result.Status.LOADING -> {
          }

        }
      }
    })
  }


  private fun openGooglePlacesBox() {
    val fields =
      listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
      .build(requireContext())
    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    Places.initialize(requireContext().applicationContext, getString(R.string.google_Api_Key))
    placesClient = Places.createClient(requireContext())
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
      when (resultCode) {
        Activity.RESULT_OK -> {
          data?.let {
            val place = Autocomplete.getPlaceFromIntent(data)
            place.name?.let {
              searchedName = it
            }
            moveCameraToLocation(place.latLng)
          }
        }
        AutocompleteActivity.RESULT_ERROR -> {
          data?.let {
//                        val status = Autocomplete.getStatusFromIntent(data)

          }
        }
        Activity.RESULT_CANCELED -> {
          // The user canceled the operation.
        }
      }
      return
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  override fun getLayoutRes(): Int {
    return R.layout.dailog_new_address
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    inView = false
  }

  override fun onCancel(dialog: DialogInterface) {
    super.onCancel(dialog)
    inView = false
  }

  override fun onDestroy() {
    super.onDestroy()
    inView = false
  }

  override fun onConfirm() {
    viewModel.latLng = mapCenterMakerLocation
    if (viewModel.latLng?.latitude != null && viewModel.latLng?.latitude != 0.0
      && viewModel.latLng?.longitude != null && viewModel.latLng?.longitude != 0.0
    ) {
      findNavController().navigate(R.id.addressDetailBottomSheet)
    } else {
      requestLocationPermission.launch(ConstantsUtil.RequiredPermissionsLocations)
    }

  }

  override fun onClickCurrent() {
    displayLocationSettingsRequest()

  }

  override fun onClickSearch() {
    openGooglePlacesBox()
  }

  override fun onClickBack() {
    inView = false
    findNavController().navigateUp()
  }

  override fun onMapMove(address: Address?) {
    viewModel.isMapMoving.value = false
    searchedName?.let {
      address?.subLocality = it
      searchedName = null
    }
    if (viewModel.isEdit) {
      if (!isAlreadySetToPrevious) {
        isAlreadySetToPrevious = true
        viewModel.currentAddress.set(address)
        viewModel.setAddress(viewModel.editAddress)
        moveCameraToLocation(
          LatLng(
            viewModel.currentAddress.get()?.latitude ?: 0.0,
            viewModel.currentAddress.get()?.longitude ?: 0.0
          )
        )

      } else {
        viewModel.currentAddress.set(address)
      }
    } else {
      viewModel.currentAddress.set(address)
    }
//    if (!viewModel.isNearByClicked && inView) {
//      try {
//        getNearByAddress()
//      } catch (e: Exception) {
//        e.printStackTrace()
//      }
//    } else {
//      viewModel.isNearByClicked = false
//    }

  }

  override fun onMapTouch() {

  }


  override fun onMapReady(map: GoogleMap?) {
    map?.let {
      baseMap = it
      initBaseMap(map)
      displayLocationSettingsRequest()
    }
  }

  override fun getGoogleUtils(): GoogleUtils? {
    return viewModel.appManager.googleUtils
  }

  override fun onMapMoveStarted() {
    viewModel.isMapMoving.value = true
  }

//  fun checkLocationServices() {
//
//
//    val builder = LocationSettingsRequest.Builder()
//    val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
//    val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//    task.addOnSuccessListener { locationSettingsResponse ->
//      if (locationSettingsResponse.locationSettingsStates.isGpsPresent && locationSettingsResponse.locationSettingsStates.isLocationPresent) {
//        goToCurrentLocation()
//      }
//      // All location settings are satisfied. The client can initialize
//      // location requests here.
//      // ...
//    }
//    task.addOnFailureListener { exception ->
//      if (exception is ResolvableApiException) {
//        // Location settings are not satisfied, but this can be fixed
//        // by showing the user a dialog.
//        try {
//          // Show the dialog by calling startResolutionForResult(),
//          // and check the result in onActivityResult().
////            exception.startResolutionForResult(
////              activity,
////              REQUEST_CHECK_SETTINGS
////            )\
//          exception.startResolutionForResult(
//            requireActivity(),
//            REQUEST_CHECK_SETTINGS
//          )
//        } catch (sendEx: IntentSender.SendIntentException) {
//          // Ignore the error.
//        }
//      }
//    }
//
////    val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
////    var gps_enabled = false
////    var network_enabled = false
////
////    try {
////      gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
////    } catch (ex: java.lang.Exception) {
////    }
////
////    try {
////      network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
////    } catch (ex: java.lang.Exception) {
////    }
////
////    if (!gps_enabled && !network_enabled) {
////      // notify user
////      MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialInfoDialog)
////        .setTitle(getString(R.string.gps_disabled))
////        .setMessage(getString(R.string.open_setting))
////        .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
////        }
////        .setPositiveButton(getString(R.string.open)) { dialog, which ->
////          startActivity(
////            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
////          )
////        }
////        .show()
////    } else {
////      goToCurrentLocation()
////    }
//
//  }

  private fun displayLocationSettingsRequest() {
    val googleApiClient = GoogleApiClient.Builder(requireContext())
      .addApi(LocationServices.API).build()
    googleApiClient.connect()
    val locationRequest = LocationRequest.create()
    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    locationRequest.interval = 10000
    locationRequest.fastestInterval = (10000 / 2).toLong()
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    builder.setAlwaysShow(true)
    val result =
      LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
    result.setResultCallback { result ->
      val status = result.status
      when (status.statusCode) {
        LocationSettingsStatusCodes.SUCCESS -> {
          goToCurrentLocation()

        }
        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
          try {
            // Show the dialog by calling startResolutionForResult(), and check the result
            // in onActivityResult().
            status.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
          } catch (e: SendIntentException) {
          }
        }
        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

        }
      }
    }
  }

  override fun onClickNearBy(
    address: com.lifepharmacy.application.model.googleplaces.Result,
    position: Int?
  ) {
    baseMap?.let {
      viewModel.isNearByClicked = true
      moveCameraToLocation(address.geometry?.getLatLng())
      if (position != null) {
        addressAdapter.selectedItem(position)
      }
    }
  }

}
