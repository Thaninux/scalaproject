package com.example.processing

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import com.example.common.{WeatherReading, EnrichedWeatherReading, WeatherAlert}
import org.apache.kafka.common.serialization.StringDeserializer
import io.circe.parser._
import io.circe.generic.auto._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

object WeatherProcessor extends App {
  implicit val system: ActorSystem = ActorSystem("WeatherProcessorSystem")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val kafkaBootstrapServers = "localhost:9092"
  val topic = "weather.readings"

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(kafkaBootstrapServers)
    .withGroupId("weather-processing-group")
    .withProperty("auto.offset.reset", "earliest")

  // Parsing JSON en WeatherReading
  def parseWeatherReading(jsonStr: String): Option[WeatherReading] = {
    decode[WeatherReading](jsonStr).toOption
  }

  val windowDuration = 5.minutes
  val slideEvery = 30.seconds

  val stream: Future[Done] = Consumer
    .plainSource(consumerSettings, akka.kafka.Subscriptions.topics(topic))
    .map(_.value)
    .map(parseWeatherReading)
    .collect { case Some(reading) => reading }
    .mapConcat { reading =>
      Enrichment.clean(reading).toList // nettoyage avancé
    }
    .map(Enrichment.enrich) // enrichissement (ajout heatIndex)
    .alsoTo(
      Flow[EnrichedWeatherReading]
        .mapConcat(reading => Alert.checkAlerts(reading).toList)
        .map { alert =>
          AlertPublisher.publishAlert(alert)
          alert
        }
        .to(Sink.ignore)
    )
    .groupBy(100, _.city)
    .groupedWithin(1000, windowDuration)
    .map { window =>
      val city = window.head.city
      val count = window.size
      val temps = window.map(_.temperature)
      val avgTemp = temps.sum / count.toDouble
      val minTemp = temps.min
      val maxTemp = temps.max
      val heatIndices = window.map(_.heatIndex)
      val avgHeatIndex = heatIndices.sum / count.toDouble

      (city, count, avgTemp, minTemp, maxTemp, avgHeatIndex)
    }
    .mergeSubstreams
    .runForeach {
      case (city, count, avgTemp, minTemp, maxTemp, avgHeatIndex) =>
        println(f"Window Stats - $city : count=$count, avgTemp=$avgTemp%.2f, minTemp=$minTemp%.2f, maxTemp=$maxTemp%.2f, avgHeatIndex=$avgHeatIndex%.2f")
    }

  sys.addShutdownHook {
    system.terminate()
  }
}
