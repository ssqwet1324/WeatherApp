package com.example.perfectweatherapp.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.perfectweatherapp.Adapters.WeatherModel
import com.example.perfectweatherapp.Dialog.DialogManager
import com.example.perfectweatherapp.Extentions.Utf8StringRequest
import com.example.perfectweatherapp.MainViewModel
import com.example.perfectweatherapp.R
import com.example.perfectweatherapp.databinding.FragmentMainBinding
import com.example.perfectweatherapp.extensions.formatDateTime
import com.example.perfectweatherapp.extensions.isPermissionGranted
import com.example.perfectweatherapp.extensions.setWeatherIcon
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.math.roundToInt

private const val API_KEY = "Input your API KEY"

class MainFragment : Fragment() {
    private lateinit var fLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    } // подключаем биндинг

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionListener()
        checkPermission()
        showHourFragment()
        init()
        updateCurrentCard()
        binding.btnNext7Days.setOnClickListener {
            showDaysFragment()
        }
    }
    // инициализируем функции

    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(context, "Permission done", Toast.LENGTH_SHORT).show()
        }
    } // слушатель доступа

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    } // проверяем дан доступ к гпс

    private fun init() {
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLocation()
    } // подключаем fLocationClient и запускаем getLocation

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    private fun checkLocation(){
        if (isLocationEnabled()){
            getLocation()
        } else {
            DialogManager.locationServicesDialog(requireContext(), object : DialogManager.Listener{
                override fun OnClick() {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    } // интерфейс

    private fun isLocationEnabled(): Boolean {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    getWeatherData("${location.latitude}, ${location.longitude}")
                } else {
                    // Обработка ошибки получения локации
                    Toast.makeText(requireContext(), "Не удалось получить местоположение", Toast.LENGTH_SHORT).show()
                    Log.e("MainFragment", "Error getting location: ${task.exception}")
                }
            }
    } // берем гпс позицию

    private fun showDaysFragment() {
        val newWindow = NewActivity.newInstance()
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
            .add(android.R.id.content, newWindow, "NewActivity")
            .addToBackStack(null)
            .commit()
    } // инициализируем показ новомго фрагмента

    private fun showHourFragment() {
        val hourFragment = HourFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, hourFragment)
            .commit()
    } // показываем по часам

    @SuppressLint("SetTextI18n")
    private fun updateCurrentCard() = with(binding) {
        model.liveDataCurrent.observe(viewLifecycleOwner) { weather ->
            tvNameWeather.text = weather.conditionWeather
            tvData.text = (weather.time).formatDateTime()
            tvCurrentTemp.text = "${weather.currentTemp.toDouble().roundToInt()}°"
            tvNameCity.text = weather.city
            tvRainPercent.text = "${weather.rainChance}%"
            tvSpeedWind.text = "${weather.windSpeed} km/h"
            tvHumindityPercent.text = "${weather.humandity}%"
            ivWeatherIcon.setWeatherIcon(weather)
        }
    } // заполняем основную карточку
    
    private fun getWeatherData(coordinate: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=${API_KEY}&q=${coordinate}&days=7&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = Utf8StringRequest(
            Request.Method.GET,
            url,
            { result ->
                Log.d("MyLog", "Weather API response: $result")
                lifecycleScope.launch(Dispatchers.IO) {
                    parseWeatherData(result)
                }
            },
            { error ->
                Log.d("MyLog", "Error ${error}")
                Toast.makeText(context, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)
    } // получаем данные с апи

    private suspend fun parseWeatherData(result: String) = withContext(Dispatchers.Main) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
    }// запускаем функции парсинга

    private fun parseDays(mainObject: JSONObject): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c"),
                day.getJSONObject("day").getString("mintemp_c"),
                day.getJSONObject("day").getString("daily_chance_of_rain"),
                day.getJSONObject("day").getString("maxwind_kph"),
                day.getJSONObject("day").getString("avghumidity"),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                "",
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    } // парсим данные по дням

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel) {
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c"),
            "",
            "",
            weatherItem.rainChance,
            mainObject.getJSONObject("current").getString("wind_kph"),
            mainObject.getJSONObject("current").getString("humidity"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            "",
            weatherItem.hours
        )
        model.liveDataCurrent.value = item
    } // парсим данные по часам

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
