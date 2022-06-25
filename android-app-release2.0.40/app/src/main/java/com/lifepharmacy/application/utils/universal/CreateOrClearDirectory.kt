package com.lifepharmacy.application.utils.universal

import android.content.Context
import android.graphics.*
import android.os.Environment
import java.io.*
import java.net.URL

/**
 * <h>CreateOrClearDirectory</h>
 *
 *
 * Singleton Class to create new directory or clear already
 * created directory
 *
 * @since 26/07/16.
 */
class CreateOrClearDirectory private constructor() {
    /** */
    /**
     * <h2>getAlbumStorageDir</h2>
     *
     *
     * custom method to create &/or clear directory
     *
     * @param mActivity: Calling activity reference
     * @param folderNameAndPath: name of folder with the path where to be created
     * @param isToClearDir: true if need to clear the directory
     * @return: return the created file
     */
    fun getAlbumStorageDir(
        mActivity: Context,
        folderNameAndPath: String,
        isToClearDir: Boolean
    ): File {
        val newDir: File
        val state = Environment.getExternalStorageState()
        newDir = if (Environment.MEDIA_MOUNTED == state) {
            File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/" + folderNameAndPath
            )
        } else {
            File(mActivity.filesDir.toString() + "/" + folderNameAndPath)
        }
        if (!newDir.isDirectory) {
            newDir.mkdirs()
        } else if (isToClearDir) {
            val newDirectory = newDir.listFiles()
            if (newDirectory.size > 0) {
                for (i in newDirectory.indices) {
                    newDirectory[i].delete()
                }
            } else {
            }
        } else {
        }
        return newDir
    }
    /** */
    /**
     * <h2>addBitmapToSdCardFromURL</h2>
     *
     *
     * method to download the user profile images from our server
     * and save it to local directory
     *
     * @param murl: url of image to be downloaded
     * @param mFile: downloaded image file
     */
    fun addBitmapToSdCardFromURL(murl: String?, mFile: File) {
        try {
            val url = URL(murl)
            val connection = url.openConnection()
            connection.connect()
            // this will be useful so that you can show a typical 0-100% progress bar

            // download the file
            val input: InputStream = BufferedInputStream(url.openStream())
            val output: OutputStream = FileOutputStream(mFile.absolutePath)
            val data = ByteArray(1024)
            var count: Int
            while (input.read(data).also { count = it } != -1) {
                output.write(data, 0, count)
            }
            output.flush()
            output.close()
            input.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /** */
    /**
     * <h2>getCircleCroppedBitmap</h2>
     *
     *
     * custom method to transform inage into circle
     *
     * @param bitmap image bitmap
     * @return bitmap of circular
     */
    fun getCircleCroppedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(),
            (bitmap.height / 2).toFloat(),
            (bitmap.width / 2).toFloat(),
            paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    /** */
    companion object {
        private var myImageHandler: CreateOrClearDirectory? = null
        /** */
        /**
         * <h2>getInstance</h2>
         *
         *
         * method to get single instance of this class
         *
         * @return: instance of this class
         */
        val instance: CreateOrClearDirectory?
            get() {
                if (myImageHandler == null) {
                    myImageHandler =
                      CreateOrClearDirectory()
                }
                return myImageHandler
            }
    }
}