package com.lifepharmacy.application.ui.livechat

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ActivityLiveChatBinding
import com.lifepharmacy.application.managers.NotificationHelpManager
import com.lifepharmacy.application.ui.webActivity.WebViewViewModel
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.livechatinc.inappchat.ChatWindowErrorType
import com.livechatinc.inappchat.ChatWindowView
import com.livechatinc.inappchat.models.NewMessageModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LiveChatActivity : AppCompatActivity(), ChatWindowView.ChatWindowEventsListener {

  lateinit var binding: ActivityLiveChatBinding
  lateinit var chatWindowView: ChatWindowView
  lateinit var configuration: ChatWindowConfiguration

  private val viewModel: WebViewViewModel by viewModels()
  var orderId: String? = null
  var orderNumber: String? = null
  val NOTIFICATION_ID = 101

  var hashMap: HashMap<String, String?> = HashMap()

  val CHANNEL_ID = "personal_notification"

  var name: String = "";
  var email: String = "";
  var phone: String = "";

  companion object {
    fun open(
      activity: Activity,
      name: String,
      email: String,
      phone: String
    ) {
      val intent = Intent(activity, LiveChatActivity::class.java)
      intent.putExtra("name", name)
      intent.putExtra("email", email)
      intent.putExtra("phone", phone)
      activity.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_live_chat)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_live_chat)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    val intent = intent
    name = intent.getStringExtra("name").toString()
    email = intent.getStringExtra("email").toString()
    phone = intent.getStringExtra("phone").toString()
    viewModel.appManager.storageManagers.chatInitiatedMut.value = true
    initLayout()
  }

  fun initLayout() {
    hashMap = HashMap()
    hashMap["phone"] = phone
    configuration = ChatWindowConfiguration(
      ConstantsUtil.LIVE_CHAT_LICENSE,
      ConstantsUtil.CHAT_WINDOW_GROUP,
      name,
      email,
      hashMap
    )
    chatWindowView =
      ChatWindowView.createAndAttachChatWindowInstance(this@LiveChatActivity)
    chatWindowView.setUpWindow(configuration)
    chatWindowView.setUpListener(this)
    chatWindowView.initialize()
    chatWindowView.showChatWindow()
  }


  override fun onBackPressed() {
    if (!chatWindowView.onBackPressed()) super.onBackPressed()
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    chatWindowView.onActivityResult(requestCode, resultCode, data)
    super.onActivityResult(requestCode, resultCode, data)
  }

  override fun onChatWindowVisibilityChanged(visible: Boolean) {
    if (!visible) {
      super.onBackPressed()
    }

  }

  private fun discardBadge() {
    // badgeCounter = 0;
    // chatBadgeTv.setVisibility(GONE);
    // chatBadgeTv.setText("");
  }

  override fun onNewMessage(message: NewMessageModel?, windowVisible: Boolean) {
    if (message != null && !windowVisible) {
      NotificationHelpManager.notificationSimpleText(this@LiveChatActivity, message)
    }

  }

  override fun onStartFilePickerActivity(intent: Intent?, requestCode: Int) {
    startActivityForResult(intent, requestCode)
  }

  override fun onError(
    errorType: ChatWindowErrorType?,
    errorCode: Int,
    errorDescription: String?
  ): Boolean {
    if (errorType == ChatWindowErrorType.WebViewClient && errorCode == -2 && chatWindowView.isChatLoaded) {
      //Chat window can handle reconnection. You might want to delegate this to chat window
      return false;
    }
    Toast.makeText(this@LiveChatActivity, errorDescription, Toast.LENGTH_SHORT).show();
    return true;
  }

  override fun handleUri(uri: Uri?): Boolean {
    return false
  }


}