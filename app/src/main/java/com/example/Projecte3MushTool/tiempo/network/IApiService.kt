package com.example.Projecte3MushTool.tiempo.network

import com.example.Projecte3MushTool.tiempo.constant.Const.Companion.openWeatherMapKey
import com.example.Projecte3MushTool.tiempo.model.forecast.ForecastResult
import com.example.Projecte3MushTool.tiempo.model.weather.WeatherResult
import retrofit2.http.GET
import retrofit2.http.Query

interface IApiService {

    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double = 0.0,
        @Query("lon") lng: Double = 0.0,
        @Query("units") units: String = "metric",
        @Query("appid") appid: String = openWeatherMapKey


    ): WeatherResult

    @GET("forecast") suspend fun getForecast(
        @Query("lat") lat: Double = 0.0,
        @Query("lon") lng: Double = 0.0,
        @Query("units") units: String = "metric",
        @Query("appid") appid: String = openWeatherMapKey


    ): ForecastResult
}