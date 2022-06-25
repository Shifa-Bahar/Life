package com.lifepharmacy.application.managers

import android.app.Activity
import android.content.IntentSender
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.andrognito.flashbar.Flashbar
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog
import com.lifepharmacy.application.utils.universal.IntentAction

/**
 * Created by Zahid Ali
 */
class AlertManager {
  companion object {
    fun showErrorMessage(activity: Activity, message: String) {
//          var temActivity = activity as FragmentActivity
//          var fragmentManager = temActivity.supportFragmentManager
//          AnimationDialog.newInstance("").show(fragmentManager,"")
      Flashbar.Builder(activity)
        .gravity(Flashbar.Gravity.TOP)
        .message(message)
        .messageSizeInSp(16f)
        .backgroundColorRes(R.color.color_error)
        .duration(2000)
        .enableSwipeToDismiss()
        .build()
        .show()
    }

    fun showSuccessMessage(activity: Activity, message: String) {
      Flashbar.Builder(activity)
        .gravity(Flashbar.Gravity.TOP)
        .message(message)
        .messageSizeInSp(16f)
        .backgroundColorRes(R.color.color_2BA)
        .duration(1500)
        .enableSwipeToDismiss()
        .build()
        .show()
    }

    fun showInfoMessage(activity: Activity, title: String, message: String) {
      Flashbar.Builder(activity)
        .gravity(Flashbar.Gravity.TOP)
        .title(title)
        .titleColor(ContextCompat.getColor(activity, R.color.accent_blue_darker))
        .messageColor(ContextCompat.getColor(activity, R.color.accent_blue_darker))
        .message(message)
        .messageSizeInSp(16f)
        .backgroundColorRes(R.color.color_ca)
        .duration(3000)
        .enableSwipeToDismiss()
        .build()
        .show()
    }

    fun showInfoAlertDialog(activity: Activity, title: String, detail: String) {
      MaterialAlertDialogBuilder(
        activity,
        R.style.ThemeOverlay_App_MaterialInfoDialog
      )
        .setTitle(title)
        .setMessage(detail)
        .setPositiveButton(activity.getString(R.string.ok)) { dialog, which ->
        }
        .show()
    }

    fun permissionRequestPopup(activity: Activity) {
      MaterialAlertDialogBuilder(activity, R.style.ThemeOverlay_App_MaterialInfoDialog)
        .setTitle(activity.getString(R.string.dont_have_permission))
        .setMessage(activity.getString(R.string.open_perssion_setting))
        .setNegativeButton(activity.getString(R.string.cancel)) { dialog, which ->
        }
        .setPositiveButton(activity.getString(R.string.open)) { dialog, which ->
          IntentAction.openLocationSetting(activity)
        }
        .show()
    }

    fun promptLocationGPSPopup(activity: Activity) {
      val builder = LocationSettingsRequest.Builder()
      val client: SettingsClient = LocationServices.getSettingsClient(activity)
      val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
      task.addOnSuccessListener { locationSettingsResponse ->
        locationSettingsResponse.locationSettingsStates.isGpsPresent
        // All location settings are satisfied. The client can initialize
        // location requests here.
        // ...
      }
      task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
          // Location settings are not satisfied, but this can be fixed
          // by showing the user a dialog.
          try {
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
//            exception.startResolutionForResult(
//              activity,
//              REQUEST_CHECK_SETTINGS
//            )
          } catch (sendEx: IntentSender.SendIntentException) {
            // Ignore the error.
          }
        }
      }
    }
  }
}