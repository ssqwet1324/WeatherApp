package com.example.perfectweatherapp.Adapters

import android.service.notification.Condition

data class WeatherModel(
    val city: String, // город
    val time: String, // время обновления
    val conditionWeather: String, // текущая погода
    val currentTemp: String, // текущая температура
    val maxTemp: String,
    val minTemp: String,
    val rainChance: String, // шанс дождя мб уюеру
    val windSpeed: String, // скорость ветра
    val humandity: String, // влажность
    val icon: String,
    val weatherTime: String,
    val hours: String // погода по часам
) // модель для заполнения апи
