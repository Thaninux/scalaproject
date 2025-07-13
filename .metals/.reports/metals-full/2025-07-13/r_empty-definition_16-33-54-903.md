error id: file:///C:/Users/ninag/OneDrive/Documents/M1/scala_project/modules/processing/src/main/scala/com/example/processing/WeatherProcessor.scala:`<none>`.
file:///C:/Users/ninag/OneDrive/Documents/M1/scala_project/modules/processing/src/main/scala/com/example/processing/WeatherProcessor.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -java/time/Duration.
	 -java/time/Duration#
	 -java/time/Duration().
	 -scala/jdk/CollectionConverters.Duration.
	 -scala/jdk/CollectionConverters.Duration#
	 -scala/jdk/CollectionConverters.Duration().
	 -Duration.
	 -Duration#
	 -Duration().
	 -scala/Predef.Duration.
	 -scala/Predef.Duration#
	 -scala/Predef.Duration().
offset: 331
uri: file:///C:/Users/ninag/OneDrive/Documents/M1/scala_project/modules/processing/src/main/scala/com/example/processing/WeatherProcessor.scala
text:
```scala
package com.example.processing

import com.example.common.{WeatherReading, WeatherAggregate}
import com.example.storage.MongoWriter
import org.apache.kafka.clients.consumer.{KafkaConsumer, ConsumerRecords}
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json.Json

import java.time.{Dur@@ation, Instant, ZoneOffset, ZonedDateTime}
import java.util.{Collections, Properties}
import scala.jdk.CollectionConverters._

object WeatherProcessor {

  def main(args: Array[String]): Unit = {
    val consumerProps = new Properties()
    consumerProps.put("bootstrap.servers", "localhost:9092")
    consumerProps.put("group.id", "weather-group")
    consumerProps.put("key.deserializer", classOf[StringDeserializer].getName)
    consumerProps.put("value.deserializer", classOf[StringDeserializer].getName)
    consumerProps.put("auto.offset.reset", "earliest")

    val consumer = new KafkaConsumer[String, String](consumerProps)
    consumer.subscribe(Collections.singletonList("weather-data"))

    val mongoWriter = new MongoWriter()

    while (true) {
      val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofSeconds(5))
      val readings = records.asScala.flatMap { record =>
        Json.parse(record.value()).asOpt[WeatherReading]
      }.toList

      val grouped = readings.groupBy(reading => roundToHour(reading.timestamp))

      grouped.foreach { case (hour, hourlyReadings) =>
        val avgTemp = hourlyReadings.map(_.temperature).sum / hourlyReadings.size
        val avgWind = hourlyReadings.map(_.windSpeed).sum / hourlyReadings.size
        val totalRain = hourlyReadings.map(_.rain).sum

        val anomalies = detectAnomalies(avgTemp, totalRain)

        val aggregate = WeatherAggregate(
          hour = hour.toString,
          averageTemperature = avgTemp,
          averageWindSpeed = avgWind,
          totalRain = totalRain,
          anomalies = anomalies
        )

        mongoWriter.writeAggregate(aggregate)
        println(s"[✔] Aggregate saved for $hour with ${anomalies.size} anomaly(-ies).")
      }
    }
  }

  def roundToHour(timestamp: String): ZonedDateTime = {
    val instant = Instant.parse(timestamp)
    ZonedDateTime.ofInstant(instant, ZoneOffset.UTC).withMinute(0).withSecond(0).withNano(0)
  }

  def detectAnomalies(temp: Double, rain: Double): List[String] = {
    var list = List[String]()
    if (temp > 40) list ::= "High temperature"
    if (rain > 50) list ::= "Heavy rainfall"
    list
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.