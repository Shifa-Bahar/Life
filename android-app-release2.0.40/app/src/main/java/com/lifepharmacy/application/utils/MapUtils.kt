package com.lifepharmacy.application.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.lifepharmacy.application.utils.universal.GoogleUtils


object MapUtils {

  private const val LN2 = 0.6931471805599453
  private const val WORLD_PX_HEIGHT = 256
  private const val WORLD_PX_WIDTH = 256
  private const val ZOOM_MAX = 21
  fun getBoundsZoomLevel(bounds: LatLngBounds, mapWidthPx: Int, mapHeightPx: Int): Int {
    val ne = bounds.northeast
    val sw = bounds.southwest
    val latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI
    val lngDiff = ne.longitude - sw.longitude
    val lngFraction = (if (lngDiff < 0) lngDiff + 360 else lngDiff) / 360
    val latZoom = zoom(mapHeightPx, WORLD_PX_HEIGHT, latFraction)
    val lngZoom = zoom(mapWidthPx, WORLD_PX_WIDTH, lngFraction)
    val result = Math.min(latZoom.toInt(), lngZoom.toInt())
    return Math.min(result, ZOOM_MAX)
  }

  private fun latRad(lat: Double): Double {
    val sin = Math.sin(lat * Math.PI / 180)
    val radX2 = Math.log((1 + sin) / (1 - sin)) / 2
    return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2
  }

  private fun zoom(mapPx: Int, worldPx: Int, fraction: Double): Double {
    return Math.floor(Math.log(mapPx / worldPx / fraction) / LN2)
  }

  fun moveCameraToBoundLocation(
    activity: Activity?,
    pickupLatLng: LatLng?,
    dropOffLatLng: LatLng?,
    map: GoogleMap?,
  ) {
    map?.let { map1 ->
//      map1.addMarker(
//        pickupLatLng?.let {
//          val height = 100
//          val width = 70
//          val b = BitmapFactory.decodeResource(
//            activity?.resources,
//            com.lifepharmacy.application.R.drawable.pick_up_marker
//          )
//          val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
//          val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
//          MarkerOptions()
//            .position(it)
//            .icon(smallMarkerIcon)
////            .icon(BitmapDescriptorFactory.fromResource(com.lifepharmacy.application.R.drawable.pick_up_marker))
//        }
//      )
//      map1.addMarker(
//        dropOffLatLng?.let {
//          val height = 100
//          val width = 70
//          val b = BitmapFactory.decodeResource(
//            activity?.resources,
//            com.lifepharmacy.application.R.drawable.drop_off_marker
//          )
//          val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
//          val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
//          MarkerOptions()
//            .position(it)
//            .icon(smallMarkerIcon)
////            .icon(BitmapDescriptorFactory.fromResource(com.lifepharmacy.application.R.drawable.drop_off_marker))
//        }
//      )

      if (pickupLatLng != null) {
        if (dropOffLatLng != null) {

//          val bounds = LatLngBounds.builder()
//            .include(pickupLatLng)
//            .include(dropOffLatLng).build()
//
//          val cp = CameraPosition.Builder()
//            .bearing( GoogleUtils.getBearing(pickupLatLng,dropOffLatLng))
////            .tilt(randomTilt())
////            .target(latLng)
//            .zoom(
//              getBoundsZoomLevel(
//                bounds,
//                mapView.measuredWidth,
//                mapView.measuredHeight
//              ).toFloat()
//            )
//            .build()
//          val cu = CameraUpdateFactory.newCameraPosition(cp)
//          map.animateCamera(cu)


          ///OLDMETHOD
          val builder = LatLngBounds.Builder()
          map1.setPadding(180, 180, 180, 180)
          builder.include(pickupLatLng)
            .include(dropOffLatLng)

          val cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 0)

          map1.moveCamera(cameraUpdate)
          GoogleUtils.showCurvedPolyline(pickupLatLng, dropOffLatLng, 0.2, map1)

//          GoogleUtils.drawCurveOnMap(map1, pickupLatLng, dropOffLatLng)
        }
      }
    }
  }

  fun setPickUpMarker(activity: Activity?, pickupLatLng: LatLng?, map: GoogleMap?) {
    map?.let { map1 ->
      map1.addMarker(
        pickupLatLng?.let {
          val height = 100
          val width = 70
          val b = BitmapFactory.decodeResource(
            activity?.resources,
            com.lifepharmacy.application.R.drawable.pick_up_marker
          )
          val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
          val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
          MarkerOptions()
            .position(it)
            .icon(smallMarkerIcon)
//            .icon(BitmapDescriptorFactory.fromResource(com.lifepharmacy.application.R.drawable.pick_up_marker))
        }
      )
    }
  }

  fun setDropOffMarker(activity: Activity?, dropOffLatLng: LatLng?, map: GoogleMap?) {
    map?.let { map1 ->
      map1.addMarker(
        dropOffLatLng?.let {
          val height = 100
          val width = 70
          val b = BitmapFactory.decodeResource(
            activity?.resources,
            com.lifepharmacy.application.R.drawable.drop_off_marker
          )
          val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
          val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
          MarkerOptions()
            .position(it)
            .icon(smallMarkerIcon)
//            .icon(BitmapDescriptorFactory.fromResource(com.lifepharmacy.application.R.drawable.drop_off_marker))
        }
      )
    }
  }
}