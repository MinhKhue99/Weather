package com.minhkhue.weather.api

import com.minhkhue.weather.model.WeatherResponse
import com.minhkhue.weather.utils.Constants
import com.minhkhue.weather.utils.Constants.END_POINT
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
	@GET(END_POINT)
	suspend fun getWeatherData(
		@Query("lat") lat: Double,
		@Query("lon") lon: Double,
		@Query("units") units: String = Constants.UNITS,
		@Query("appid") key: String = Constants.API_KEY
	): Response<WeatherResponse>
}