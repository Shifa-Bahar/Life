package com.lifepharmacy.application.ui.utils.dailogs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogAnimationTitleDescReturnBinding
import com.lifepharmacy.application.databinding.DailogInputActionBinding
import com.lifepharmacy.application.ui.orders.viewmodels.OrderDetailViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class InputActionDialog : BaseDialogFragment<DailogInputActionBinding>() {
  var onClickInputActionDialog: ClickInputActionDialog? = null
  fun setInputDialogActionListener(dialogResult: ClickInputActionDialog) {
    onClickInputActionDialog = dialogResult
  }

  lateinit var title: String
  lateinit var description: String
  lateinit var positiveButtonTitle: String
  lateinit var negativeButtonTitle: String

  companion object {
    const val TAG = "InputActionDialog"
    var isAlreadyOpen: Boolean = false
    fun newInstance(
      title: String,
      description: String,
      positiveButtonTitle: String,
      negativeButtonTitle: String
    ): InputActionDialog {

      val dialog = InputActionDialog()
      val bundle = Bundle()
      bundle.putString("title", title)
      bundle.putString("description", description)
      bundle.putString("positiveButtonTitle", positiveButtonTitle)
      bundle.putString("negativeButtonTitle", negativeButtonTitle)
      dialog.arguments = bundle
      isAlreadyOpen = true
      return dialog
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
    isCancelable = false


  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.FullScreenTransDialogTheme)
    arguments?.let {
      title = it.getString("title").toString()
      description = it.getString("description").toString()
      positiveButtonTitle = it.getString("positiveButtonTitle").toString()
      negativeButtonTitle = it.getString("negativeButtonTitle").toString()
    }

  }

  private fun initUI() {
    binding.click = onClickInputActionDialog
    binding.title = title
    binding.description = description
    binding.positiveButton = positiveButtonTitle
    binding.negativeButton = negativeButtonTitle
    binding.btnPositive.setOnClickListener {
      isAlreadyOpen = false
//      navController.previousBackStackEntry?.savedStateHandle?.set("key", result)
      dismiss()
      onClickInputActionDialog?.onClickPositiveButton(binding.edInput.text.toString())
    }
    binding.btnNegative.setOnClickListener {
      isAlreadyOpen = false
      dismiss()
      onClickInputActionDialog?.onClickNegativeButton()
    }
  }

  override fun getLayoutRes(): Int {
    return R.layout.dailog_input_action
  }

}
