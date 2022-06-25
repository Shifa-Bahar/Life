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
import com.lifepharmacy.application.ui.address.adapters.AddressTypeAdapter
import com.lifepharmacy.application.ui.address.adapters.ClickItemAddressType
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class AddressTypeBottomSheet : BaseBottomUpSheet<BottomSheetAddressTypeBinding>(),
    ClickItemAddressType {
    private val viewModel: AddressViewModel by activityViewModels()
    private lateinit var addressTypeAdapter: AddressTypeAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = true
        initLayout()
        observers()
    }

  private  fun initLayout() {
      addressTypeAdapter = AddressTypeAdapter(requireActivity(),this)
      binding.rvAddressTypes.adapter = addressTypeAdapter
    }
    private fun observers(){
        addressTypeAdapter.setDataChanged(viewModel.getAddressTypes())
    }

    override fun getLayoutRes(): Int {
        return R.layout.bottom_sheet_address_type
    }

    override fun permissionGranted(requestCode: Int) {

    }

    override fun onClickAddress(addressTypeModel: AddressTypeModel) {
        viewModel.setAddressType(addressTypeModel)
        findNavController().navigateUp()
    }


}