package com.example.Projecte3MushTool.tiempo.model.weather

import com.google.gson.annotations.SerializedName

data class Wind (
    @SerializedName("speed") var speed: Double? = null,
    @SerializedName("deg") var deg: Int? = null
)