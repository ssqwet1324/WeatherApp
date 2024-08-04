package com.example.perfectweatherapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.perfectweatherapp.Adapters.WeatherModel

class MainViewModel: ViewModel(){
    val liveDataCurrent = MutableLiveData<WeatherModel>() // обновление основной части
    val liveDataList = MutableLiveData<List<WeatherModel>>() // обновление части по часам
} // списки для информации с апи