package com.lifepharmacy.application.utils

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import com.lifepharmacy.application.managers.MediaManager
import com.lifepharmacy.application.managers.MediaManagerKT
import com.lifepharmacy.application.model.ContactReturnModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * Created by Zahid Ali
 */
class IntentHandler(var context: Context, var fragment: Fragment, var mediaManager: MediaManager) {
  private var uriContact: Uri? = null

  fun handleGalleryIntent(data: Intent?): File? {
    var file: File? = null
    if (data != null) {
      val contentURI = data.data
//                        contentURI?.path?.let { it ->
      try {
        val newBitmap: Bitmap =
          mediaManager.handleSamplingAndRotationBitmap(
            context,
            contentURI
          )
        if (android.os.Build.VERSION.SDK_INT >= 28) {

          try {
            val uri = newBitmap?.let { MediaManagerKT.saveImage(it, "viame", context) }
            val string = MediaManagerKT.createCopyAndReturnRealPath(uri, context)
            Log.e("Test", "onActivityResult: $string")
            string?.let {
              file = File(it)
//              MediaManagerKT.startCropImage(file!!, context, fragment)
            }

          } catch (e: Exception) {
            e.printStackTrace()
          }


        } else {
          val fileUri = mediaManager.getImageUri(
            context,
            newBitmap
          )
          file = File(
            mediaManager.getRealPathFromURI(
              context,
              fileUri
            )
          )

          if (file!!.exists()) {
//            MediaManagerKT.startCropImage(file!!, context, fragment)

          }

        }

      } catch (e: java.lang.Exception) {
        e.printStackTrace()
      }

//                        }


    }
    return file
  }

  fun handleContactIntent(data: Intent?): ContactReturnModel? {
    var contactReturnModel: ContactReturnModel = ContactReturnModel(null, null)
    uriContact = data!!.data
    val c: Cursor? = uriContact?.let {
      context.contentResolver.query(
        it, null, null, null, null
      )
    }
    if (c != null) {
      if (c.moveToFirst()) {
        val name =
          c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
        contactReturnModel.name = name.toString()
        val hasPhone =
          c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
        val id: String =
          c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))
        if (hasPhone.equals("1", ignoreCase = true)) {
          try {

            val phones: Cursor? = context.contentResolver.query(
              ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
              null,
              ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
              null,
              null
            )
            phones?.moveToFirst()
            val cId: String = id
            var cNumber1: String? = "1"
            cNumber1 = phones?.getString(phones.getColumnIndex("data1"))
            cNumber1?.let { phone ->
              contactReturnModel.number = phone
            }
          } catch (e: java.lang.Exception) {
            e.printStackTrace()
          }

        }

      }
    }
    c?.close()
    return contactReturnModel
  }


}