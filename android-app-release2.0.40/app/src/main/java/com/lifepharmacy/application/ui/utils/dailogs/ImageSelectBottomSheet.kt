package com.lifepharmacy.application.ui.utils.dailogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.databinding.BottomImageSelectBinding

/**
 * Created by Zahid Ali
 */
class ImageSelectBottomSheet : BaseBottomUpSheet<BottomImageSelectBinding>() {

  var onImageSourceSelected: ImageSelectBottomSheet.OnImageSourceSelected? = null

  companion object {
    const val TAG = "ImageSelectBottomSheet"
    fun newInstance(title: String?): ImageSelectBottomSheet {
      val imageSheet = ImageSelectBottomSheet()
      val bundle = Bundle()
      bundle.putString("title", title)
      imageSheet.arguments = bundle
      return imageSheet
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initLayout()

    isCancelable = true
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
  }

  private fun initLayout() {
    binding.mView = this

  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_image_select
  }

  fun onClickGallery() {
    dismiss()
    onImageSourceSelected?.onGallerySelected()

  }

  fun onClickCamera() {
    dismiss()
    onImageSourceSelected?.onCameraSelected()

  }

  fun setImageSelectListener(dialogResult: ImageSelectBottomSheet.OnImageSourceSelected) {
    onImageSourceSelected = dialogResult
  }

  interface OnImageSourceSelected {
    fun onGallerySelected()
    fun onCameraSelected()
    fun onDismissed()
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    onImageSourceSelected?.onDismissed()
  }

  override fun onCancel(dialog: DialogInterface) {
    super.onCancel(dialog)
    onImageSourceSelected?.onDismissed()
  }

  override fun permissionGranted(requestCode: Int) {

  }
}