package com.lifepharmacy.application.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.lifepharmacy.application.ui.utils.dailogs.ImageSelectBottomSheet
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

/**
 * Created by Zahid Ali
 */
class IntentStarter(var context: Context, var fragment: Fragment) {

  var isPictureBoxOpen: Boolean = false
  var photoFile: File? = null
  lateinit var currentPhotoPath: String


  fun openImageSelectionBottomSheet() {
    if (!isPictureBoxOpen) {
      var selectImage: ImageSelectBottomSheet = ImageSelectBottomSheet.newInstance(
        ImageSelectBottomSheet.TAG
      )
      selectImage.show(
        fragment.childFragmentManager,
        ImageSelectBottomSheet.TAG
      )
      isPictureBoxOpen = true
      selectImage.setImageSelectListener(object :
        ImageSelectBottomSheet.OnImageSourceSelected {
        override fun onGallerySelected() {
          selectImage.dismiss()
          isPictureBoxOpen = false
          val galleryIntent = Intent()
          galleryIntent.type = "image/*"
          galleryIntent.action = Intent.ACTION_GET_CONTENT;
          fragment.startActivityForResult(
            galleryIntent,
            ConstantsUtil.GALLERY_PIC
          )
        }

        override fun onCameraSelected() {
          selectImage.dismiss()
          isPictureBoxOpen = false
          dispatchTakePictureIntent()
        }

        override fun onDismissed() {
          isPictureBoxOpen = false
        }

      })
    }

  }

  //    fun openImagePDFBottomSheet() {
//
//        if (!isPictureBoxOpen) {
//            var selectImage: ImagePDFBottomSheet = ImagePDFBottomSheet.newInstance(
//                ImageSelectBottomSheet.TAG
//            )
//            selectImage.show(
//                fragment.childFragmentManager,
//                ImageSelectBottomSheet.TAG
//            )
//            isPictureBoxOpen = true
//            selectImage.setImageSelectListener(object :
//                ImagePDFBottomSheet.OnImageSourceSelected {
//                override fun onGallerySelected() {
//                    selectImage.dismiss()
//                    isPictureBoxOpen = false
//                    val galleryIntent = Intent()
//                    galleryIntent.type = "image/*"
//                    galleryIntent.action = Intent.ACTION_GET_CONTENT;
//                    fragment.startActivityForResult(
//                        galleryIntent,
//                        Constants.GALLERY_PIC
//                    )
//                }
//
//                override fun onCameraSelected() {
//                    selectImage.dismiss()
//                    isPictureBoxOpen = false
//                    dispatchTakePictureIntent()
//                }
//
//                override fun onCanceled() {
//                    isPictureBoxOpen = false
//                }
//
//                override fun onPDF() {
//                    selectImage.dismiss()
//                    isPictureBoxOpen = false
//                    selectCSVFile()
//                }
//
//            })
//        }
//
//    }
  @Throws(IOException::class)
  private fun createImageFile(): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
      "JPEG_${timeStamp}_", /* prefix */
      ".jpg", /* suffix */
      storageDir /* directory */
    ).apply {
      // Save a file: path for use with ACTION_VIEW intents
      currentPhotoPath = absolutePath
    }
  }

  private fun dispatchTakePictureIntent() {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
      // Ensure that there's a camera activity to handle the intent
      takePictureIntent.resolveActivity(context.packageManager)?.also {
        // Create the File where the photo should go
        photoFile = try {
          createImageFile()
        } catch (ex: IOException) {
          // Error occurred while creating the File
          null
        }
        // Continue only if the File was successfully created
        photoFile?.also {
          val photoURI: Uri = FileProvider.getUriForFile(
            context,
            "com.lifepharmacy.application.fileprovider",
            it
          )
          takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
          fragment.startActivityForResult(takePictureIntent, ConstantsUtil.CAMERA_PIC)
        }
      }
    }
  }

  private fun selectCSVFile() {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "application/pdf"
    val i = Intent.createChooser(intent, "File")
    fragment.startActivityForResult(i, ConstantsUtil.PDF_INTENT)
  }
}