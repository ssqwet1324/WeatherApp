package com.example.perfectweatherapp.Fragment

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.example.perfectweatherapp.Adapters.WeatherModel
import com.example.perfectweatherapp.MainViewModel
import com.example.perfectweatherapp.R
import com.example.perfectweatherapp.databinding.FragmentNewActivityBinding
import com.example.perfectweatherapp.extensions.setWeatherIcon
import com.squareup.picasso.Picasso
import java.util.Locale

class NewActivity : Fragment() {
    private var _binding: FragmentNewActivityBinding? = null
    private val binding get() = _binding!!
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewActivityBinding.inflate(inflater, container, false)
        loadDaysFragment()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateTommorowCard()
        binding.arrowBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    } // при нажатии на стрелочку закрываем фрагмент

    private fun loadDaysFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_days, DaysFragment.newInstance())
            .commit()
    } // подгружаем список по дням

    @SuppressLint("SetTextI18n")
    private fun updateTommorowCard() = with(binding) {
        model.liveDataList.observe(viewLifecycleOwner) { weatherList ->
            weatherList.getOrNull(1)?.let { tomorrowWeather ->
                tvTempTomorrow.text = "${Math.round(tomorrowWeather.maxTemp.toDouble())}°"
                tvConditionTommorw.text = tomorrowWeather.conditionWeather
                tvRainPercent.text = "${tomorrowWeather.rainChance}%"
                tvSpeedWind.text = "${tomorrowWeather.windSpeed} km/h"
                tvHumindityPercent.text = "${tomorrowWeather.humandity}%"
                imgWeatherr.setWeatherIcon(tomorrowWeather)
            }
        }
    } // заполняем карточку tommorow

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewActivity()
    }
}
