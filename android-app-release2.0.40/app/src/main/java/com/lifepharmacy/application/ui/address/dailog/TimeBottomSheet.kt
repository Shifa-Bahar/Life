package com.lifepharmacy.application.ui.address.dailog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.databinding.BottomSheetAddressTypeBinding
import com.lifepharmacy.application.model.address.AddressTypeModel
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.address.adapters.ClickItemAddressType
import com.lifepharmacy.application.ui.address.adapters.TimeTypeTypeAdapter
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class TimeBottomSheet : BaseBottomUpSheet<BottomSheetAddressTypeBinding>(),
    ClickItemAddressType {
    private val viewModel: AddressViewModel by activityViewModels()
    private lateinit var timeTypeAdapter: TimeTypeTypeAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = true
        initLayout()
        observers()
    }

  private  fun initLayout() {
      timeTypeAdapter = TimeTypeTypeAdapter(requireActivity(),this)
      binding.rvAddressTypes.adapter = timeTypeAdapter
    }
    private fun observers(){
        timeTypeAdapter.setDataChanged(viewModel.getTimeTypes())
    }

    override fun getLayoutRes(): Int {
        return R.layout.bottom_sheet_address_type
    }

    override fun permissionGranted(requestCode: Int) {

    }

    override fun onClickAddress(addressTypeModel: AddressTypeModel) {
        viewModel.setTimeType(addressTypeModel)
        findNavController().navigateUp()
    }


}