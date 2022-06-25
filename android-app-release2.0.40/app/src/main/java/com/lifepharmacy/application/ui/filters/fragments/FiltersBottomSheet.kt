package com.lifepharmacy.application.ui.filters.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpRatioScreenSheet
import com.lifepharmacy.application.databinding.BottomSheetFiltersBinding
import com.lifepharmacy.application.managers.analytics.filterApplied
import com.lifepharmacy.application.managers.analytics.filterScreenOpen
import com.lifepharmacy.application.model.filters.FilterModel
import com.lifepharmacy.application.model.filters.FilterTypeModel
import com.lifepharmacy.application.ui.filters.adapters.*
import com.lifepharmacy.application.ui.productList.viewmodel.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import kotlin.math.roundToInt


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class FiltersBottomSheet : BaseBottomUpRatioScreenSheet<BottomSheetFiltersBinding>(0.65),
  OnClickFilters,
  ClickFiltersBottomSheet, ClickFilterType, ClickFilterCheckBox, ClickFilterRadio {

  private val viewModel: ProductListViewModel by activityViewModels()
  var title: String? = "Select Filter"

  private var onClickCarDialog: FilterCallback? = null

  private lateinit var filtersTypeAdapter: FilterTypesAdapter
  private lateinit var filterCheckBoxAdapter: FilterCheckboxAdapter
  private lateinit var filterRadioAdapter: FilterRadioAdapter
  private var selectedParent = 0

  companion object {
    const val TAQ = "FiltersBottomSheet"
    fun newInstance(): FiltersBottomSheet {
      val placesSavedDialog = FiltersBottomSheet()
      val bundle = Bundle()
      placesSavedDialog.arguments = bundle
      return placesSavedDialog
    }
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.appManager.analyticsManagers.filterScreenOpen()
    if (arguments != null) {
      title = requireArguments().getString("title")
    }
    initLayout()

    observers()
    isCancelable = true


  }

  fun initLayout() {
    binding.click = this
    binding.llRange.viewModel = viewModel
    binding.llRange.lifecycleOwner = this
    binding.isList = viewModel.isList
    binding.isThereFilter = viewModel.isThereFilter
    viewModel.isList.set(true)
    filtersTypeAdapter = FilterTypesAdapter(requireActivity(), this, viewModel.filtersManager)
    binding.layoutFilters.filter.adapter = filtersTypeAdapter
    filtersTypeAdapter.setDataChanged(viewModel.filtersManager.allFilters)
    try {
      viewModel.filtersManager.allFilters?.get(0)?.let { initActiveFilters(it, 0) }
    } catch (e: Exception) {

    }
//
    priceRangeInit()
  }

  @SuppressLint("LongLogTag")
  private fun priceRangeInit() {
    binding.llRange.rangeSlider.addOnSliderTouchListener(object :
      RangeSlider.OnSliderTouchListener {
      override fun onStartTrackingTouch(slider: RangeSlider) {
        val values = slider.values
      }

      override fun onStopTrackingTouch(slider: RangeSlider) {
        changePriceRange(slider)
      }
    })
    binding.llRange.rangeSlider.addOnChangeListener { rangeSlider, value, fromUser ->
      // Responds to when slider's value is changed
      changePriceRange(rangeSlider)
    }
    binding.llRange.rangeSlider.setLabelFormatter(LabelFormatter { value -> //It is just an example
      when (value) {
        1000.0f -> {
          getString(R.string.ten_thousand_pluse)

        }
        1.0f -> {
          getString(R.string.zero)
        }
        else -> {
          value.toString()
        }
      }
    })
  }

  fun observers() {
//    viewModel.filtersManager.allFiltersMut.observe(viewLifecycleOwner, Observer {
//      it?.let {
//        viewModel.isThereFilter.set(viewModel.filtersManager.checkIfAnyFilterSelected())
//      }
//    })
    viewModel.filtersManager.totalCountOfFilters.observe(viewLifecycleOwner, Observer {
      it?.let {
        if (it > 0) {
          viewModel.isThereFilter.set(true)
        } else {
          viewModel.isThereFilter.set(false)
        }
      }
    })
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = super.onCreateDialog(savedInstanceState)
    return dialog
  }

  override fun onClickApply() {
    if (viewModel.rangeTo.value == getString(R.string.ten_thousand_pluse)) {
      viewModel.filtersManager.toPrice = ""
    } else {
      viewModel.filtersManager.toPrice = viewModel.rangeTo.value
    }
    if (viewModel.rangeFrom.value == getString(R.string.zero)) {
      viewModel.filtersManager.fromPrice = ""
    } else {
      viewModel.filtersManager.fromPrice = viewModel.rangeFrom.value
    }
    viewModel.appManager.analyticsManagers.filterApplied(viewModel.filtersManager.getFilterModel())
    onClickCarDialog?.onClickApply()
    dismiss()
  }

  override fun onClickClear() {

    viewModel.filtersManager.clearFilters()
    refreshFilters()
  }

  override fun onClickClose() {
    dismiss()
  }

  private fun refreshFilters() {
    filtersTypeAdapter.setDataChanged(viewModel.filtersManager.allFilters)

  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_filters
  }


  override fun permissionGranted(requestCode: Int) {

  }


  interface FilterCallback {
    fun onClickApply()
    fun onClickClear()
  }

  fun setClickCaListener(dialogResult: FilterCallback) {
    onClickCarDialog = dialogResult
  }

  override fun onClickFilterType(filterType: FilterTypeModel, position: Int) {
    selectedParent = position
    initActiveFilters(filterType, position)

  }

  private fun changePriceRange(slider: RangeSlider) {
    val values = slider.values
    Log.d("onStopTrackingTouchFrom", values[0].toString())
    Log.d("onStopTrackingTouch T0", values[1].toString())
    if (values[0] == 0f) {
      viewModel.rangeFrom.value = getString(R.string.zero)
    } else {
      viewModel.rangeFrom.value = values[0].roundToInt().toString()
    }
    if (values[1] == 1000.0f) {
      viewModel.rangeTo.value = getString(R.string.ten_thousand_pluse)
    } else {
      viewModel.rangeTo.value = values[1].roundToInt().toString()
    }
  }

  private fun initActiveFilters(filterType: FilterTypeModel, position: Int) {
    filtersTypeAdapter.setItemSelected(position)
    when (filterType.inputType) {
      "checkbox" -> {
        viewModel.isList.set(true)
        filterCheckBoxAdapter = FilterCheckboxAdapter(
          requireActivity(),
          position,
          filterType.type,
          this
        )
        binding.rvFilterList.adapter = filterCheckBoxAdapter
        filterCheckBoxAdapter.setDataChanged(filterType.data)
      }
      "radio" -> {
        viewModel.isList.set(true)
        filterRadioAdapter = FilterRadioAdapter(
          requireActivity(),
          filterType.type,
          position,
          this
        )
        binding.rvFilterList.adapter = filterRadioAdapter
        filterRadioAdapter.setDataChanged(filterType.data)
      }
      "range" -> {
        viewModel.isList.set(false)
//        if (!viewModel.filtersManager.fromPrice.isNullOrEmpty()) {
//          viewModel.filtersManager.fromPrice?.let {
//            binding.llRange.rangeSlider.valueFrom = it.toFloat()
//          }
//        }
//        if (!viewModel.filtersManager.toPrice.isNullOrEmpty()) {
//          viewModel.filtersManager.toPrice?.let {
//            binding.llRange.rangeSlider.valueTo = it.toFloat()
//          }
//        }
      }
      else -> {

      }
    }

//    if (filterType.inputType == "checkbox") {
//
//    }
//    if (filterType.inputType == "radio") {
//
//    }
//    if (filterType.inputType == "range") {
//
//    } else {
//      viewModel.saveSelectedRange(
//        binding.llRange.edFrom.text.toString(),
//        binding.llRange.edTo.text.toString()
//      )
//      binding.llRange.edFrom.clearFocus()
//      binding.llRange.edTo.clearFocus()
//      KeyboardUtils.hideKeyboard(requireActivity(), binding.llRange.edFrom)
//      KeyboardUtils.hideKeyboard(requireActivity(), binding.llRange.edTo)
//    }
//    if (!viewModel.filtersManager.fromPrice.isNullOrEmpty()) {
//      binding.llRange.edFrom.setText(viewModel.filtersManager.fromPrice.toString())
//    }
//    if (!viewModel.filtersManager.toPrice.isNullOrEmpty()) {
//      binding.llRange.edFrom.setText(viewModel.filtersManager.toPrice.toString())
//    }
  }

  override fun onClickCheckBox(
    filter: FilterModel,
    type: String,
    position: Int,
    parentPosition: Int
  ) {
    viewModel.filtersManager.checkOrUncheck(parentPosition, position)
    filterCheckBoxAdapter.replaceData(viewModel.filtersManager.allFilters?.get(parentPosition)?.data)
    filterCheckBoxAdapter.setItems(position)
    filtersTypeAdapter.setItemSelected(parentPosition)
  }

  override fun onClickRadio(
    filter: FilterModel,
    type: String,
    position: Int,
    parentPosition: Int
  ) {
    viewModel.filtersManager.radioButtonChecked(parentPosition, position)
    filterRadioAdapter.replaceData(viewModel.filtersManager.allFilters?.get(parentPosition)?.data)
    filterRadioAdapter.setItemSelected(position)
    filtersTypeAdapter.setItemSelected(parentPosition)
  }


}