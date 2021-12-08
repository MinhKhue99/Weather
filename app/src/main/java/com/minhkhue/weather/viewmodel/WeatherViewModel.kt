package com.minhkhue.weather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhkhue.weather.model.WeatherResponse
import com.minhkhue.weather.repository.WeatherRepository
import com.minhkhue.weather.utils.NetworkHelper
import com.minhkhue.weather.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
	private val weatherRepository: WeatherRepository,
	private val networkHelper: NetworkHelper
) : ViewModel() {
	
	private val _response = MutableLiveData<Resource<WeatherResponse>>()
	val responseWeatherData: LiveData<Resource<WeatherResponse>> get() = _response
	
	fun getWeatherData(latitude: Double, longitude: Double) = viewModelScope.launch {
		_response.postValue(Resource.loading(null))
		if (networkHelper.isNetworkConnected()) {
			weatherRepository.getWeatherData(latitude, longitude).let {
				if (it.isSuccessful) {
					_response.postValue(Resource.success(it.body()))
				} else {
					_response.postValue(Resource.error(it.message().toString(), null))
				}
			}
		} else {
			_response.postValue(Resource.error("No Internet connected", null))
		}
	}
}