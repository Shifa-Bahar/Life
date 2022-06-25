package com.lifepharmacy.application.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseActivity
import com.lifepharmacy.application.databinding.ActivityUpdateAppBinding
import com.lifepharmacy.application.model.BottomBarModel
import com.lifepharmacy.application.ui.dashboard.DashboardWithNativeBottomActivity
import com.lifepharmacy.application.utils.universal.ConnectionLiveData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateAppActivity : BaseActivity<ActivityUpdateAppBinding>() {
  companion object {
    fun open(activity: Activity) {
      val intent = Intent(activity, UpdateAppActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      activity.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding.btnUpdate.setOnClickListener {
      openLink("https://play.google.com/store/apps/details?id=com.lifepharmacy.application")
    }

  }

  override fun getLayoutRes(): Int {
    return R.layout.activity_update_app
  }

  override fun getLoadingLayout(): ConstraintLayout {
    return binding.llLoading.clLoading
  }

  override fun permissionGranted(requestCode: Int) {

  }

  private fun openLink(link: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    startActivity(browserIntent)
  }
}