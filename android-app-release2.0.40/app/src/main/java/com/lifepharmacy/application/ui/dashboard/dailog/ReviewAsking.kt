package com.lifepharmacy.application.ui.dashboard.dailog

import android.app.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.lifepharmacy.application.R
import com.lifepharmacy.application.managers.PersistenceManager
import com.lifepharmacy.application.ui.dashboard.viewmodel.DashboardViewModel

class ReviewAsking(val context: Activity, val viewModel: DashboardViewModel) {

  var reviewManager: ReviewManager? = null
  var reviewInfo: ReviewInfo? = null

  init {
    getReviewInformation()
  }

  fun askForReview() {
    MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialInfoDialog)
      .setTitle(context.getString(R.string.review_app))
      .setMessage(context.getString(R.string.review_on_playstore))
      .setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
        viewModel.openReviewBox.value = false
      }
      .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
        viewModel.openReviewBox.value = false
        startReviewFlow()
      }
      .show()
  }

  private fun getReviewInformation() {
    reviewManager = ReviewManagerFactory.create(context)
    val request = (reviewManager ?: return).requestReviewFlow()
    request.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // We got the ReviewInfo object
        reviewInfo = task.result
      } else {
      }
    }

  }

  private fun startReviewFlow() {
    if (reviewInfo != null) {
      val flow: Task<Void> =
        (reviewManager ?: return).launchReviewFlow(context, reviewInfo ?: return)
      flow.addOnCompleteListener {
        viewModel.appManager.persistenceManager.setReviewBoxStatus(true)
      }
    } else {
//      Toast.makeText(applicationContext, "In App Rating failed", Toast.LENGTH_LONG).show();
    }

  }
}