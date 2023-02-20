package com.example.myapplication.Classes

import kotlin.math.roundToInt

class Utils {
    companion object{

        @JvmStatic
        fun getTimeStringFromDoble(time: Double): String {
            val resultInt = time.roundToInt()
            val hours = resultInt % 86400 / 3600
            val minutes = resultInt % 86400 % 3600 /60
            val seconds = resultInt % 86400 % 3600 % 60

            return makeTimeString(hours,minutes,seconds)
        }

        private fun makeTimeString(hour: Int, min: Int, sec: Int): String = String.format("%02d:%02d:%02d", hour, min, sec)

    }

}