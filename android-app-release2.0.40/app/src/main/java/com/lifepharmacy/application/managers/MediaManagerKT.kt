package com.lifepharmacy.application.managers

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.yalantis.ucrop.UCrop
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.create
import java.io.*


/**
 * Created by Zahid Ali
 */

class MediaManagerKT {
  companion object {
    @Nullable
    fun createCopyAndReturnRealPath(
      @NonNull uri: Uri?, context: Context
    ): String? {
      val contentResolver: ContentResolver = context.contentResolver ?: return null

      // Create file path inside app's data dir
      val filePath: String =
        (context.applicationInfo.dataDir.toString() + File.separator
            + System.currentTimeMillis())
      val file = File(filePath)
      try {
        val inputStream: InputStream =
          uri?.let { contentResolver.openInputStream(it) } ?: return null
        val outputStream: OutputStream = FileOutputStream(file)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()
      } catch (ignore: IOException) {
        return null
      }
      return file.absolutePath
    }

    /// @param folderName can be your app's name
    fun saveImage(bitmap: Bitmap, folderName: String, context: Context): Uri? {
//        if (android.os.Build.VERSION.SDK_INT >= 29) {
      val values = contentValues()
      values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName)
      values.put(MediaStore.Images.Media.IS_PENDING, true)
      // RELATIVE_PATH and IS_PENDING are introduced in API 29.

      var uri: Uri? = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
      )
      if (uri != null) {
        saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
        values.put(MediaStore.Images.Media.IS_PENDING, false)
        context.contentResolver.update(uri, values, null, null)
      } else {
        uri = getImageUri(
          context,
          bitmap
        )
      }
      return uri
    }

    private fun contentValues(): ContentValues {
      val values = ContentValues()
      values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
      values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
      values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
      return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
      if (outputStream != null) {
        try {
          bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
          outputStream.close()
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }

    private fun getImageUri(context: Context, inImage: Bitmap): Uri? {
      val bytes = ByteArrayOutputStream()
      inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
      val path =
        MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
      return Uri.parse(path)
    }

    fun startCropImage(newFile: File, context: Context, fragment: Fragment): File {
      var destinationCropImage: String
      try {
        destinationCropImage =
          ConstantsUtil.APP_NAME + "_crop" + System.currentTimeMillis() + ".png"
        UCrop.of(
          Uri.fromFile(newFile), Uri.fromFile(
            File(
              context.cacheDir, destinationCropImage
            )
          )
        ).withMaxResultSize(1080, 1080).start(context, fragment, ConstantsUtil.CROP_IMAGE)
      } catch (e: Exception) {
        e.printStackTrace()
      }
      return newFile
    }

    suspend fun getCompressedPhoto(file: File, context: Context): File {
      return Compressor.compress(context, file) {
        resolution(1080, 1080)
        quality(80)
        size(2_097_152)
      }
    }

    fun makeFilePart(file: File): MultipartBody.Part {

      val requestFile: RequestBody = create("multipart/form-data".toMediaTypeOrNull(), file)

// MultipartBody.Part is used to send also the actual file name


// MultipartBody.Part is used to send also the actual file name

//        val mediaType: MediaType? = "text/plain".toMediaTypeOrNull()
//        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
//          .addFormDataPart(
//            "file",
//            file.absolutePath,
//            create(
//              "application/octet-stream".toMediaTypeOrNull(),
//              file
//            )
//          )
//          .build()

      return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }
  }

}