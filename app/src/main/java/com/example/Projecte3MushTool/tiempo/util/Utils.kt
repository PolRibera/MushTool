package com.example.Projecte3MushTool.tiempo.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class Utils {

    companion object {
        fun timestampToHumanDate(timestamp: Long, format: String): String {
            val sdf = SimpleDateFormat(format)
            return sdf.format(timestamp * 1000L)
        }

        fun buildIcon(icon: String, isBigSize : Boolean = true): String {
            return if (isBigSize){
                "https://openweathermap.org/img/wn/$icon@4x.png"
            }
            else {
                "https://openweathermap.org/img/wn/$icon.png"
            }
        }
    }
}