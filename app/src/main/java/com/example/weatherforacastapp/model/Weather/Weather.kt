package com.example.weatherforacastapp.model.Weather

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("main")
    var main: Int? = null,
    @SerializedName("description")
    var description: Int? = null,
    @SerializedName("icon")
    var icon: Int? = null
    )
