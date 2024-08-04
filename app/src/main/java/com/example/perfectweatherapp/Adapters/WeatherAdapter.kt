package com.example.perfectweatherapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectweatherapp.R
import com.example.perfectweatherapp.databinding.ListItemBinding
import com.example.perfectweatherapp.extensions.getWeatherIcon
import com.squareup.picasso.Picasso
// тут наш list_item мы обновляем
class WeatherAdapter : ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListItemBinding.bind(view)

        fun bind(item: WeatherModel) = with(binding) {
            tempByHours.text = item.currentTemp
            timeHours.text = item.time

            val iconResource = if (item.time.isNotEmpty()) {
                getWeatherIcon(item.conditionWeather, item.time)
            } else {
                getWeatherIcon(item.conditionWeather)
            }

            if (iconResource != R.drawable.ic_launcher_foreground) {
                binding.pictureByHours.setImageResource(iconResource)
            } else {
                Picasso.get().load("https:" + item.icon).into(pictureByHours)
            }
        }
    } // подключаем картинку к погоде по часам

    class Comparator : DiffUtil.ItemCallback<WeatherModel>() {
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }
    } // обновляем карточки с погодой по часам

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}
