package com.example.ingestion

import com.example.common.WeatherReading
import sttp.client3._
import sttp.client3.akkahttp.AkkaHttpBackend
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._

import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.{Future, ExecutionContextExecutor}

import java.util.{Properties, UUID}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object WeatherStreamer extends App {
  implicit val system: ActorSystem = ActorSystem("WeatherSystem")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val backend: SttpBackend[Future, Any] = AkkaHttpBackend()

  val cities = List(
    ("Paris", "48.8566", "2.3522"),
    ("New York", "40.7128", "-74.0060"),
    ("Tokyo", "35.6895", "139.6917")
  )

  val apiKey = "4d299d8e74714ae8e59581a12a7d4afd" 
  val units = "metric"

  // Kafka config
  val kafkaProps = new Properties()
  kafkaProps.put("bootstrap.servers", "localhost:9092")
  kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](kafkaProps)

  def fetchWeather(city: String, lat: String, lon: String): Future[Unit] = {
    val url = uri"https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=$units&appid=$apiKey"

    basicRequest.get(url).send().map { response =>
      response.body match {
        case Right(jsonStr) =>
          val parsed = for {
            json <- parse(jsonStr)
            main = json.hcursor.downField("main")
            wind = json.hcursor.downField("wind")
            temp <- main.get[Double]("temp")
            humidity <- main.get[Int]("humidity")
            windSpeed <- wind.get[Double]("speed")
          } yield WeatherReading(city, System.currentTimeMillis(), temp, windSpeed, humidity)

          parsed match {
            case Right(reading) =>
              val json = reading.asJson.noSpaces
              val record = new ProducerRecord[String, String]("weather.readings", UUID.randomUUID().toString, json)
              producer.send(record)
              println(s"[✓] Sent to Kafka: $json")

            case Left(err) =>
              println(s"[!] JSON parsing error: $err")
          }

        case Left(err) =>
          println(s"[!] HTTP error: $err")
      }
    }
  }

  system.scheduler.scheduleAtFixedRate(0.seconds, 30.seconds) { () =>
    cities.foreach { case (name, lat, lon) =>
      fetchWeather(name, lat, lon)
    }
  }

  sys.addShutdownHook {
    producer.close()
    backend.close()
    system.terminate()
  }
}
