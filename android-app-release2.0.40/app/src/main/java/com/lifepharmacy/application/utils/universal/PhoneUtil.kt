package com.lifepharmacy.application.utils.universal

import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

/**
 * Created by Zahid Ali
 */
class PhoneUtil {
    companion object {

        fun getFormatNumber(phone:String):String{
            var validatedNumber = phone
            if (validatedNumber.startsWith("00")){
                validatedNumber = "+"+phone.substring(2)
            }
            else if (validatedNumber.startsWith("+")){
                validatedNumber = phone
            }
            else if (validatedNumber.startsWith("05")){
                validatedNumber = "+971"+phone.substring(1)
            }
            else  if (!validatedNumber.startsWith("+")){
                validatedNumber = "+$phone"
            }
            return validatedNumber
        }

        fun getCountryIsoCode(number: String,phoneNumberUtil: PhoneNumberUtil): String? {


            val phoneNumber = try {
                phoneNumberUtil.parse(number, null)
            } catch (e: NumberParseException) {
                e.printStackTrace()
                return null
            }
            if (phoneNumber == null) return null

            return phoneNumberUtil.getRegionCodeForCountryCode(phoneNumber.countryCode)
        }
    }
}