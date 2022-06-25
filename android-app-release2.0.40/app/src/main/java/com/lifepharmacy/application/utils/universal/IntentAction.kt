package com.lifepharmacy.application.utils.universal

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat.startActivity
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.AppManager
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.livechatinc.inappchat.ChatWindowView

object IntentAction {
  fun openWaze(activity: Activity, latitude: Double, longitude: Double) {
    try {
      activity.packageManager?.let {
        val url = "waze://?ll=$latitude,$longitude&navigate=yes"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.resolveActivity(it)?.let {
          activity.startActivity(intent)
        } ?: run {
          AlertManager.showErrorMessage(activity, "App not found")
        }
      }
    } catch (e: Exception) {

    }

  }

  fun callNumber(activity: Activity, number: String) {
    try {
      val dialIntent = Intent(
        Intent.ACTION_DIAL,
        Uri.parse("tel:00${number}")
      )
      activity.startActivity(dialIntent)
    } catch (e: Exception) {

    }

  }

  fun openNavigation(activity: Activity, latitude: String?, longitude: String?) {
    try {
      activity.packageManager?.let {
        val navigationIntentUri =
          Uri.parse("google.navigation:q=${latitude},${longitude}") //creating intent with latlng

        val mapIntent = Intent(Intent.ACTION_VIEW, navigationIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        mapIntent.resolveActivity(it)?.let {
          activity.startActivity(mapIntent)
        } ?: run {
          AlertManager.showErrorMessage(activity, "App not found")
        }
      }
    } catch (e: Exception) {

    }

  }

  fun openWhatsapp(activity: Activity, number: String) {
    val phone = "00:$number"
    val whatsappText = "Hello"
    val formattedNumber = number.replace("+", "").replace(" ", "")
    try {
      val waIntent = Intent("android.intent.action.MAIN")
      waIntent.setAction(Intent.ACTION_SEND)
      waIntent.type = "text/plain"
      waIntent.putExtra("jid", "$formattedNumber@s.whatsapp.net")
      waIntent.setPackage("com.whatsapp")
      waIntent.putExtra(Intent.EXTRA_TEXT, whatsappText)
      activity.startActivity(Intent.createChooser(waIntent, "Share with WhatsApp"))
    } catch (e: PackageManager.NameNotFoundException) {
      AlertManager.showErrorMessage(activity, "App not found")
    } catch (e: Exception) {
      AlertManager.showErrorMessage(activity, "App not found")
    }
  }

  fun sendTextToOtherApps(activity: Activity, text: String) {
    try {
      val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
      }
      val shareIntent = Intent.createChooser(sendIntent, null)
      activity.startActivity(shareIntent)
    } catch (e: Exception) {

    }

  }
//   fun openChatActivity(activity: Activity,appManager: AppManager){
//    val identity: Identity = AnonymousIdentity.Builder()
//      .withNameIdentifier(appManager.persistenceManager.getLoggedInUser()?.name) // name is optional
//      .withEmailIdentifier(appManager.persistenceManager.getLoggedInUser()?.email) // email is optional
//      .build()
//    Zendesk.INSTANCE.setIdentity(identity);
////    val answerBotEngine: Engine? = AnswerBotEngine.engine()
//    val supportEngine: Engine = SupportEngine.engine()
//    val chatEngine: Engine? = ChatEngine.engine()
//
//    MessagingActivity.builder()
//      .withEngines(chatEngine,supportEngine)
//      .show(activity)
//  }

  fun openLink(link: String, activity: Activity) {
    try {
      val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
      activity.startActivity(browserIntent)
    } catch (e: Exception) {

    }

  }

  fun openInsta(link: String, activity: Activity) {
    val uri = Uri.parse(link)
    val likeIng = Intent(Intent.ACTION_VIEW, uri)

    likeIng.setPackage("com.instagram.android")

    try {
      activity.startActivity(likeIng)
    } catch (e: ActivityNotFoundException) {
      activity.startActivity(
        Intent(
          Intent.ACTION_VIEW,
          Uri.parse(link)
        )
      )
    }
  }

  fun openFacebook(link: String, activity: Activity) {
    try {
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/426253597411506"))
      activity.startActivity(intent)
    } catch (e: java.lang.Exception) {
      activity.startActivity(
        Intent(
          Intent.ACTION_VIEW,
          Uri.parse(link)
        )
      )
    }
  }

  fun openLiveChat(
    appManager: AppManager,
    fullScreenChatWindow: ChatWindowView?,
    activity: Activity,
    listener: ChatWindowView.ChatWindowEventsListener
  ) {
    val customParamsMap = HashMap<String, String>()
    customParamsMap["phone"] = appManager.persistenceManager.getLoggedInUser()?.phone.toString()
    customParamsMap["source"] = "profile"
    val configuration = ChatWindowConfiguration(
      ConstantsUtil.LIVE_CHAT_LICENSE,
      "0",
      appManager.persistenceManager.getLoggedInUser()?.name,
      appManager.persistenceManager.getLoggedInUser()?.email,
      customParamsMap
    )
    var window = fullScreenChatWindow
    if (window == null) {
      window = ChatWindowView.createAndAttachChatWindowInstance(activity);
      window!!.setUpWindow(configuration);
      window.setUpListener(listener)
      window.initialize();
    }
    window.showChatWindow()
  }

  fun openLocationSetting(activity: Activity) {
    try {
      val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
      val uri = Uri.fromParts("package", activity.packageName, null)
      intent.data = uri
      activity.startActivity(intent)
    } catch (e: Exception) {

    }

  }

}

