error id: file:///C:/Users/ninag/OneDrive/Documents/M1/scala_project/modules/ingestion/src/main/scala/com/example/ingestion/WeatherProducer.scala:`<none>`.
file:///C:/Users/ninag/OneDrive/Documents/M1/scala_project/modules/ingestion/src/main/scala/com/example/ingestion/WeatherProducer.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1628
uri: file:///C:/Users/ninag/OneDrive/Documents/M1/scala_project/modules/ingestion/src/main/scala/com/example/ingestion/WeatherProducer.scala
text:
```scala
package com.example.ingestion

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import com.example.common.model.WeatherReading
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import sttp.client3._
import sttp.client3.akkahttp.AkkaHttpBackend
import sttp.client3.UriContext

import java.util.{Properties, UUID}
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

object IngestionApp extends App {
  implicit val system: ActorSystem = ActorSystem("IngestionSystem")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val sttpBackend = AkkaHttpBackend()

  val apiKey = "4d299d8e74714ae8e59581a12a7d4afd" 
  val lat = "48.8566" // Paris
  val lon = "2.3522"
  val units = "metric"
  val url = uri"https://api.openweathermap.org/data/3.0/onecall?lat=$lat&lon=$lon&exclude=minutely,daily,alerts&units=$units&appid=$apiKey"

 
 case class WeatherReading(
    dt: Long,
    temp: Double,
    wind_speed: Double,
    humidity: Int,
    rain: Option[Map[String, Double]]
  )

basicRequest.get(url).send().map { response =>
    response.body match {
      case Right(body) =>
        // On récupère uniquement le champ "current"
        val parsed = for {
          json <- parse(body)
          current <- json.hcursor.downField("current").as[WeatherReading]
        } yield current

        parsed match {
          case Right(weather) =>
            println(s"Current weathe@@r: $weather")
          case Left(error) =>
            println(s"Erreur de parsing : $error")
        }

      case Left(error) =>
        println(s"Erreur HTTP: $error")
    }

    backend.close()
  }
}


//   val kafkaProps = new Properties()
//   kafkaProps.put("bootstrap.servers", "localhost:9092")
//   kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//   kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//   val producer = new KafkaProducer[String, String](kafkaProps)

//   val source: Source[WeatherReading, NotUsed] = Source.tick(1.second, 10.seconds, NotUsed).mapAsync(1) { _ =>
//     basicRequest.get(url).send().map { response =>
//       val json = response.body.getOrElse("")
//       parse(json).flatMap(_.hcursor.downField("current").as[WeatherReading]) match {
//         case Right(reading) => reading.copy(city = "Paris") // enrich manually
//         case Left(error) =>
//           println(s"[ERROR] Failed to parse weather data: $error")
//           null
//       }
//     }
//   }.filter(_ != null)

//   val kafkaSink = Sink.foreach[WeatherReading] { reading =>
//     val json = reading.asJson.noSpaces
//     val record = new ProducerRecord[String, String]("weather.readings", UUID.randomUUID().toString, json)
//     producer.send(record)
//     println(s"[✓] Sent reading to Kafka: $json")
//   }

//   val stream: Future[Done] = source.runWith(kafkaSink)
// }

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.