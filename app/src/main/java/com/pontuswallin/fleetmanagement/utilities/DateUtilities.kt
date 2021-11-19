package com.pontuswallin.fleetmanagement.utilities

import android.icu.util.TimeZone
import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.*

class DateUtilities {

    companion object {

        fun dateStringToDateObj(date: String): Date {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            return dateFormat.parse(date)
        }

        fun convertToLocalDate(dateToConvert: Date): LocalDate? {
            return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }

        fun convertToLocalDateTime(dateToConvert: Date): LocalDateTime? {
            return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }

        fun convertToDate(dateToConvert: LocalDate): Date? {
            return Date.from(
                dateToConvert.atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            )
        }

        fun parseDateString(dateString: String): Date? {
            val sdf = android.icu.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ")
            sdf.timeZone = TimeZone.getTimeZone("GMT")
            return sdf.parse(dateString)
        }

        fun createTimeAgo(timestamp: String): String {
            val sdf = android.icu.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ")
            sdf.timeZone = TimeZone.getTimeZone("GMT")
            try {
                val date = sdf.parse(timestamp)
                val previousTime = convertToLocalDateTime(date)
                val now = LocalDateTime.now()

                val diffInYears = now.year - previousTime!!.year
                val diffInMonths = now.monthValue - previousTime!!.monthValue
                val diffInDays = now.dayOfMonth - previousTime!!.dayOfMonth

                val diffInHours = now.hour - previousTime.hour
                val diffInMinutes = now.minute - previousTime.minute
                val diffInSeconds = now.second - previousTime.second

                if(diffInYears > 0) {
                    return getTimeAgo(
                        previousTime,
                        now,
                        DateUtils.WEEK_IN_MILLIS
                    )
                }
                if(diffInMonths > 0) {
                    val diffAbs = Math.abs(diffInDays)
                    return "$diffAbs days ago"
                }
                if(diffInDays > 0) {
                    return getTimeAgo(
                        previousTime,
                        now,
                        DateUtils.HOUR_IN_MILLIS
                    )
                }
                if(diffInHours > 0) {
                    return getTimeAgo(
                        previousTime,
                        now,
                        DateUtils.MINUTE_IN_MILLIS,
                    )
                }
                if(diffInMinutes > 0) {
                    return getTimeAgo(
                        previousTime,
                        now,
                        DateUtils.SECOND_IN_MILLIS
                    )
                }
                if(diffInSeconds > 0) {
                    return "$diffInSeconds sec ago"
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return "No timeago created!"
        }

        fun getSimpleDateStringWithSlashes(date: Date): String {
            return SimpleDateFormat("dd / MM / YYYY").format(date)
        }

        fun getSimpleDateStringWithDashes(date: Date): String {
            return SimpleDateFormat("dd-MM-YYYY").format(date)
        }

        private fun convertLocalDateToLong(localDateTime: LocalDateTime): Long {
            val zdt: ZonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault())
            return zdt.toInstant().toEpochMilli()
        }

        private fun getTimeAgo(time: LocalDateTime, now: LocalDateTime, format: Long): String {
            val timeAsLong = convertLocalDateToLong(time)
            val nowAsLong = convertLocalDateToLong(now)
            return DateUtils.getRelativeTimeSpanString(
                timeAsLong, nowAsLong, format, DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString()
        }
    }
}