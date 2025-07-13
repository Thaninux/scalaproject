package com.example.common

case class WeatherReading(
  city: String,
  timestamp: Long,
  temperature: Double,
  windSpeed: Double,
  humidity: Int
)

case class EnrichedWeatherReading(
  city: String,
  timestamp: Long,
  temperature: Double,
  windSpeed: Double,
  humidity: Int,
  heatIndex: Double // nouvel indice
)

case class WeatherAlert(
  city: String,
  timestamp: Long,
  alertType: String,
  message: String
)
