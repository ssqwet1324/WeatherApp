package com.example.perfectweatherapp.extensions

import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.perfectweatherapp.Adapters.WeatherModel
import java.util.Locale
import com.example.perfectweatherapp.R
import com.squareup.picasso.Picasso

fun Fragment.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        activity as AppCompatActivity,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}
// function from formateDate
fun String.formatDateTime(inputPattern: String = "yyyy-MM-dd HH:mm", outputPattern: String = "EEE MMM d HH:mm"): String {
    return try {
        val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
        val outputFormat = SimpleDateFormat(outputPattern, Locale.ENGLISH)
        val date = inputFormat.parse(this)
        outputFormat.format(date)
    } catch (e: Exception) {
        this
    }
}
// from NewActivity and MainFragmnent
fun ImageView.setWeatherIcon(weather: WeatherModel) {
    val currentTime = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("HH", Locale.getDefault())
    val hour = dateFormat.format(currentTime).toInt()
    val isNight = hour in 0..5 || hour in 22..23

    val iconResource = if (weather.time.isNotEmpty()) {
        getWeatherIcon(weather.conditionWeather)
    } else {
        getWeatherIcon(weather.conditionWeather)
    }

    if (iconResource != R.drawable.ic_launcher_foreground) {
        this.setImageResource(iconResource)
    } else {
        Picasso.get().load("https:" + weather.icon).into(this)
    }
}
// from weather adapter
fun getWeatherIcon(condition: String, time: String = "10:00"): Int {
    val hour = time.split(":")[0].toInt()
    val isNight = hour in 0..5 || hour in 22..23

    return when {
        condition.contains("Partly Cloudy", ignoreCase = false) -> if (isNight) R.drawable.partly_cloudy_night else R.drawable.cloudy_sunny
        condition.contains("Cloudy", ignoreCase = false) -> if (isNight) R.drawable.cloudy_night else R.drawable.cloudy
        condition.contains("rain", ignoreCase = true) -> if (isNight) R.drawable.rainy_night else R.drawable.rainy
        condition.contains("snow", ignoreCase = true) -> if (isNight) R.drawable.snowy_night else R.drawable.snowy
        condition.contains("sun", ignoreCase = true) -> R.drawable.sunny
        condition.contains("Clear", ignoreCase = false) -> R.drawable.clear
        condition.contains("thunder", ignoreCase = true) -> if (isNight) R.drawable.thunder_night else R.drawable.thunder
        condition.contains("Freezing drizzle", ignoreCase = false) -> R.drawable.freezing_drizzle
        condition.contains("Ice pellets", ignoreCase = false) -> if (isNight) R.drawable.storm else R.drawable.storm
        condition.contains("Overcast", ignoreCase = false) -> if (isNight) R.drawable.partly_cloudy_night else R.drawable.cloudy_sunny
        condition.contains("light drizzle", ignoreCase = true) -> if (isNight) R.drawable.rainy_night else R.drawable.rainy
        condition.contains("Fog", ignoreCase = false) -> R.drawable.fog
        condition.contains("Mist", ignoreCase = false) -> R.drawable.fog
        condition.contains("Blizzard", ignoreCase = false) -> if (isNight) R.drawable.snowy_night else R.drawable.snowy
        condition.contains("Sleet", ignoreCase = true) -> if (isNight) R.drawable.snowy_night else R.drawable.snowy
        else -> R.drawable.ic_launcher_foreground // Значение по умолчанию, если не совпадает ни с одним из условий
    }
}