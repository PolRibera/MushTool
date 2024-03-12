package com.example.Projecte3MushTool.tiempo.model.forecast

import com.example.Projecte3MushTool.tiempo.model.weather.Clouds
import com.example.Projecte3MushTool.tiempo.model.weather.Main
import com.example.Projecte3MushTool.tiempo.model.weather.Sys
import com.example.Projecte3MushTool.tiempo.model.weather.Weather
import com.example.Projecte3MushTool.tiempo.model.weather.Wind
import com.google.gson.annotations.SerializedName

data class CustomList (
    @SerializedName("dt") var dt: Int? = null,
    @SerializedName("main") var main: Main? = Main(),
    @SerializedName("weather") var weather: List<Weather>? = arrayListOf(),
    @SerializedName("clouds") var clouds: Clouds? = Clouds(),
    @SerializedName("wind") var wind: Wind? = Wind(),
    @SerializedName("visibility") var visibility: Int? = null,
    @SerializedName("pop") var pop: Double? = null,
    @SerializedName("sys") var sys: Sys? = Sys(),
    @SerializedName("dt_txt") var dt_Txt: String? = null
)