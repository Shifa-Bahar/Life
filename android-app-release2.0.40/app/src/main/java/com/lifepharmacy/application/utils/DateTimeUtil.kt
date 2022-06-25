package com.lifepharmacy.application.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Zahid Ali
 */
class DateTimeUtil {
  companion object {
    @SuppressLint("SimpleDateFormat")
    fun getTimesInMilles(time: String?): Long {
      val timeMiles: Long
      val sdf = SimpleDateFormat("dd-MM-yyyy")
      timeMiles = try {
        val mDate = sdf.parse(time)
        mDate.time
        // System.out.println("Date in milli :: " + fromDateMilles);
      } catch (e: ParseException) {
        e.printStackTrace()
        System.currentTimeMillis()
      }
      return timeMiles
    }

    @SuppressLint("SimpleDateFormat")
    fun getFirstDateOfCurrentMonth(): String? {
      val firstDayofMonth: String
      val cal = Calendar.getInstance()
      cal[Calendar.DAY_OF_MONTH] = cal.getActualMinimum(Calendar.DAY_OF_MONTH)
      val df = SimpleDateFormat("dd-MMM-yyyy")
      firstDayofMonth = df.format(cal.time)
      return firstDayofMonth
    }

    fun getCurrentDateAndTime(): Calendar {
      //Current Time
      val cal = Calendar.getInstance()
      cal.timeZone = TimeZone.getTimeZone("Asia/Dubai")
      cal[Calendar.YEAR] = cal[Calendar.YEAR]
      cal[Calendar.DAY_OF_MONTH] = cal[Calendar.DAY_OF_MONTH]
      cal[Calendar.MONTH] = cal[Calendar.MONTH]
      cal[Calendar.HOUR_OF_DAY] = cal[Calendar.HOUR_OF_DAY]
      cal[Calendar.MINUTE] = cal[Calendar.MINUTE]
      cal[Calendar.SECOND] = 0

      return cal
    }

    fun getCurrentDateObject(): Date {
      val cal = Calendar.getInstance()
      return cal.time
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormatTimeString(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("MMM d, yyyy h:mm a")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormatonlyTimeString(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("h:mm")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    fun getUtcTimeToDateObject(input: String): Date? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        date
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormatDateOnlyString(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("MMM d, yyyy")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    @SuppressLint("SimpleDateFormat")
    fun getStringFromServerBlogDate(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("MMM d, yyyy")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }
    @SuppressLint("SimpleDateFormat")
    fun getFormatMonthYearString(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("MM/yy")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }
    @SuppressLint("SimpleDateFormat")
    fun getUTCStringToDate(input: String): Date? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        utcFormat.parse(input)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    @SuppressLint("SimpleDateFormat")
    fun getUTCStringFromDate(input: Date): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getDefault()
        utcFormat.format(input)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormatDateOnlyStringWithOutTimeZone(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
//        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("MMM d, yyyy")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    fun getCalenderObjectFromStringWithoutTimeZone(input: String): Calendar? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("Asia/Dubai")
//                utcFormat.timeZone = TimeZone.getDefault()
        val date = utcFormat.parse(input)
        val cal = Calendar.getInstance()
        cal.time = date
        return cal
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    fun getCalenderObjectFromStringWithoutTimeZoneAndMilSec(input: String): Calendar? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("Asia/Dubai")
//                utcFormat.timeZone = TimeZone.getDefault()
        val date = utcFormat.parse(input)
        val cal = Calendar.getInstance()
        cal.time = date
        return cal
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    fun getStringFromStringWithoutTimeZone(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("MM/d, h:mm")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }


    fun getStringDateFromStringWithoutTimeZone(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("dd/MM")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    fun getDayStringDateFromStringWithoutTimeZone(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("EEEE")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    fun getDayDAteFromStringWithoutTimeZone(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("EEEE,dd-MM-yyyy")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    fun getDayDAteFromStringWithoutYeRTimeZone(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("MMM,dd")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    fun getStartendtimeFromStringWithoutTimeZone(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("hh:mm a")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    fun getWeekStringDateFromStringWithoutTimeZone(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("EEE")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }
    fun getDateStringDateFromStringWithoutTimeZone(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("dd/MM/yyyy")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    fun getStringDateFromStringWithoutTimeZoneAndMilSecs(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("MMM d, yyyy")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    fun getStringDateFromStringWithoutTimeZoneAndMilSec(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("dd-MM-yyyy")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    fun getCalenderObjectFromStringDateOnlyWithoutTimeZone(input: String): Calendar? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//        utcFormat.timeZone = TimeZone.getTimeZone("Asia/Dubai")
        val date = utcFormat.parse(input)
        val cal = Calendar.getInstance()
        cal.time = date
        return cal
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    @SuppressLint("SimpleDateFormat")
    fun getCalenderStringFromStringDateOnlyWithoutTimeZone(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("MMM d, yyyy")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }

    @SuppressLint("SimpleDateFormat")
    fun utcToLocal(inputDate: String?): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-ddTHH:mm:ssz", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(inputDate)
        val outPutDateFormat = SimpleDateFormat("MMMM d yyyy hh:mm a")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        null
      }

    }

    @SuppressLint("SimpleDateFormat")
    fun getShortFormatTimeString(input: String): String? {
      return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = utcFormat.parse(input)
        val outPutDateFormat = SimpleDateFormat("MM/d, h:mm")
        outPutDateFormat.timeZone = TimeZone.getDefault()
        outPutDateFormat.format(date)
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
      }
    }


    @SuppressLint("SimpleDateFormat")
    fun isGivenTimeIsBetweenTwoTime(
      targetTimeStr: String,
      startTimeStr: String,
      endTimeStr: String
    ): Boolean {
      try {
        val formatter =
          SimpleDateFormat("HH:mm")
        val startTime = formatter.parse(startTimeStr)
        val endTime = formatter.parse(endTimeStr)
        val targetTime = formatter.parse(targetTimeStr)
        return if (startTime.after(endTime)) {
          targetTime.before(endTime) || targetTime.after(startTime)
        } else {
          targetTime.before(endTime) && targetTime.after(startTime)
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
      return false
    }

    fun isDeliverCanBeToday(
      entTime: Int
    ): Boolean {
      try {
//        val endCal = getCurrentDateAndTime()
//        endCal.set(Calendar.HOUR, entTime)
        val targetTime = getCurrentDateAndTime()
        com.lifepharmacy.application.utils.universal.Logger.d(
          "CurrentHour",
          targetTime.get(Calendar.HOUR_OF_DAY).toString()
        )
        com.lifepharmacy.application.utils.universal.Logger.d("endHour", entTime.toString())
        if (targetTime.get(Calendar.HOUR_OF_DAY) < entTime) {
          return true
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
      return false
    }

    fun getTimeDiffInMils(entTime: Int): Long {
      val endCal = Calendar.getInstance()
      endCal.set(Calendar.HOUR_OF_DAY, 0)
      endCal.add(Calendar.HOUR_OF_DAY, entTime)
      val timeMiles = Calendar.getInstance()
      com.lifepharmacy.application.utils.universal.Logger.d(
        "CurrentHour",
        timeMiles.get(Calendar.HOUR_OF_DAY).toString()
      )
      com.lifepharmacy.application.utils.universal.Logger.d(
        "endHour",
        endCal.get(Calendar.HOUR_OF_DAY).toString()
      )
      com.lifepharmacy.application.utils.universal.Logger.d("entTime", entTime.toString())
      com.lifepharmacy.application.utils.universal.Logger.d(
        "Differenc",
        (endCal.timeInMillis - timeMiles.timeInMillis).toString()
      )
      com.lifepharmacy.application.utils.universal.Logger.d(
        "endMils",
        (endCal.timeInMillis).toString()
      )
      com.lifepharmacy.application.utils.universal.Logger.d(
        "currentMils",
        (timeMiles.timeInMillis).toString()
      )
      val endDate = Date(endCal.timeInMillis)
      val currentDate = Date(timeMiles.timeInMillis)
      com.lifepharmacy.application.utils.universal.Logger.d(
        "Difference",
        (endDate.time - currentDate.time).toString()
      )
      return endDate.time - currentDate.time
    }

    fun getTimeDiffInMilsFromToDates(startCalender: Calendar, endCalender: Calendar): Long {
//      val endCal = Calendar.getInstance()
      val endDate = Date(endCalender.timeInMillis)
      val currentDate = Date(startCalender.timeInMillis)
      return endDate.time - currentDate.time
    }

    fun getTimeDifferenceBtwTwoDates(date1: Date, date2: Date): Long {
      val diff: Long = date2.time - date1.time
      val seconds = diff / 1000
      return seconds / 60
    }

    fun getTimeDifferenceBtwTwoDatesInHorse(date1: Date, date2: Date): Long {
      val diff: Long = date2.time - date1.time
      val seconds = diff / 1000
      val minutes = seconds / 60
      val hours = minutes / 60
      return hours
    }

    fun getTimeDiffInMilsFromToDatesWithMinusOneMin(
      startCalender: Calendar,
      endCalender: Calendar
    ): Long {
//      val endCal = Calendar.getInstance()
      endCalender.add(Calendar.MINUTE, -1)
      val endDate = Date(endCalender.timeInMillis)
      val currentDate = Date(startCalender.timeInMillis)
      return endDate.time - currentDate.time
    }

    fun getGiveUTCTimeIsBeforeCurrent(date: String?): Boolean {
      var returnValue = false
      val giveDate = date?.let { getUTCStringToDate(it) }
      val currentDate = getCurrentDateObject()
      giveDate?.let {
        if (it < currentDate) {
          returnValue = true
        }
      }
      return returnValue
    }
  }

}