package com.lifepharmacy.application.ui.webActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ActivityWebViewBinding
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.IntentAction
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WebViewActivity : AppCompatActivity(), ClickTool {

  var url: String? = ""
  var title: String? = null
  private val viewModel: WebViewViewModel by viewModels()
  lateinit var binding: ActivityWebViewBinding

  companion object {
    fun open(activity: Activity, url: String, title: String? = null) {
      val intent = Intent(activity, WebViewActivity::class.java)
      intent.putExtra("url", url)
      intent.putExtra("title", title)
      activity.startActivityForResult(intent, ConstantsUtil.PAYMENT_ACTIVITY_REQUEST_CODE)
//      activity.overridePendingTransition(R.anim.up_from_bottom, R.anim.fade_out)
    }
  }

  @SuppressLint("SourceLockedOrientationActivity")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_web_view)
    AnalyticsUtil.setEvenWithNamePropertyValue(
      this,
      getString(R.string.analytic_screen), "WebViewActivity")
    binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    val myWebView: WebView = findViewById(R.id.webview)
    val intent = intent
    url = intent.getStringExtra("url")
    title = intent.getStringExtra("title")
    binding.click = this
    binding.include5.click = this
    binding.include5.tvToolbarTitle.text = title ?: getString(R.string.blog_title)
    url?.let {
      myWebView.settings.setAppCacheEnabled(false)
      val webSettings: WebSettings = myWebView.settings
      webSettings.javaScriptEnabled = true

      if (title == getString(R.string.invoice)) {
        viewModel.shareVisibility.value = false
        myWebView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$url")
      } else if(title == getString(R.string.report)) {
        viewModel.shareVisibility.value = true
        Log.e(TAG, "onCreate:url$url")
//        val pdf = "https://lifeadmin-app.s3.me-south-1.amazonaws.com/images/PtKEL2vryrfL655rcsR06gWnusUyhRZxc9T27m8W.pdf"
        val pdf = "$url"
        Log.e(TAG, "onCreate:pdf$pdf")
//        myWebView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")
        myWebView.loadUrl("https://docs.google.com/viewer?url=$pdf")

//        myWebView.loadUrl("https://www.google.co.in/")

//        viewModel.shareVisibility.value = false
//        myWebView.loadUrl("https://lifeadmin-app.s3.me-south-1.amazonaws.com/images/PtKEL2vryrfL655rcsR06gWnusUyhRZxc9T27m8W.pdf")
//        Log.e(TAG, "onCreate: report $url")
      } else if (title == getString(R.string.lifeurl)) {
        binding.include5.tvToolbarTitle.visibility = View.GONE
        viewModel.shareVisibility.value = false
        Log.e(TAG, "onCreate: life url $url")
        myWebView.loadUrl("$url")
      } else {
        viewModel.shareVisibility.value = true
        myWebView.loadUrl(it, viewModel.appManager.networkUtils.getHeadersInMap())
      }
//      https://drive.google.com/viewerng/viewer?embedded=true&url=

      myWebView.webViewClient = object : WebViewClient() {
        override fun doUpdateVisitedHistory(
          view: WebView?,
          url: String?,
          isReload: Boolean
        ) {
          super.doUpdateVisitedHistory(view, url, isReload)
        }
      }

    }
    binding.imgShare.setOnClickListener {
      myWebView.url?.let { it1 -> IntentAction.sendTextToOtherApps(this, it1) }
    }

  }


  override fun onClickBack() {
    this.onBackPressed()
  }
}