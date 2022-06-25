package com.lifepharmacy.application.ui.utils.dailogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.databinding.BottomImageActionBinding
import com.lifepharmacy.application.databinding.BottomImageSelectBinding

/**
 * Created by Zahid Ali
 */
class ImageActionBottomSheet : BaseBottomUpSheet<BottomImageActionBinding>() {

  var onImageSourceSelected: OnImageActionSelected? = null
  lateinit var url: String

  companion object {
    const val TAG = "ImageActionBottomSheet"
    fun newInstance(url: String): ImageActionBottomSheet {
      val imageSheet = ImageActionBottomSheet()
      val bundle = Bundle()
      bundle.putString("url", url)
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
    arguments?.let {
      url = it.getString("url").toString()
    }
  }

  private fun initLayout() {
    binding.mView = this

  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_image_action
  }

  fun onView() {
    dismiss()
    onImageSourceSelected?.onClickView(url)
  }

  fun onDelete() {
    dismiss()
    onImageSourceSelected?.onDelete(url)

  }

  fun onDismiss() {
    dismiss()
    onImageSourceSelected?.onDismissed()
  }

  fun setImageSelectListener(dialogResult: OnImageActionSelected) {
    onImageSourceSelected = dialogResult
  }

  interface OnImageActionSelected {
    fun onClickView(url:String)
    fun onDelete(url: String)
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