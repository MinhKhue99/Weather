package com.minhkhue.weather.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
	fun convertTime(timeZone: String, time: Long): String {
		val hour: String
		val simpleDateFormat = SimpleDateFormat("h:mm aa")
		simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
		hour = simpleDateFormat.format(Date(time))
		return hour
	}
	fun convertWeekDays(dt:Long):String{
		val weekDay:String
		val simpleDateFormat = SimpleDateFormat("EEE")
		weekDay = simpleDateFormat.format(dt)
		return weekDay
	}
}