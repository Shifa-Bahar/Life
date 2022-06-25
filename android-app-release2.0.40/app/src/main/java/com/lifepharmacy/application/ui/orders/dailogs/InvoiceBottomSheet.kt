package com.lifepharmacy.application.ui.orders.dailogs

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpRatioScreenSheet
import com.lifepharmacy.application.databinding.BottomPdfReaderBinding
import com.lifepharmacy.application.ui.orders.viewmodels.OrderDetailViewModel
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class InvoiceBottomSheet : BaseBottomUpRatioScreenSheet<BottomPdfReaderBinding>(0.9) {
  private val viewModel: OrderDetailViewModel by activityViewModels()
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCancelable = true
    initLayout()

  }

  private fun initLayout() {
//    viewModel.stream.observe(viewLifecycleOwner, Observer {
//      it?.let {
////        binding.pdfView.fromStream(it)
//
//      }
//    })
//    val uri = Uri.parse("https://lifeadmin-app.s3.me-south-1.amazonaws.com/users/invoice/AE-SB6640-1.pdf")
//    binding.pdfView.fromUri(uri)
  }


  override fun getLayoutRes(): Int {
    return R.layout.bottom_pdf_reader
  }

  override fun permissionGranted(requestCode: Int) {
    if (requestCode == ConstantsUtil.PERMISSION_PICTURE_REQUEST_CODE) {

    }
  }


}