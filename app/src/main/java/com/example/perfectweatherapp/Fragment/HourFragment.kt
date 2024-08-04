package com.example.perfectweatherapp.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perfectweatherapp.Adapters.WeatherAdapter
import com.example.perfectweatherapp.Adapters.WeatherModel
import com.example.perfectweatherapp.MainViewModel
import com.example.perfectweatherapp.databinding.FragmentHourBinding
import com.example.perfectweatherapp.extensions.formatDateTime
import org.json.JSONArray

class HourFragment : Fragment() {
    private lateinit var binding: FragmentHourBinding
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHourBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        model.liveDataCurrent.observe(viewLifecycleOwner) { weatherModel ->
            adapter.submitList(getHoursList(weatherModel))
        }
    } // подключаем модел

    private fun initRecyclerView() = with(binding) {
        rcViewByHours.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        adapter = WeatherAdapter()
        rcViewByHours.adapter = adapter
    } // подключаем адаптер

    private fun getHoursList(item: WeatherModel): List<WeatherModel> {
        val hourArray = JSONArray(item.hours)
        return List(hourArray.length()) { i ->
            hourArray.getJSONObject(i).let { hourObject ->
                val timeString = hourObject.getString("time")
                WeatherModel(
                    city = item.city,
                    time = timeString.formatDateTime(inputPattern = "yyyy-MM-dd HH:mm", outputPattern = "HH:mm"),
                    conditionWeather = hourObject.getJSONObject("condition").getString("text"),
                    currentTemp = "${Math.round(hourObject.getString("temp_c").toDouble())}°",
                    "",
                    "",
                    "",
                    "",
                    "",
                    hourObject.getJSONObject("condition").getString("icon"),
                    timeString,
                    ""
                )
            }
        }
    } // получаем погоду по часам
    companion object {
        @JvmStatic
        fun newInstance() = HourFragment()
    }
}
