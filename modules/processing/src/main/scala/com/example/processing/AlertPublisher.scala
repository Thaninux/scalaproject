package com.example.processing

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import com.example.common.WeatherAlert
import io.circe.syntax._
import io.circe.generic.auto._

object AlertPublisher {

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)
  val alertTopic = "weather.alerts"

  def publishAlert(alert: WeatherAlert): Unit = {
    val json = alert.asJson.noSpaces
    val record = new ProducerRecord[String, String](alertTopic, alert.city, json)
    producer.send(record)
    println(s"Alert sent to Kafka: $json")
  }

  sys.addShutdownHook {
    producer.close()
  }
}
