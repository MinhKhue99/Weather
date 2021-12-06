package com.minhkhue.weather.repository

import com.minhkhue.weather.api.ApiHelper
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val apiHelper: ApiHelper) {
	suspend fun getWeatherData(
		lat: Double,
		lon: Double,
	) = apiHelper.getWeatherData(lat, lon)
}