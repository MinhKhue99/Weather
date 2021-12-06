package com.minhkhue.weather.api

import com.minhkhue.weather.model.WeatherResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {
	override suspend fun getWeatherData(lat: Double, lon: Double): Response<WeatherResponse> =
		apiService.getWeatherData(lat, lon)
}