package com.minhkhue.weather.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.minhkhue.weather.R
import com.minhkhue.weather.databinding.ActivityMainBinding
import com.minhkhue.weather.utils.Status
import com.minhkhue.weather.utils.TimeUtils
import com.minhkhue.weather.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
	private lateinit var binding: ActivityMainBinding
	private val weatherViewModel: WeatherViewModel by viewModels()
	private var latitude: Double = 0.0
	private var longitude: Double = 0.0
	private var cityName: String = ""
	private var fusedLocationProvider: FusedLocationProviderClient? = null
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
			== PackageManager.PERMISSION_GRANTED
		) {
			//When permission granted
			getDeviceLocation()
		} else {
			//when permission denied
			ActivityCompat.requestPermissions(
				this,
				arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				69
			)
		}
		setupObserver()
	}
	
	private fun getDeviceLocation() {
		if (ActivityCompat.checkSelfPermission(
				this,
				Manifest.permission.ACCESS_FINE_LOCATION
			) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
				this,
				Manifest.permission.ACCESS_COARSE_LOCATION
			) != PackageManager.PERMISSION_GRANTED
		) {
			
			return
		}
		fusedLocationProvider?.lastLocation?.addOnCompleteListener {
			val location: Location = it.result
			val geocoder = Geocoder(this, Locale.getDefault())
			try {
				val address: MutableList<Address> =
					geocoder.getFromLocation(location.latitude, location.longitude, 1)
				latitude = address[0].latitude
				longitude = address[0].longitude
				cityName = address[0].locality
				weatherViewModel.getWeatherData(latitude, longitude)
			} catch (ex: IOException) {
				ex.printStackTrace()
			}
		}
	}
	
	private fun setupObserver() {
		weatherViewModel.responseWeatherData.observe(this, { response ->
			when (response.status) {
				Status.SUCCESS -> {
					binding.tvLocation.text = cityName
					binding.tvTemperature.text =
						StringBuilder(response.data?.current?.temp.toString()).append(" ℃")
					binding.tvHumidity.text =
						StringBuilder(response.data?.current?.humidity.toString()).append(" %")
					binding.tvStatus.text = response?.data?.current?.weather?.get(0)?.description
					binding.imgIcon.load(
						"https://openweathermap.org/img/w/" + response?.data?.current?.weather?.get(
							0
						)?.icon + ".png"
					) {
						crossfade(true)
						placeholder(R.drawable.weather)
						transformations(CircleCropTransformation())
					}
					binding.tvWindSpeed.text =
						StringBuilder(response.data?.current?.wind_speed.toString()).append(" m/s")
					binding.tvSunrise.text = TimeUtils.convertTime(
						response.data!!.timezone,
						response.data.current.sunrise
					)
					
					binding.tvSunset.text = TimeUtils.convertTime(
						response.data.timezone,
						response.data.current.sunset
					)
				}
				Status.LOADING -> {}
				Status.ERROR -> {
					Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
				}
			}
			
		})
	}
	
}