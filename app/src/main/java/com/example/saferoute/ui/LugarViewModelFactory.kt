package com.example.saferoute.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.saferoute.api.WeatherService

class LugarViewModelFactory(private val service: WeatherService) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LugarViewModel::class.java)) {
            return LugarViewModel(service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
