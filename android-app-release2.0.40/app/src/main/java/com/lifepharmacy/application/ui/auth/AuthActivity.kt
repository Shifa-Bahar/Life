package com.lifepharmacy.application.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseActivity
import com.lifepharmacy.application.databinding.ActivityAuthBinding
import com.lifepharmacy.application.ui.auth.viewmodel.AuthViewModel
import com.lifepharmacy.application.utils.universal.ConnectionLiveData

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivityAuthBinding>() {
  private val viewModel: AuthViewModel by viewModels()

  companion object {
    fun open(activity: Activity) {
      val intent = Intent(activity, AuthActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      activity.startActivity(intent)
//            activity.overridePendingTransition(R.anim.up_from_bottom, R.anim.fade_out)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val connectionLiveData =
      ConnectionLiveData(this)
    connectionLiveData.observe(this, Observer { isConnected ->
      isConnected?.let {
        if (it) {
//          Toast.makeText(this, "Network Connected", Toast.LENGTH_SHORT).show()
        } else {
//          Toast.makeText(this, "Network Disconnected", Toast.LENGTH_SHORT).show()
        }
        // do job
      }
    })
//        var navController = Navigation.findNavController(this, R.id.fragment)
  }


  override fun getLayoutRes(): Int {

    return R.layout.activity_auth
  }


  override fun permissionGranted(requestCode: Int) {
  }


  override fun getLoadingLayout(): ConstraintLayout {
    return binding.llLoading.clLoading
  }
}