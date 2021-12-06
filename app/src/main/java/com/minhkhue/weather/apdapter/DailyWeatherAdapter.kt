package com.minhkhue.weather.apdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.minhkhue.weather.R
import com.minhkhue.weather.databinding.ItemDailyWeatherBinding
import com.minhkhue.weather.model.Daily
import com.minhkhue.weather.utils.TimeUtils

class DailyWeatherAdapter : RecyclerView.Adapter<DailyWeatherAdapter.WeatherViewHolder>() {
	inner class WeatherViewHolder(val binding: ItemDailyWeatherBinding) :
		RecyclerView.ViewHolder(binding.root)
	
	private val diffCallback = object : DiffUtil.ItemCallback<Daily>() {
		override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
			return oldItem.dt == newItem.dt
		}
		
		override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
			return oldItem == newItem
		}
	}
	
	private val differ = AsyncListDiffer(this, diffCallback)
	var response: List<Daily>
		get() = differ.currentList
		set(value) {
			differ.submitList(value)
		}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
		return WeatherViewHolder(
			ItemDailyWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		)
	}
	
	override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
		val currentResponse = response[position]
		holder.binding.apply {
			tvDate.text = TimeUtils.convertWeekDays(currentResponse.dt)
			tvMaxTemp.text = StringBuilder(currentResponse.temp.max.toString()).append(" ℃")
			tvMinTemp.text = StringBuilder(currentResponse.temp.min.toString()).append(" ℃")
			ivIcon.load("https://openweathermap.org/img/w/" + currentResponse.weather[0].icon + ".png") {
				crossfade(true)
				placeholder(R.drawable.weather)
				transformations(CircleCropTransformation())
			}
		}
	}
	
	override fun getItemCount(): Int = response.size
}