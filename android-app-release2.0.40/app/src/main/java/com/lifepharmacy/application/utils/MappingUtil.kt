package com.lifepharmacy.application.utils

import com.lifepharmacy.application.model.address.AddressTypeModel

/**
 * Created by Zahid Ali
 */
object MappingUtil {
    fun getAddressType(type:String): AddressTypeModel {
        var addressTypeModel =
          AddressTypeModel()
        if (type == "Work") {
            addressTypeModel.addressId = 1
        }
        if (type == "Home") {
            addressTypeModel.addressId = 2
        }
        if (type == "Others") {
            addressTypeModel.addressId = 3
        }
        addressTypeModel.name = type
        return addressTypeModel
    }
    fun getTimeType(type:Int): AddressTypeModel {
        var timeTypeModel =
          AddressTypeModel()
        if (type.toString() == "0") {
            timeTypeModel.name = "Morning"
            timeTypeModel.addressId = 0
        }
        if (type.toString() == "1") {
            timeTypeModel.name = "Afternoon"
            timeTypeModel.addressId = 1
        }
        if (type.toString() == "2") {
            timeTypeModel.name = "Evening"
            timeTypeModel.addressId = 2
        }
        return  timeTypeModel
    }

}