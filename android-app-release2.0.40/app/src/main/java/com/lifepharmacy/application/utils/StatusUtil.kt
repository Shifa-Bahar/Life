package com.lifepharmacy.application.utils

import android.content.Context
import com.lifepharmacy.application.R
import com.lifepharmacy.application.enums.VoucherStatus
import com.lifepharmacy.application.model.vouchers.VoucherModel

/**
 * Created by Zahid Ali
 */
class StatusUtil {
  companion object {
    fun getAllStatusString(status: Int): String? {
      var returnString = ""
      when (status) {
        0 -> {
          returnString = "Pending"
        }
        1 -> {
          returnString = "Confirmed"
        }
        2 -> {
          returnString = "Picked"
        }
        3 -> {
          returnString = "Packed"
        }
        4 -> {
          returnString = "Shipped"
        }
        5 -> {
          returnString = "Delivered"
        }
        6 -> {
          returnString = "Cancelled"
        }
      }
      return returnString
    }

    fun getVoucherStatusUtil(voucher: VoucherModel, context: Context): String {
      var returnText = ""
      when {
        voucher.mANUALEXPIRY == "1" -> {
          returnText =
            "${context.getString(R.string.status_colon)} ${context.getString(R.string.expired)}"
        }
        voucher.sTATUS == "2" -> {
          returnText =
            "${context.getString(R.string.status_colon)} ${context.getString(R.string.redeemed)}"
        }
        else -> {
          val fromDate =
            DateTimeUtil.getCalenderObjectFromStringDateOnlyWithoutTimeZone(
              voucher.vALIDFROM ?: ""
            )?.time
          val toDate =
            DateTimeUtil.getCalenderObjectFromStringWithoutTimeZone(voucher.eXPIRYDATE ?: "")?.time
          val currentDate = DateTimeUtil.getCurrentDateAndTime().time
          fromDate?.let {
            toDate?.let {
              when {
                fromDate > currentDate -> {
                  returnText =
                    "${context.getString(R.string.status_colon)} ${context.getString(R.string.not_yet_available)}"
                }
                toDate < currentDate -> {
                  returnText =
                    "${context.getString(R.string.status_colon)} ${context.getString(R.string.expired)}"
                }
                fromDate.compareTo(currentDate) * currentDate.compareTo(toDate) > 0
                -> {
                  returnText =
                    "${context.getString(R.string.status_colon)} ${context.getString(R.string.valid)}"
                }
              }
            }
          }
        }
      }
      return returnText
    }
    fun voucherStatusEnum(voucher: VoucherModel):VoucherStatus{
      var voucherStatus : VoucherStatus = VoucherStatus.VALID
      when (voucher.mANUALEXPIRY) {
        "1" -> {
          voucherStatus = VoucherStatus.EXPIRED
        }
        else -> {
          val fromDate =
            DateTimeUtil.getCalenderObjectFromStringDateOnlyWithoutTimeZone(
              voucher.vALIDFROM ?: ""
            )?.time
          val toDate =
            DateTimeUtil.getCalenderObjectFromStringWithoutTimeZone(voucher.eXPIRYDATE ?: "")?.time
          val currentDate = DateTimeUtil.getCurrentDateAndTime().time
          fromDate?.let {
            toDate?.let {
              when {
                toDate < currentDate -> {
                  voucherStatus = VoucherStatus.EXPIRED
                }
                fromDate.compareTo(currentDate) * currentDate.compareTo(toDate) > 0
                -> {
                  voucherStatus = VoucherStatus.VALID
                }
              }
            }
          }
        }
      }
      return voucherStatus
    }
  }

}