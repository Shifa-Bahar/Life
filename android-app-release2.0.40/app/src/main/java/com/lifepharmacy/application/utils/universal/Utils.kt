package com.lifepharmacy.application.utils.universal

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.system.exitProcess


/**
 * Created by Zahid Ali
 */
class Utils {
  companion object {
    fun isNumberString(string: String?): Boolean {
      var returnFlag = false
      if (string != null) {
        try {
          val d: Double = string.stringToNullSafeDouble()
        } catch (nfe: NumberFormatException) {
          returnFlag = false
        }
        returnFlag = true
      }
      return returnFlag
    }

    fun formatNumberToSimple(string: String): String {
      return string.substringAfter("+").replace(" ", "")
    }

    fun fullNumberToSimple(string: String): String {
      return string.substring(3)
    }

    fun latLongFromStringComaSeparated(string: String): LatLng {
      return LatLng(
        string.substringBefore(",").stringToNullSafeDouble(),
        string.substringAfter(",").stringToNullSafeDouble()
      )
    }

    fun latFromStringComaSeparated(string: String): Double {
      return string.substringBefore(",").stringToNullSafeDouble()
    }

    fun LongFromStringComaSeparated(string: String): Double {
      return string.substringAfter(",").stringToNullSafeDouble()
    }

    fun vibrate(activity: Activity) {
      val vibrator: Vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        vibrator.vibrate(100)
      }
    }

    fun isNetworkAvailable(context: Context): Boolean {
      var cm: ConnectivityManager? =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      var isNetworkAvail = false
      try {
        if (cm != null) {
          val n: Network? = cm.activeNetwork
          if (n != null) {
            val nc = cm.getNetworkCapabilities(n)
            isNetworkAvail =
              nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
              )
            return isNetworkAvail
          }
        }
        return isNetworkAvail
      } catch (e: Exception) {
        e.printStackTrace()
      } finally {
        if (cm != null) {
          cm = null
        }
      }
      return false
    }

    fun getAppVersion(context: Context): String? {
      return try {
        val packageInfo = context.packageManager
          .getPackageInfo(context.packageName, 0)
        packageInfo.versionName
      } catch (e: PackageManager.NameNotFoundException) {
        throw RuntimeException("Could not get package name: $e")
      }
    }

    fun exitApp(activity: Activity) {
      MaterialAlertDialogBuilder(activity, R.style.ThemeOverlay_App_MaterialAlertDialog)
        .setTitle(activity.resources.getString(R.string.exit_app))
        .setMessage(activity.resources.getString(R.string.are_you_sure))
        .setNegativeButton(activity.resources.getString(R.string.dimiss)) { dialog, which ->
        }
        .setPositiveButton(activity.resources.getString(R.string.yes)) { dialog, which ->
          activity.finish();
          exitProcess(0);
        }
        .show()
    }

    fun getGenderKeyForSend(input: String): String {
      return when (input) {
        "Male" -> {
          "M"
        }
        "Female" -> {
          "F"
        }
        "Not Specified" -> {
          "O"
        }
        else -> {
          "M"
        }
      }
    }

    fun getDocTypeForSend(input: String): String {
      return when (input) {
        "Emirates ID Front" -> {
          "emirates_id_front"
        }
        "Emirates ID Back" -> {
          "emirates_id_back"
        }
        "Insurance Card Front" -> {
          "insurance_id_front"
        }
        "Insurance Card Back" -> {
          "insurance_id_back"
        }
        else -> {
          "emirates_id_front"
        }
      }
    }

    fun getDocTypeForShow(input: String): String {
      return when (input) {
        "emirates_id_front" -> {
          "Emirates ID Front"
        }
        "emirates_id_back" -> {
          "Emirates ID Back"
        }
        "insurance_id_front" -> {
          "Insurance Card Front"
        }
        "insurance_id_back" -> {
          "Insurance Card Back"
        }
        else -> {
          "emirates_id_front"
        }
      }
    }

    fun getRandomUUID(): String {
      val uuid: UUID = UUID.randomUUID()
      return uuid.toString()
    }

    fun getBlockNumber(blockSize: Double?, totalNumber: Double?): Int {
      var numberOfBlocks = 1
      if (totalNumber != null && blockSize != null) {
        var temp = totalNumber / blockSize
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.UP
        val multiplier = df.format(temp)

        numberOfBlocks = multiplier.toInt()
      }
      return numberOfBlocks
    }
  }
}