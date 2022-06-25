package com.lifepharmacy.application.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import org.json.JSONException
import org.json.JSONObject
import com.yalantis.ucrop.UCrop
import java.io.*
import kotlin.jvm.Throws

/**
 * <h>HandlePictureEvents</h>
 * this class open the popup for the option to take the image
 * after it takes the the, it crops the image
 * and then upload it to amazon
 */
class HandlePictureEvents(mcontext: Activity?, fragment: Fragment?) {
    private var mcontext: Activity? = null
    private var takenNewImage: String? = null
    private var destinationCropImage: String? = null
    var newFile: File? = null
    var destinationFile: File? = null
    private var fragment: Fragment? = null
    /**
     * <h>openDialog</h>
     *
     *
     * this dialog have the option to choose whether to take picture
     * or open gallery or cancel the dialog
     *
     */
    //    public void openDialog()
    //    {
    //        try {
    //            takenNewImage = Constants.APP_NAME+"_"+System.nanoTime()+".png";
    //            CreateOrClearDirectory directory = CreateOrClearDirectory.getInstance();
    //
    //            File folder = new File(Environment.getExternalStorageDirectory()+File.separator+Constants.PARENT_FOLDER_PATH);
    //            if(!folder.exists())
    //                folder.mkdirs();
    //
    //            newFile = directory.getAlbumStorageDir(mcontext, Constants.PARENT_FOLDER_PATH,false);
    //            final Resources resources = mcontext.getResources();
    //
    //            ArrayList<String> options = new ArrayList<>();
    //            options.add(resources.getString(R.string.TakePhoto));
    //            options.add(resources.getString(R.string.ChoosefromGallery));
    //            options.add(resources.getString(R.string.action_cancel));
    //
    //            Alerts.showBottomSheetListDialog(mcontext, options,
    //                    resources.getString(R.string.AddPhoto), new Alerts.OnSearchableDialog() {
    //                @Override
    //                public void onItemSelected(Object o) {
    //                    String s = (String) o;
    //                    if (s.equals(resources.getString(R.string.TakePhoto)))
    //                    {
    //                        takePicFromCamera();
    //                    }
    //                    else if (s.equals(resources.getString(R.string.ChoosefromGallery))) {
    //                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
    //                        photoPickerIntent.setType("image/*");
    //                        if(fragment!=null)
    //                            fragment.startActivityForResult(photoPickerIntent, Constants.GALLERY_PIC);
    //                        else
    //                            mcontext.startActivityForResult(photoPickerIntent, Constants.GALLERY_PIC);
    //                    }
    //                    else if (s.equals(resources.getString(R.string.action_cancel))){
    //
    //                    }
    //                }
    //            });
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    /**
     * <h1>takePicFromCamera</h1>
     *
     *
     * This method is got called, when user chooses to take photos from camera.
     *
     */
    fun takePicFromCamera() {
        val state: String
        try {
            takenNewImage = ""
            state = Environment.getExternalStorageState()
            takenNewImage = ConstantsUtil.APP_NAME + "_" + System.nanoTime() + ".png"
            val folder = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + ConstantsUtil.PARENT_FOLDER_PATH
            )
            if (!folder.exists()) folder.mkdirs()
            val newProfileImageUri: Uri
            newFile = if (Environment.MEDIA_MOUNTED == state) File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + ConstantsUtil.PARENT_FOLDER_PATH + File.separator,
                takenNewImage
            ) else File(
                mcontext!!.filesDir.toString() + File.separator + ConstantsUtil.PARENT_FOLDER_PATH + File.separator,
                takenNewImage
            )
            newProfileImageUri =
                if (Build.VERSION.SDK_INT >= VERSION_CODES.N) FileProvider.getUriForFile(
                    mcontext!!, mcontext!!.applicationContext.packageName + ".provider", newFile!!
                ) else Uri.fromFile(newFile)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, newProfileImageUri)
            intent.putExtra("return-data", true)
            if (fragment != null) fragment!!.startActivityForResult(
                intent,
                ConstantsUtil.CAMERA_PIC
            ) else mcontext!!.startActivityForResult(intent, ConstantsUtil.CAMERA_PIC)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * <h2>startCropImage</h2>
     *
     *
     * This method got called when cropping starts done.
     *
     * @param newFile image file to be cropped
     */
    fun startCropImage(newFile: File): File {
        try {
            destinationCropImage =
                ConstantsUtil.APP_NAME + "_crop" + System.currentTimeMillis() + ".png"
            if (fragment != null) {
                UCrop.of(
                    Uri.fromFile(newFile), Uri.fromFile(
                        File(
                            mcontext!!.cacheDir, destinationCropImage
                        )
                    )
                )
                    .start(mcontext!!, fragment!!, ConstantsUtil.CROP_IMAGE)
            } else {
                UCrop.of(
                    Uri.fromFile(newFile), Uri.fromFile(
                        File(
                            mcontext!!.cacheDir, destinationCropImage
                        )
                    )
                )
                    .start(mcontext!!, ConstantsUtil.CROP_IMAGE)
            }

//            Timber.e("Input_Image "+Uri.fromFile(newFile)+" Exist "+newFile.exists()+ "\nDestination "+Uri.fromFile(new File(mcontext.getCacheDir(), destinationCropImage))
//                    +" Exist "+new File(mcontext.getCacheDir(), destinationCropImage).exists());
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return newFile
    }

    /**
     * <h2>gallery</h2>
     *
     *
     * This method is got called, when user chooses to take photos from camera.
     *
     * @param data uri data given by gallery
     */
    fun gallery(data: Uri?): File? {
        try {
            val state = Environment.getExternalStorageState()
            takenNewImage = ConstantsUtil.APP_NAME + System.nanoTime() + ".png"
            val folder = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + ConstantsUtil.PARENT_FOLDER_PATH
            )
            if (!folder.exists()) folder.mkdirs()
            newFile = if (Environment.MEDIA_MOUNTED == state) {
                File(
                    Environment.getExternalStorageDirectory()
                        .toString() + File.separator + ConstantsUtil.PARENT_FOLDER_PATH + File.separator,
                    takenNewImage
                )
            } else {
                File(
                    mcontext!!.filesDir.toString() + File.separator + ConstantsUtil.PARENT_FOLDER_PATH + File.separator,
                    takenNewImage
                )
            }
            val inputStream = mcontext!!.contentResolver.openInputStream(
                data!!
            )
            val fileOutputStream = FileOutputStream(newFile)
            copyStream(inputStream, fileOutputStream)
            fileOutputStream.close()
            inputStream!!.close()
            // newProfileImageUri = Uri.fromFile(newFile);
            startCropImage(newFile!!)
        } catch (e: Exception) {
            e.printStackTrace()
            newFile = File(
                mcontext!!.filesDir.toString() + File.separator + ConstantsUtil.PARENT_FOLDER_PATH + File.separator,
                takenNewImage
            )
            try {
                val inputStream = mcontext!!.contentResolver.openInputStream(
                    data!!
                )
                val fileOutputStream = FileOutputStream(newFile)
                copyStream(inputStream, fileOutputStream)
                fileOutputStream.close()
                inputStream!!.close()
                // newProfileImageUri = Uri.fromFile(newFile);
                startCropImage(newFile!!)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return newFile
    }

    fun saveBitmapToFile(bitmap: Bitmap?): File? {
        var newFile: File? = null
        if (bitmap != null) {
            try {
                val state = Environment.getExternalStorageState()
                val takenNewImage = ConstantsUtil.APP_NAME + System.nanoTime() + ".png"
                val folder = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + File.separator + ConstantsUtil.PARENT_FOLDER_PATH
                )
                if (!folder.exists()) folder.mkdirs()
                newFile = if (Environment.MEDIA_MOUNTED == state) {
                    File(
                        Environment.getExternalStorageDirectory()
                            .toString() + File.separator + ConstantsUtil.PARENT_FOLDER_PATH + File.separator,
                        takenNewImage
                    )
                } else {
                    File(
                        mcontext!!.filesDir.toString() + File.separator + ConstantsUtil.PARENT_FOLDER_PATH + File.separator,
                        takenNewImage
                    )
                }
                var outputStream: FileOutputStream? = null
                try {
                    outputStream =
                        FileOutputStream(newFile.absolutePath) //here is set your file path where you want to save or also here you can set file object directly
                    bitmap.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        outputStream
                    ) // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        outputStream?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    // Refresh External storage that file is in SDCARD
                    if (newFile != null && newFile.exists()) {
                        mcontext!!.sendBroadcast(
                            Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                Uri.fromFile(newFile)
                            )
                        )
                        MediaScannerConnection.scanFile(
                            mcontext,
                            arrayOf(newFile.toString()),
                            arrayOf(newFile.name),
                            null
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return newFile
    }

    /**
     * <h1>stringToJsonAndPublish</h1>
     *
     *
     * This method is used to convert our string into json file and then publish on amazon.
     *
     * @param fileName contains the name of file.
     * @param uri contains the uri.
     * @return the json object.
     */
    private fun stringToJsonAndPublish(fileName: String, uri: Uri): JSONObject {
        val message = JSONObject()
        try {
            message.put("type", "image")
            message.put("filename", fileName)
            message.put("uri", uri.toString())
            message.put("uploaded", "inprocess")
            message.put("confirm", false)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return message
    }

    companion object {
        @Throws(IOException::class)
        fun copyStream(input: InputStream?, output: OutputStream) {
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (input!!.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
        }
    }

    init {
        this.fragment = fragment
        this.mcontext = mcontext
    }
}