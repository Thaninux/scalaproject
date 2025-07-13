package com.example.processing

import com.example.common.{WeatherReading, EnrichedWeatherReading}

object Enrichment {

  /** Calcul simple de l'indice de chaleur (heat index) approximatif */
  def calculateHeatIndex(temp: Double, humidity: Int): Double = {
    // Formule simplifiée en °C
    0.5 * (temp + 61.0 + ((temp - 68.0) * 1.2) + (humidity * 0.094))
  }

  /** Nettoyage avancé */
  def clean(reading: WeatherReading): Option[WeatherReading] = {
    val validTemp = reading.temperature >= -50 && reading.temperature <= 60
    val validHumidity = reading.humidity >= 0 && reading.humidity <= 100
    val validWindSpeed = reading.windSpeed >= 0 && reading.windSpeed <= 150

    if (validTemp && validHumidity && validWindSpeed) Some(reading)
    else None
  }

  /** Enrichissement en ajoutant l'indice de chaleur */
  def enrich(reading: WeatherReading): EnrichedWeatherReading = {
    val heatIndex = calculateHeatIndex(reading.temperature, reading.humidity)
    EnrichedWeatherReading(
      reading.city,
      reading.timestamp,
      reading.temperature,
      reading.windSpeed,
      reading.humidity,
      heatIndex
    )
  }
}
