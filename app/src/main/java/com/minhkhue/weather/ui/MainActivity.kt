package com.minhkhue.weather.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import com.minhkhue.weather.R
import com.minhkhue.weather.apdapter.DailyWeatherAdapter
import com.minhkhue.weather.databinding.ActivityMainBinding
import com.minhkhue.weather.model.Daily
import com.minhkhue.weather.utils.Status
import com.minhkhue.weather.utils.TimeUtils
import com.minhkhue.weather.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
	private lateinit var binding: ActivityMainBinding
	private val weatherViewModel: WeatherViewModel by viewModels()
	private var lat: Double = 0.0
	private var lon: Double = 0.0
	private var cityName: String = ""
	private lateinit var locationManager: LocationManager
	private lateinit var weatherAdapter: DailyWeatherAdapter
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		supportActionBar?.hide()
		getDeviceLocation()
		setupObserver()
	}
	
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == PERMISSION_REQUEST_LOCATION) {
			if (grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				getDeviceLocation()
				Snackbar.make(binding.root, "Permission granted", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
			} else {
				Snackbar.make(binding.root, "Permission not granted", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
			}
		}
	}
	
	private fun getDeviceLocation() {
		if (ActivityCompat.checkSelfPermission(
				this@MainActivity,
				Manifest.permission.ACCESS_FINE_LOCATION
			) ==
			PackageManager.PERMISSION_GRANTED &&
			ActivityCompat.checkSelfPermission(
				this@MainActivity,
				Manifest.permission.ACCESS_COARSE_LOCATION
			) ==
			PackageManager.PERMISSION_GRANTED
		) {
			locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
			locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				5000,
				0F
			) { location ->
				lat = location.latitude
				lon = location.longitude
				val geocoder = Geocoder(this, Locale.getDefault())
				val address: MutableList<Address> = geocoder.getFromLocation(lat, lon, 1)
				cityName = if (address[0].locality != null)
					address[0].locality
				else
					"_ _"
				binding.tvLocation.text = cityName
				weatherViewModel.getWeatherData(lat, lon)
			}
		} else {
			requestLocationPermission()
		}
	}
	
	private fun requestLocationPermission() {
		if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
			requestPermissions(
				arrayOf(
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
				),
				PERMISSION_REQUEST_LOCATION
			)
		} else {
			requestPermissions(
				arrayOf(
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
				),
				PERMISSION_REQUEST_LOCATION
			)
		}
	}
	
	private fun setupObserver() {
		weatherViewModel.responseWeatherData.observe(this, { response ->
			when (response.status) {
				Status.SUCCESS -> {
					binding.tvTemperature.text =
						StringBuilder(response.data?.current?.temp?.toInt().toString()).append(" ℃")
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
					setupUI()
					renderList(response.data.daily)
				}
				Status.LOADING -> {
				}
				Status.ERROR -> {
					Snackbar.make(binding.root, response.message.toString(), Snackbar.LENGTH_SHORT)
						.show()
				}
			}
			
		})
	}
	
	private fun renderList(daily: List<Daily>) {
		weatherAdapter.response = daily
	}
	
	private fun setupUI() {
		weatherAdapter = DailyWeatherAdapter()
		binding.rvDailyWeather.apply {
			adapter = weatherAdapter
			layoutManager =
				LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
			setHasFixedSize(true)
		}
	}
	
	companion object {
		const val PERMISSION_REQUEST_LOCATION = 9696
	}
}