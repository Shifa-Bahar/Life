package com.lifepharmacy.application.ui.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ActivityPaymentWebViewBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.model.payment.Urls
import com.lifepharmacy.application.ui.payment.viewmodel.WebViewPaymentViewModel
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog.Companion.TAG
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.MyWebChromeClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class WebViewPaymentActivity : AppCompatActivity(), ClickTool {

  var paymentURL: String? = ""
  var sucessURL: String? = ""
  var failURL: String? = ""
  private val viewModel: WebViewPaymentViewModel by viewModels()

  lateinit var binding: ActivityPaymentWebViewBinding

  companion object {
    fun open(activity: Activity, urls: Urls) {
      val intent = Intent(activity, WebViewPaymentActivity::class.java)
      intent.putExtra("paymentURL", urls.paymentUrl)
      intent.putExtra("successURL", urls.successUrl)
      intent.putExtra("failURL", urls.failUrl)
      activity.startActivityForResult(intent, ConstantsUtil.PAYMENT_ACTIVITY_REQUEST_CODE)
//      activity.overridePendingTransition(R.anim.up_from_bottom, R.anim.fade_out)
    }
  }

  @SuppressLint("SourceLockedOrientationActivity")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_payment_web_view)
    AnalyticsUtil.setEvenWithNamePropertyValue(this, "ScreenOpened", "PaymentWebView")
    binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_web_view)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    val myWebView: WebView = findViewById(R.id.webview)
    val intent = intent
    paymentURL = intent.getStringExtra("paymentURL")
    failURL = intent.getStringExtra("failURL")
    sucessURL = intent.getStringExtra("successURL")

    viewModel.titleOfAppbar.value = "Online Payment"
    binding.include.click = this

    observers()
    paymentURL?.let {

      myWebView.settings.setAppCacheEnabled(false)
      val webSettings: WebSettings = myWebView.settings
      webSettings.javaScriptEnabled = true
      myWebView.setWebChromeClient(MyWebChromeClient(this))
      myWebView.loadUrl(it)
      myWebView.webViewClient = object : WebViewClient() {
        override fun doUpdateVisitedHistory(
          view: WebView?,
          url: String?,
          isReload: Boolean
        ) {
          when (url) {
            sucessURL -> {
              sendResultBack(1)
            }
            failURL -> {
              AlertManager.showErrorMessage(
                this@WebViewPaymentActivity,
                getString(R.string.payment_failed)
              )
              sendResultBack(2)
            }
          }
          super.doUpdateVisitedHistory(view, url, isReload)
        }
      }

    }

  }

  fun observers() {
    viewModel.titleOfAppbar.observe(this, Observer {
      binding.include.tvToolbarTitle.text = it
    })

  }

  fun sendResultBack(status: Int) {
    CoroutineScope(Dispatchers.Main.immediate).launch {
      delay(500)
      val returnIntent = Intent()
      returnIntent.putExtra("status", status)
      setResult(RESULT_OK, returnIntent)
      finish()
    }

  }

  override fun onBackPressed() {
    MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
      .setTitle(this.resources.getString(R.string.cancel_payment_title))
      .setMessage(this.resources.getString(R.string.cancel_payment_des))
      .setNegativeButton(this.resources.getString(R.string.dimiss)) { dialog, which ->
      }
      .setPositiveButton(this.resources.getString(R.string.cancel)) { dialog, which ->
        sendResultBack(2)
      }
      .show()

  }

  override fun onClickBack() {
    this.onBackPressed()
  }
}