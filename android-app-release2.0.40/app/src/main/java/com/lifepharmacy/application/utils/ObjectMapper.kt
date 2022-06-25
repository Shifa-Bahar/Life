package com.lifepharmacy.application.utils

import com.lifepharmacy.application.model.orders.*
import com.lifepharmacy.application.model.orders.suborder.SubOrderDetailForRating
import com.lifepharmacy.application.model.orders.suborder.SubOrderItemForRating
import com.lifepharmacy.application.model.orders.suborder.SubOrderItemWithJustItemOfRating
import com.lifepharmacy.application.model.orders.suborder.SubOrderProductItemForRating

object ObjectMapper {

  fun getSubOrderMainRatingToFloatRating(subOrderDetail: SubOrderDetailForRating): SubOrderDetail {
    return SubOrderDetail(
      addressId = subOrderDetail.addressId,
      codCharge = subOrderDetail.codCharge,
      createdAt = subOrderDetail.createdAt,
      deliveryFee = subOrderDetail.deliveryFee,
      discount = subOrderDetail.discount,
      fulfilmentType = subOrderDetail.fulfilmentType,
      id = subOrderDetail.id,
      parentOrderId = subOrderDetail.parentOrderId,
      items = subOrderDetail.items,
      status = subOrderDetail.status,
      statusLabel = subOrderDetail.statusLabel,
      storeCode = subOrderDetail.storeCode,
      subOrderId = subOrderDetail.subOrderId,
      tax = subOrderDetail.tax,
      total = subOrderDetail.total,
      updatedAt = subOrderDetail.updatedAt,
      userId = subOrderDetail.userId,
      transactions = subOrderDetail.transactions,
      isOrderReturnable = subOrderDetail.isOrderReturnable,
      addressesDetails = subOrderDetail.addressesDetails,
      addresses = subOrderDetail.addresses,
      orderId = subOrderDetail.orderId,
      subTotal = subOrderDetail.subTotal,
      subOrders = subOrderDetail.subOrders?.let { getSubOrderItemJustItemRatingToFloatRating(it) },
      rating = subOrderDetail.rating?.value
    )
  }

  fun getSubOrderItemJustItemRatingToFloatRating(subOrderDetail: ArrayList<SubOrderItemWithJustItemOfRating>): ArrayList<SubOrderItem> {
    return subOrderDetail.map { item ->
      SubOrderItem(
        addressId = item.addressId,
        codCharge = item.codCharge,
        createdAt = item.createdAt,
        deliveryFee = item.deliveryFee,
        discount = item.discount,
        fulfilmentType = item.fulfilmentType,
        id = item.id,
        parentOrderId = item.parentOrderId,
        items = item.items?.let { getSubOrderProductItemRatingToFloatRating(it) },
        status = item.status,
        statusLabel = item.statusLabel,
        storeCode = item.storeCode,
        subOrderId = item.subOrderId,
        tax = item.tax,
        total = item.total,
        updatedAt = item.updatedAt,
        userId = item.userId,
        transactions = item.transactions,
        isOrderReturnable = item.isOrderReturnable,
        addressesDetails = item.addressesDetails,
        addresses = item.addresses,
        orderId = item.orderId,
        subTotal = item.subTotal,
        rating = item.rating ?: 0F
      )
    } as ArrayList<SubOrderItem>
  }

  fun getSubOrderItemRatingToFloatRating(subOrderDetail: ArrayList<SubOrderItemForRating>): ArrayList<SubOrderItem> {
    return subOrderDetail.map { item ->
      SubOrderItem(
        addressId = item.addressId,
        codCharge = item.codCharge,
        createdAt = item.createdAt,
        deliveryFee = item.deliveryFee,
        discount = item.discount,
        fulfilmentType = item.fulfilmentType,
        id = item.id,
        parentOrderId = item.parentOrderId,
        items = item.items?.let { getSubOrderProductItemRatingToFloatRating(it) },
        status = item.status,
        statusLabel = item.statusLabel,
        storeCode = item.storeCode,
        subOrderId = item.subOrderId,
        tax = item.tax,
        total = item.total,
        updatedAt = item.updatedAt,
        userId = item.userId,
        transactions = item.transactions,
        isOrderReturnable = item.isOrderReturnable,
        addressesDetails = item.addressesDetails,
        addresses = item.addresses,
        orderId = item.orderId,
        subTotal = item.subTotal,
        rating = item.rating?.value
      )
    } as ArrayList<SubOrderItem>
  }


  fun getSubOrderMainFloatToRatingRating(subOrderDetail: SubOrderDetail): SubOrderDetailForRating {
    return SubOrderDetailForRating(
      addressId = subOrderDetail.addressId,
      codCharge = subOrderDetail.codCharge,
      createdAt = subOrderDetail.createdAt,
      deliveryFee = subOrderDetail.deliveryFee,
      discount = subOrderDetail.discount,
      fulfilmentType = subOrderDetail.fulfilmentType,
      id = subOrderDetail.id,
      parentOrderId = subOrderDetail.parentOrderId,
      items = subOrderDetail.items,
      status = subOrderDetail.status,
      statusLabel = subOrderDetail.statusLabel,
      storeCode = subOrderDetail.storeCode,
      subOrderId = subOrderDetail.subOrderId,
      tax = subOrderDetail.tax,
      total = subOrderDetail.total,
      updatedAt = subOrderDetail.updatedAt,
      userId = subOrderDetail.userId,
      transactions = subOrderDetail.transactions,
      isOrderReturnable = subOrderDetail.isOrderReturnable,
      addressesDetails = subOrderDetail.addressesDetails,
      addresses = subOrderDetail.addresses,
      orderId = subOrderDetail.orderId,
      subTotal = subOrderDetail.subTotal,
      subOrders = subOrderDetail.subOrders?.let { getSubOrderItemFloatToJustItemRatRating(it) },
      rating = Rating(
        value = subOrderDetail.rating ?: 0F
      )
    )
  }

  fun getSingleSubOrderItemFloatToJustItemRatRating(item: SubOrderItem): SubOrderItemWithJustItemOfRating {
    return SubOrderItemWithJustItemOfRating(
      addressId = item.addressId,
      codCharge = item.codCharge,
      createdAt = item.createdAt,
      deliveryFee = item.deliveryFee,
      discount = item.discount,
      fulfilmentType = item.fulfilmentType,
      id = item.id,
      parentOrderId = item.parentOrderId,
      items = item.items?.let { getSubOrderProductItemFloatToRatRating(it) },
      status = item.status,
      statusLabel = item.statusLabel,
      storeCode = item.storeCode,
      subOrderId = item.subOrderId,
      tax = item.tax,
      total = item.total,
      updatedAt = item.updatedAt,
      userId = item.userId,
      transactions = item.transactions,
      isOrderReturnable = item.isOrderReturnable,
      addressesDetails = item.addressesDetails,
      addresses = item.addresses,
      orderId = item.orderId,
      subTotal = item.subTotal,
      rating = item.rating ?: 0F,
      shipment = item.shipment
    )

  }

  fun getSubOrderItemFloatToJustItemRatRating(subOrderDetail: ArrayList<SubOrderItem>): ArrayList<SubOrderItemWithJustItemOfRating> {
    return subOrderDetail.map { item ->
      SubOrderItemWithJustItemOfRating(
        addressId = item.addressId,
        codCharge = item.codCharge,
        createdAt = item.createdAt,
        deliveryFee = item.deliveryFee,
        discount = item.discount,
        fulfilmentType = item.fulfilmentType,
        id = item.id,
        parentOrderId = item.parentOrderId,
        items = item.items?.let { getSubOrderProductItemFloatToRatRating(it) },
        status = item.status,
        statusLabel = item.statusLabel,
        storeCode = item.storeCode,
        subOrderId = item.subOrderId,
        tax = item.tax,
        total = item.total,
        updatedAt = item.updatedAt,
        userId = item.userId,
        transactions = item.transactions,
        isOrderReturnable = item.isOrderReturnable,
        addressesDetails = item.addressesDetails,
        addresses = item.addresses,
        orderId = item.orderId,
        subTotal = item.subTotal,
        rating = item.rating ?: 0F,
        shipment = item.shipment
      )
    } as ArrayList<SubOrderItemWithJustItemOfRating>
  }

  fun getSubOrderItemFloatToRatRating(subOrderDetail: ArrayList<SubOrderItem>): ArrayList<SubOrderItemForRating> {
    return subOrderDetail.map { item ->
      SubOrderItemForRating(
        addressId = item.addressId,
        codCharge = item.codCharge,
        createdAt = item.createdAt,
        deliveryFee = item.deliveryFee,
        discount = item.discount,
        fulfilmentType = item.fulfilmentType,
        id = item.id,
        parentOrderId = item.parentOrderId,
        items = item.items?.let { getSubOrderProductItemFloatToRatRating(it) },
        status = item.status,
        statusLabel = item.statusLabel,
        storeCode = item.storeCode,
        subOrderId = item.subOrderId,
        tax = item.tax,
        total = item.total,
        updatedAt = item.updatedAt,
        userId = item.userId,
        transactions = item.transactions,
        isOrderReturnable = item.isOrderReturnable,
        addressesDetails = item.addressesDetails,
        addresses = item.addresses,
        orderId = item.orderId,
        subTotal = item.subTotal,
        shipment = item.shipment,
        rating = Rating(
          value = item.rating ?: 0F
        )
      )
    } as ArrayList<SubOrderItemForRating>
  }

  fun getSubOrderProductItemRatingToFloatRating(subOrderDetail: ArrayList<SubOrderProductItemForRating>): ArrayList<SubOrderProductItem> {
    return subOrderDetail.map { item ->
      SubOrderProductItem(
        discount = item.discount,
        id = item.id,
        lineTotal = item.lineTotal,
        price = item.price,
        productDetails = item.productDetails,
        productName = item.productName,
        qty = item.qty,
        sku = item.sku,
        grossLineTotal = item.grossLineTotal,
        vat = item.vat,
        unitPrice = item.unitPrice,
        netLineTotal = item.netLineTotal,
        tax = item.tax,
        rating = item.rating?.value ?: 0F
      )
    } as ArrayList<SubOrderProductItem>
  }

  fun getSubOrderProductItemFloatToRatRating(subOrderDetail: ArrayList<SubOrderProductItem>): ArrayList<SubOrderProductItemForRating> {
    return subOrderDetail.map { item ->
      SubOrderProductItemForRating(
        discount = item.discount,
        id = item.id,
        lineTotal = item.lineTotal,
        price = item.price,
        productDetails = item.productDetails,
        productName = item.productName,
        qty = item.qty,
        sku = item.sku,
        grossLineTotal = item.grossLineTotal,
        vat = item.vat,
        unitPrice = item.unitPrice,
        netLineTotal = item.netLineTotal,
        tax = item.tax,
        rating = Rating(
          value = item.rating ?: 0F
        )
      )
    } as ArrayList<SubOrderProductItemForRating>
  }
}