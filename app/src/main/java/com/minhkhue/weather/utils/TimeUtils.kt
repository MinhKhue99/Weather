package com.minhkhue.weather.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
	@SuppressLint("SimpleDateFormat")
	fun convertTime(timeZone: String, time: Long): String {
		val hour: String
		val simpleDateFormat = SimpleDateFormat("h:mm aa")
		simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
		hour = simpleDateFormat.format(Date(time))
		return hour
	}
	
	fun convertWeekDays(dt: Long): String {
		val weekDay: String
		val days = arrayOf(
			"Sunday",
			"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
		)
		val d = Date(dt * 1000)
		weekDay = days[d.day]
		return weekDay
	}
}