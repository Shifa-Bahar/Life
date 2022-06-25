package com.lifepharmacy.application.utils.universal

import android.content.Context
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.lifepharmacy.application.managers.StorageManagers
import com.lifepharmacy.application.model.config.Polygon
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


class GoogleUtils @Inject constructor(private val context: Context) {
  fun getLocationAddress(location: LatLng): Address? {
    if (location != null) {
      val geocoder = Geocoder(context.applicationContext, Locale.getDefault())
      val result: String? = null
      try {
        val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (addressList != null && addressList.size > 0) {
          var address = addressList[0]
          if (address?.subLocality == null && address?.subAdminArea != null && Utils.isNumberString(
              address?.subAdminArea
            )
          ) {
            address?.subLocality = address?.subAdminArea
          } else if (address?.subLocality == null && address?.featureName != null && Utils.isNumberString(
              address?.featureName
            )
          ) {
            address?.subLocality = address?.featureName
          } else if (address?.subLocality == null && address?.subThoroughfare != null && Utils.isNumberString(
              address?.subThoroughfare
            )
          ) {
            address?.subLocality = address?.subThoroughfare
          } else if (address?.subLocality == null && address?.thoroughfare != null && Utils.isNumberString(
              address?.thoroughfare
            )
          ) {
            address?.subLocality = address?.thoroughfare
          }

          if (address?.adminArea == null && address?.locality != null) {
            address?.adminArea = address?.locality
          }
          val gson = Gson()
          val jsonStr = gson.toJson(address)
          Logger.d("Address", jsonStr)
          return address
        }
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
    return null
  }

  companion object {
    const val mapURL =
      "https://maps.googleapis.com/maps/api/staticmap?size=512x512&maptype=roadmap\\&key=AIzaSyAdYmC9csx2BVWSPbGe3NYxQAPOluaL7NY&markers=color:0x1D1D1D%7Clabel:P%7C25.185879800000000,55.281799799999995&markers=color:0x1D1D1D%7Clabel:D%7C25.190000000000000,55.280000000000000"

    fun decodePoly(encoded: String): List<LatLng> {
      val poly: MutableList<LatLng> = ArrayList()
      var index = 0
      val len = encoded.length
      var lat = 0.0
      var lng = 0.0
      while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
          b = encoded[index++].toInt() - 63
          result = result or (b and 0x1f shl shift)
          shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat
        shift = 0
        result = 0
        do {
          b = encoded[index++].toInt() - 63
          result = result or (b and 0x1f shl shift)
          shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng
        val p = LatLng(
          lat.toDouble() / 1E5,
          lng.toDouble() / 1E5
        )
        poly.add(p)
      }
      return poly
    }

    fun getBearing(begin: LatLng, end: LatLng): Float {
      val lat = Math.abs(begin.latitude - end.latitude)
      val lng = Math.abs(begin.longitude - end.longitude)
      if (begin.latitude < end.latitude && begin.longitude < end.longitude) return Math.toDegrees(
        Math.atan(lng / lat)
      )
        .toFloat() else if (begin.latitude >= end.latitude && begin.longitude < end.longitude) return (90 - Math.toDegrees(
        Math.atan(lng / lat)
      ) + 90).toFloat() else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude) return (Math.toDegrees(
        Math.atan(lng / lat)
      ) + 180).toFloat() else if (begin.latitude < end.latitude && begin.longitude >= end.longitude) return (90 - Math.toDegrees(
        Math.atan(lng / lat)
      ) + 270).toFloat()
      return (-1).toFloat()
    }

    fun getLatLongFromString(string: String?): LatLng? {
      var returnLatLng: LatLng? = null
      try {
        if (string != null) {
          returnLatLng =
            LatLng(
              string.substringBefore(",").stringToNullSafeDouble(),
              string.substringAfter(",").stringToNullSafeDouble()
            )
        }
      } catch (e: Exception) {

      }

      return returnLatLng
    }

    fun getStringFromLatLong(latLng: LatLng?): String? {
      var returnLatLng: String? = null
      try {
        if (latLng != null) {
          returnLatLng = latLng.latitude.toString() + "," + latLng.longitude.toString()

        }
      } catch (e: Exception) {

      }

      return returnLatLng
    }

    fun addressInLatLongStringWithComaSeparated(address: Address): String {
      return address.latitude.toString() + "," + address.longitude.toString()
    }

    fun getLatLongFromLocation(location: Location): LatLng {
      return LatLng(location.latitude, location.longitude)
    }

    fun distanceBetweenTwoLatLongs(latLng1: LatLng, latLng2: LatLng): Double {
      val lat1 = latLng1.latitude
      val lat2 = latLng1.longitude
      val lon1 = latLng2.latitude
      val lon2 = latLng2.longitude

      val theta = lon1 - lon2
      var dist = (sin(
        deg2rad(
          lat1
        )
      )
          * sin(
        deg2rad(
          lat2
        )
      )
          + (cos(
        deg2rad(
          lat1
        )
      )
          * cos(
        deg2rad(
          lat2
        )
      )
          * cos(
        deg2rad(
          theta
        )
      )))
      dist = acos(dist)
      dist =
        rad2deg(
          dist
        )
      dist *= 60 * 1.1515
      return dist
    }

    fun distanceBetweenTwoLatLongsValues(
      lat1: Double,
      lon1: Double,
      lat2: Double,
      lon2: Double
    ): Double {

      val theta = lon1 - lon2
      var dist = (sin(
        deg2rad(
          lat1
        )
      )
          * sin(
        deg2rad(
          lat2
        )
      )
          + (cos(
        deg2rad(
          lat1
        )
      )
          * cos(
        deg2rad(
          lat2
        )
      )
          * cos(
        deg2rad(
          theta
        )
      )))
      dist = acos(dist)
      dist =
        rad2deg(
          dist
        )
      dist *= 60 * 1.1515
      return dist
    }

    private fun deg2rad(deg: Double): Double {
      return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
      return rad * 180.0 / Math.PI
    }

    private fun isInGivenPolyGon(latLong: LatLng, list: ArrayList<LatLng>): Boolean {
      return PolyUtil.containsLocation(latLong, list, true)
    }

    fun isUserInGivePolyGoneList(polygon: ArrayList<Polygon>): Boolean {
      for (item in polygon) {
        if (isInGivenPolyGon(StorageManagers.userLatLng, item.getLaLngArrayList())) {
          return true
        }
      }
      return false
    }

    fun drawCurveOnMap(googleMap: GoogleMap, latLng1: LatLng, latLng2: LatLng) {

      //Adding marker is optional here, you can move out from here.
//    googleMap.addMarker(
//      MarkerOptions().position(latLng1).icon(BitmapDescriptorFactory.defaultMarker()))
//    googleMap.addMarker(
//      MarkerOptions().position(latLng2).icon(BitmapDescriptorFactory.defaultMarker()))

      val k = 0.2 //curve radius
      var h = SphericalUtil.computeHeading(latLng1, latLng2)
      var d = 0.0
      val p: LatLng?

      //The if..else block is for swapping the heading, offset and distance
      //to draw curve always in the upward direction
      if (h < 0) {
        d = SphericalUtil.computeDistanceBetween(latLng2, latLng1)
        h = SphericalUtil.computeHeading(latLng2, latLng1)
        //Midpoint position
        p = SphericalUtil.computeOffset(latLng2, d * 0.5, h)
      } else {
        d = SphericalUtil.computeDistanceBetween(latLng1, latLng2)

        //Midpoint position
        p = SphericalUtil.computeOffset(latLng1, d * 0.5, h)
      }

      //Apply some mathematics to calculate position of the circle center
      val x = (1 - k * k) * d * 0.5 / (2 * k)
      val r = (1 + k * k) * d * 0.5 / (2 * k)

      val c = SphericalUtil.computeOffset(p, x, h + 90.0)

      //Calculate heading between circle center and two points
      val h1 = SphericalUtil.computeHeading(c, latLng1)
      val h2 = SphericalUtil.computeHeading(c, latLng2)

      //Calculate positions of points on circle border and add them to polyline options
      val numberOfPoints = 1000 //more numberOfPoints more smooth curve you will get
      val step = (h2 - h1) / numberOfPoints

      //Create PolygonOptions object to draw on map
      val polygon = PolygonOptions()

      //Create a temporary list of LatLng to store the points that's being drawn on map for curve
      val temp = arrayListOf<LatLng>()

      //iterate the numberOfPoints and add the LatLng to PolygonOptions to draw curve
      //and save in temp list to add again reversely in PolygonOptions
      for (i in 0 until numberOfPoints) {
        val latlng = SphericalUtil.computeOffset(c, r, h1 + i * step)
        polygon.add(latlng) //Adding in PolygonOptions
        temp.add(latlng)    //Storing in temp list to add again in reverse order
      }

      //iterate the temp list in reverse order and add in PolygonOptions
      for (i in (temp.size - 1) downTo 1) {
        polygon.add(temp[i])
      }

      polygon.strokeColor(Color.BLUE)
      polygon.strokeWidth(5f)
      polygon.strokePattern(listOf(Dash(20f), Gap(20f))) //Skip if you want solid line
      googleMap.addPolygon(polygon)

      temp.clear() //clear the temp list
    }

    fun showCurvedPolyline(p1: LatLng, p2: LatLng, k: Double, googleMap: GoogleMap) {
      //Calculate distance and heading between two points
      val d = SphericalUtil.computeDistanceBetween(p1, p2)
      val h = SphericalUtil.computeHeading(p1, p2)

      //Midpoint position
      val p = SphericalUtil.computeOffset(p1, d * 0.5, h)

      //Apply some mathematics to calculate position of the circle center
      val x = (1 - k * k) * d * 0.5 / (2 * k)
      val r = (1 + k * k) * d * 0.5 / (2 * k)
      val c = SphericalUtil.computeOffset(p, x, h + 90.0)

      //Polyline options
      val options = PolylineOptions()
//      val pattern: List<PatternItem> = Arrays.<PatternItem> asList <PatternItem?>( Dash(30), Gap(20))

      val pattern: List<PatternItem> = Arrays.asList<PatternItem>(Dash(30F), Gap(20F))

      //Calculate heading between circle center and two points
      val h1 = SphericalUtil.computeHeading(c, p1)
      val h2 = SphericalUtil.computeHeading(c, p2)

      //Calculate positions of points on circle border and add them to polyline options
      val numpoints = 100
      val step = (h2 - h1) / numpoints
      for (i in 0 until numpoints) {
        val pi = SphericalUtil.computeOffset(c, r, h1 + i * step)
        options.add(pi)
      }

      //Draw polyline
      googleMap.addPolyline(
        options.width(10f).color(Color.BLUE).geodesic(false).pattern(pattern)
      )
    }

    private fun angleFromCoordinate(
      lat1: Double, long1: Double, lat2: Double,
      long2: Double
    ): Double {
      val dLon = long2 - long1
      val y = Math.sin(dLon) * Math.cos(lat2)
      val x = Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1)
          * Math.cos(lat2) * Math.cos(dLon))
      var brng = Math.atan2(y, x)
      brng = Math.toDegrees(brng)
      brng = (brng + 360) % 360
      brng = 360 - brng // count degrees counter-clockwise - remove to make clockwise
      return brng
    }

    fun staticMapWithPickAndDropOffUrl(
      startLatitude: Float,
      startLongitude: Float,
      endLatitude: Float,
      endLongitude: Float,
    ): String =
      "https://maps.googleapis.com/maps/api/staticmap?size=800x200&maptype=roadmap\\&key=${ConstantsUtil.GOOGLE_MAP_KEY}&markers=color:0x002579%7Clabel:P%7C$startLatitude,$startLongitude&markers=color:0x002579%7Clabel:D%7C${endLatitude},${endLongitude}"

    fun staticMapWithOneMarkerUrl(
      lat: Float,
      long: Float
    ): String =
      "https://maps.googleapis.com/maps/api/staticmap?size=800x200&maptype=roadmap\\&key=${ConstantsUtil.GOOGLE_MAP_KEY}&markers=color:0x002579%7Clabel:P%7C$lat,$long"
  }

}