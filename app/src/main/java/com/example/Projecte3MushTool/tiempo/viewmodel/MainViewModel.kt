package com.example.Projecte3MushTool.tiempo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Projecte3MushTool.tiempo.model.MyLatLng
import com.example.Projecte3MushTool.tiempo.model.forecast.ForecastResult
import com.example.Projecte3MushTool.tiempo.model.weather.WeatherResult
import com.example.Projecte3MushTool.tiempo.network.RetrofitClient
import kotlinx.coroutines.launch

enum class STATE {
    LOADING,
    SUCCES,
    FAILED
}

class MainViewModel : ViewModel() {
    var state by mutableStateOf(STATE.LOADING)

    var weatherResponse: WeatherResult by mutableStateOf(WeatherResult())
    var forecastResponse: ForecastResult by mutableStateOf(ForecastResult())

    var errorMessage: String by mutableStateOf("")

    fun getWeatherByLocation(latLng: MyLatLng) {
        viewModelScope.launch {

            state = STATE.LOADING
            val apiService = RetrofitClient.getInstance()
            try {
                val apiResponse = apiService.getWeather(latLng.lat, latLng.lng)
                weatherResponse = apiResponse
                state = STATE.SUCCES

            } catch (e: Exception) {
                errorMessage = e.message.toString()
                state = STATE.FAILED
            }
        }
    }

    fun getForecastByLocation(latLng: MyLatLng) {
        viewModelScope.launch {

            state = STATE.LOADING
            val apiService = RetrofitClient.getInstance()
            try {
                val apiResponse = apiService.getForecast(latLng.lat, latLng.lng)
                forecastResponse = apiResponse
                state = STATE.SUCCES

            } catch (e: Exception) {
                errorMessage = e.message.toString()
                state = STATE.FAILED
            }
        }
    }


}