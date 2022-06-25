package com.lifepharmacy.application.managers

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.lifepharmacy.application.model.filters.FilterMainRequest
import com.lifepharmacy.application.model.filters.FilterModel
import com.lifepharmacy.application.model.filters.FilterRequestModel
import com.lifepharmacy.application.model.filters.FilterTypeModel
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.Logger
import javax.inject.Inject

class FiltersManager
@Inject constructor(var persistenceManager: PersistenceManager) {
  var allFiltersMut = MutableLiveData<ArrayList<FilterTypeModel>>()
  var allFilters: ArrayList<FilterTypeModel>? = ArrayList()
  var appliedFilters: ArrayList<FilterRequestModel>? = ArrayList()
  var appliedFilterMain: FilterMainRequest = FilterMainRequest()
  var fromPrice: String? = null
  var searchQuery: String? = ""
  var instantOnly: Boolean? = false
  var toPrice: String? = null
  var parentFilter: FilterRequestModel? = null
  lateinit var subCategory: FilterRequestModel
  var totalCountOfFilters = MutableLiveData<Int>()

  //    fun selectUnselected(filter: FilterModel, type: String, inputType: String = "") {
//        filter.type = type
//        filter.inputType = inputType
//        if (inputType == "radio") {
//            selectedFilters?.let {
//                if (!it.isNullOrEmpty()) {
//                    for (item in it) {
//                        if (item.inputType == "radio"){
//                            removeItem(filter, type)
//                        }
//
//                    }
//                }
//
//            }
//
//        }
//        if (selectedFilters?.contains(filter) == true) {
//            removeItem(filter, type)
//        } else {
//            selectedFilters?.add(filter)
//        }
//        selectedFiltersMut.value = selectedFilters
//    }
//
//    fun removeItem(filter: FilterModel, type: String) {
//        filter.type = type
//        if (selectedFilters?.contains(filter) == true) {
//            selectedFilters?.remove(filter)
//        }
//        selectedFiltersMut.value = selectedFilters
//    }
//
//    fun addMinRange(string: String) {
//        selectedFilters?.add(FilterModel(key = "lowerPriceLimit", value = string, condition = ">="))
//    }
//
//    fun addMaxRange(string: String) {
//        selectedFilters?.add(FilterModel(key = "maxPriceLimit", value = string, condition = "<="))
//    }
  fun addFirstFilter(id: String, type: String) {
    appliedFilters?.clear()
    val filterModel = FilterRequestModel()
    filterModel.key = type
    filterModel.value = id
    filterModel.condition = "="
    parentFilter = filterModel
    appliedFilters?.add(parentFilter!!)
    appliedFilterMain.filters = appliedFilters
  }

  fun replaceParentFilter(filterRequestModel: FilterRequestModel) {
    appliedFilters?.clear()
    parentFilter = filterRequestModel
    appliedFilters?.add(parentFilter!!)
    appliedFilterMain.filters = appliedFilters
  }

  fun addSubCatFilter(id: String, type: String) {
    val tempFilterModel = FilterRequestModel()
    if (parentFilter?.key == type) {
      appliedFilters?.remove(parentFilter)
      tempFilterModel.key = type
      tempFilterModel.value = parentFilter?.value
      if (tempFilterModel.value.isNullOrBlank()) {
        tempFilterModel.value = id
      } else {
        tempFilterModel.value = tempFilterModel.value + "," + id
      }
      tempFilterModel.condition = "="
    } else {
      tempFilterModel.key = type
      tempFilterModel.value = id
      tempFilterModel.condition = "="
    }
//    val filterModel = FilterRequestModel()
//    filterModel.key = type
//    filterModel.value = id
//    filterModel.condition = "="
    subCategory = tempFilterModel
    appliedFilters?.add(subCategory)
    appliedFilterMain.filters = appliedFilters
  }

  fun getFilterModel(): FilterMainRequest {
    appliedFilterMain.filters = appliedFilters
    if (searchQuery.isNullOrEmpty()) {
      appliedFilterMain.search = null
    } else {
      appliedFilterMain.search = searchQuery
    }

    appliedFilterMain.instantOnly = instantOnly
    return appliedFilterMain
  }

  fun updateAllFilters(filters: ArrayList<FilterTypeModel>) {
    allFilters = filters
    allFiltersMut.value = allFilters

  }

  fun clearFilters() {
    allFilters?.let {
      if (!allFilters.isNullOrEmpty()) {
        for (mainItem in it) {
          mainItem.data?.let { dataArray ->
            for (item in dataArray) {
              item.isChecked = false
            }
          }

        }
      }
    }
    fromPrice = ""
    toPrice = ""
    clearSelectedFilters()
    allFiltersMut.value = allFilters
  }

  fun clearAllIncludingParentFilter() {
    if (parentFilter != null) {
      parentFilter = null
    }

    clearFilters()
  }

  private fun clearSelectedFilters() {
    appliedFilters?.let {
      it.clear()
      parentFilter?.let { it1 -> it.add(it1) }
    }
    calculateTotalSelectedFilters()
  }

  fun checkOrUncheck(parentPosition: Int, internalPosition: Int) {
    allFilters?.let {
      it[parentPosition].data?.get(internalPosition)?.isChecked =
        it[parentPosition].data?.get(internalPosition)?.isChecked != true

    }
    allFiltersMut.value = allFilters
  }

  fun clearSingleFilter(parentPosition: Int, internalPosition: Int) {
    checkOrUncheck(parentPosition, internalPosition)
    calculateTotalSelectedFilters()
  }

  fun radioButtonChecked(parentPosition: Int, internalPosition: Int) {
    allFilters?.let {
      it[parentPosition].data?.let { data ->
        if (!data.isNullOrEmpty()) {
          for (item in data) {
            item.isChecked = false
          }
        }
      }
    }
    allFilters?.let {
      it[parentPosition].data?.get(internalPosition)?.isChecked =
        it[parentPosition].data?.get(internalPosition)?.isChecked != true
    }
    allFiltersMut.value = allFilters
  }

  fun checkIfAnyFilterSelected(): Boolean {
    allFilters?.let {
      if (!allFilters.isNullOrEmpty()) {
        for (mainItem in it) {
          mainItem.data?.let { dataArray ->
            for (item in dataArray) {
              if (item.isChecked) {
                return true
              }
            }
          }

        }
      }

    }
    return false
  }

  fun getSelectedText(parentPosition: Int): String? {
    allFilters?.let {
      if (!it.isNullOrEmpty()) {
        it[parentPosition].data?.let { dataArray ->
          if (it[parentPosition].inputType == "radio") {
            for (item in dataArray) {
              if (item.isChecked) {
                return item.title
              }
            }
          } else {
            var count = 0
            for (item in dataArray) {
              if (item.isChecked) {
                count++
              }
            }
            if (count > 0) {
              return "$count, Selected items"
            }
          }

        }
      }

    }
    return null
  }

  fun makeSelectedFilter(): FilterMainRequest? {
    clearSelectedFilters()
    allFilters?.let {
      if (!allFilters.isNullOrEmpty()) {
        for (mainItem in it) {
          if (mainItem.inputType == "range") {
            checkAndAddRangeValue()
          } else {
            mainItem.data?.let { dataArray ->
              checkAndAddFilter(dataArray, mainItem.type)
            }
          }
        }
      }
    }
    calculateTotalSelectedFilters()
    appliedFilterMain.filters = appliedFilters
    val gson = Gson()
    val jsonStr = gson.toJson(appliedFilterMain)
    Logger.d("Filter", jsonStr)

    return appliedFilterMain
  }

  fun checkAndAddRangeValue() {

    if (!toPrice?.trim().isNullOrBlank()) {
      val filterModelMax = FilterRequestModel()
      filterModelMax.key = "maxPriceLimit"
      filterModelMax.value = toPrice
      filterModelMax.condition = "<="
      appliedFilters?.add(filterModelMax)
    }
    if (!fromPrice?.trim().isNullOrBlank()) {
      val filterModelMin = FilterRequestModel()
      filterModelMin.key = "lowerPriceLimit"
      filterModelMin.value = fromPrice
      filterModelMin.condition = ">="
      appliedFilters?.add(filterModelMin)
    }

//    if (!toPrice?.trim().isNullOrBlank() && !fromPrice?.trim().isNullOrBlank()) {
//      val filterModelMin = FilterRequestModel()
//      filterModelMin.key = "lowerPriceLimit"
//      filterModelMin.value = fromPrice
//      filterModelMin.condition = ">="
//      val filterModelMax = FilterRequestModel()
//      filterModelMax.key = "maxPriceLimit"
//      filterModelMax.value = toPrice
//      filterModelMax.condition = "<="
//      appliedFilters?.add(filterModelMin)
//      appliedFilters?.add(filterModelMax)
//    }
  }

  private fun checkAndAddFilter(list: ArrayList<FilterModel>, mainType: String) {
    val tempFilterModel = FilterRequestModel()
    for (item in list) {
      if (item.isChecked) {
        if (mainType == "orderBy") {
          tempFilterModel.key = mainType
          tempFilterModel.value = item.key
          tempFilterModel.condition = item.value
        } else {
          if (parentFilter?.key == mainType) {
            appliedFilters?.remove(parentFilter)
            tempFilterModel.key = mainType
            if (tempFilterModel.value.isNullOrBlank()) {
              tempFilterModel.value = item.id
            } else {
              tempFilterModel.value = tempFilterModel.value + "," + item.id
            }
            tempFilterModel.condition = "="
          } else {
            tempFilterModel.key = mainType
            if (tempFilterModel.value.isNullOrBlank()) {
              tempFilterModel.value = item.id
            } else {
              tempFilterModel.value = tempFilterModel.value + "," + item.id
            }
            tempFilterModel.condition = "="
          }

        }
      }
    }
    if (!tempFilterModel.value.isNullOrBlank()) {
      appliedFilters?.add(tempFilterModel)
    }

  }

  private fun calculateTotalSelectedFilters() {
    var localCount = 0
    if (!allFilters.isNullOrEmpty()) {
      for (item in allFilters!!) {
        item.setSelectedFilterCount()
        localCount += item.individualCount

      }
    }
//    if (localCount > 0) {
    if (!(fromPrice.isNullOrEmpty() && toPrice.isNullOrEmpty())) {
      localCount += 1
    }
//    }
    totalCountOfFilters.value = localCount
  }

  fun checkIfFilterContains(type: String, id: String): Boolean {
    return parentFilter?.value?.contains(id) == true && parentFilter?.key.equals(type)

  }

  fun getFilterInString(): String {
    val gson = Gson()
    return gson.toJson(getFilterModel())
  }


//    fun filterSubtitle(type: String): String? {
//        if (type == "orderBy") {
//            var title =
//                selectedFilters?.firstOrNull { seleted -> seleted.type == type }
//            return title?.title
//        } else {
//            return null
//        }
//    }
}