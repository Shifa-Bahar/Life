package com.lifepharmacy.application.ui.profile.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.databinding.BottomDocsTypeBinding
import com.lifepharmacy.application.databinding.BottomGenederBinding
import com.lifepharmacy.application.ui.documents.viewmodel.DocumentsViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel

/**
 * Created by Zahid Ali
 */
class GenderSelectionBottomSheet : BaseBottomUpSheet<BottomGenederBinding>() {
  private val viewModel: ProfileViewModel by activityViewModels()

  companion object {
    const val TAG = "GenderSelectionBottomSheet"
    fun newInstance(): GenderSelectionBottomSheet {
      return GenderSelectionBottomSheet()
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
    return R.layout.bottom_geneder
  }

  fun onClickMale() {
    viewModel.gender.value = getString(R.string.male)
    findNavController().navigateUp()
  }

  fun onClickFemale() {
    viewModel.gender.value = getString(R.string.female)
    findNavController().navigateUp()
  }

  fun onClickNotSpecific() {
    viewModel.gender.value = getString(R.string.not_specified)
    findNavController().navigateUp()

  }

  fun onDismiss() {
    findNavController().navigateUp()
  }

  override fun permissionGranted(requestCode: Int) {

  }
}