package com.lifepharmacy.application.broadCastReciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.SmsMessage

/**
 * <h2>BroadcastReceiver</h2>
 *
 *
 * subclass of BroadcastReceiver to received verification sms code
 * and handle it
 *
 * @since  4/4/17.
 */
abstract class ReadSms : BroadcastReceiver() {
  /* Service to auto matically read the SMS for verifying the account of the user */
//  val sms = SmsManager.getDefault()
  override fun onReceive(context: Context, intent: Intent) {

    // Retrieves a map of extended data from the intent.
    val bundle = intent.extras
    try {
      if (bundle != null) {
        val pdusObj =
          bundle["pdus"] as Array<Any>?
        for (i in pdusObj!!.indices) {
          val currentMessage =
            SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
          val phoneNumber = currentMessage.displayOriginatingAddress
          val senderNum = phoneNumber
          val message = currentMessage.displayMessageBody
          if (message != null) {
            //message = message.substring(length - 4, length);
//                        if(Constants.IS_DEBUG) Log.e("Message", "Hello" + message);
            onSmsReceived(message)
            abortBroadcast()
          }
        } // end for loop
      } // bundle is null
    } catch (e: Exception) {
//            if(Constants.IS_DEBUG) Log.e("SmsReceiver", "Exception smsReceiver" + e);
    }
  }

  protected abstract fun onSmsReceived(s: String?)
}