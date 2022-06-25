package com.lifepharmacy.application.ui.utils.dailogs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogAnimationTitleDescReturnBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class AnimationTitleDescActionDialog : BaseDialogFragment<DailogAnimationTitleDescReturnBinding>() {
  var onClickAnimationTitleDescActionDialog: ClickAnimationTitleDescActionDialog? = null
  fun setAnimationDialogActionListener(dialogResult: ClickAnimationTitleDescActionDialog) {
    onClickAnimationTitleDescActionDialog = dialogResult
  }
  lateinit var title:String
  lateinit var description:String
  lateinit var buttonTitle:String
  lateinit var animation:String

  companion object {
    const val TAG = "ImageSelectBottomSheet"
    fun newInstance(title: String,description:String,buttonTitle:String,animation:String): ImageSelectBottomSheet {
      val dialog = ImageSelectBottomSheet()
      val bundle = Bundle()
      bundle.putString("title", title)
      bundle.putString("description", description)
      bundle.putString("buttonTitle", buttonTitle)
      bundle.putString("animation", animation)
      dialog.arguments = bundle
      return dialog
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
    observers()
    isCancelable = false


  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_FRAME, R.style.FullScreenTransDialogTheme)
    arguments?.let {
      title = it.getString("title").toString()
      description = it.getString("description").toString()
      buttonTitle = it.getString("buttonTitle").toString()
      animation = it.getString("animation").toString()
    }

  }

  private fun initUI() {
    binding.click = onClickAnimationTitleDescActionDialog
    binding.lifecycleOwner = this
    binding.animationView.setAnimation(animation)
    binding.animationView.playAnimation()
    binding.title = title
    binding.description = description
    binding.buttonTitle = buttonTitle
  }


  private fun observers() {
  }


  override fun getLayoutRes(): Int {
    return R.layout.dailog_animation_title_desc_return
  }

}
