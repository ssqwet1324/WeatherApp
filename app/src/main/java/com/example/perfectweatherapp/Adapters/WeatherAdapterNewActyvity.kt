package com.example.perfectweatherapp.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectweatherapp.databinding.ListItemDaysBinding
import com.example.perfectweatherapp.extensions.formatDateTime
import com.example.perfectweatherapp.extensions.getWeatherIcon
import kotlin.math.roundToInt

class WeatherAdapterNewActivity : ListAdapter<WeatherModel, WeatherAdapterNewActivity.Holder>(WeatherDiffCallback()) {

    class Holder(private val binding: ListItemDaysBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: WeatherModel) = with(binding) {
            daysTxt.text = item.time.formatDateTime(inputPattern = "yyyy-MM-dd", outputPattern = "EEE")
            nameWeather.text = item.conditionWeather
            minTempDays.text = "${item.minTemp.toDouble().roundToInt()}°"
            maxTempDays.text = "${item.maxTemp.toDouble().roundToInt()}°"
            imgWeather.setImageResource(getWeatherIcon(item.conditionWeather))
        }
    } // заполняем список по дням

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ListItemDaysBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}

class WeatherDiffCallback : DiffUtil.ItemCallback<WeatherModel>() {
    override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
        // Check unique identifier (if you have one)
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
        return oldItem == newItem
    }
} // обновляем списки с погодой по дням
