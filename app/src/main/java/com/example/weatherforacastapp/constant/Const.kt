package com.example.weatherforacastapp.constant

class Const {
    companion object {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        const val openWeatherMapApiKey = "c7ac52d12fd15101784454e4193fd0ef";

        const val colorBg1 = 0xff08203e;
        const val colorBg2 = 0xff557c93;

        const val LOADING = "loading..."
        const val NA = "N/A"
    }
}