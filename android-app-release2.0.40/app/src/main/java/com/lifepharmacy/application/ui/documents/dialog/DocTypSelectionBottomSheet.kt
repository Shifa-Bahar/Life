package com.lifepharmacy.application.ui.documents.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.databinding.BottomDocsTypeBinding
import com.lifepharmacy.application.ui.documents.viewmodel.DocumentsViewModel

/**
 * Created by Zahid Ali
 */
class DocTypSelectionBottomSheet : BaseBottomUpSheet<BottomDocsTypeBinding>() {
  private val viewModel: DocumentsViewModel by activityViewModels()

  companion object {
    const val TAG = "DocTypSelectionBottomSheet"
    fun newInstance(): DocTypSelectionBottomSheet {
      return DocTypSelectionBottomSheet()
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
    return R.layout.bottom_docs_type
  }

  fun onClickEmirateFront() {
    viewModel.docType.value = getString(R.string.emirates_id_front)
    findNavController().navigateUp()
  }

  fun onClickEmirateBack() {
    viewModel.docType.value = getString(R.string.emirates_id_back)
    findNavController().navigateUp()
  }

  fun onClickInsuranceCard() {
    viewModel.docType.value = getString(R.string.insurance_card_front)
    findNavController().navigateUp()

  }
  fun onClickInsuranceCardBack() {
    viewModel.docType.value = getString(R.string.insurance_card_back)
    findNavController().navigateUp()

  }
  fun onDismiss() {
    findNavController().navigateUp()
  }

  override fun permissionGranted(requestCode: Int) {

  }
}