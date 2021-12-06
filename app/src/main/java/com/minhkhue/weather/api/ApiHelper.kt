package com.minhkhue.weather.api

import com.minhkhue.weather.model.WeatherResponse
import retrofit2.Response

interface ApiHelper {
	suspend fun getWeatherData(
		lat: Double,
		lon: Double,
	): Response<WeatherResponse>
}