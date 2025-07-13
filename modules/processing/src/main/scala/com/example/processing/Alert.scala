package com.example.processing

import com.example.common.{EnrichedWeatherReading, WeatherAlert}

object Alert {

  /** Règles simples d’alerte */
  def checkAlerts(reading: EnrichedWeatherReading): Option[WeatherAlert] = {
    if (reading.temperature > 35) {
      Some(WeatherAlert(
        reading.city,
        reading.timestamp,
        "HighTemperature",
        s"Température élevée détectée : ${reading.temperature} °C"
      ))
    } else if (reading.heatIndex > 33) {
      Some(WeatherAlert(
        reading.city,
        reading.timestamp,
        "HighHeatIndex",
        f"Indice de chaleur élevé : ${reading.heatIndex}%.2f"
      ))
    } else None
  }
}
