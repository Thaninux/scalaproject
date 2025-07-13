package com.example.ingestion

import org.apache.kafka.clients.consumer.{KafkaConsumer, ConsumerConfig}
import java.time.Duration
import java.util.{Collections, Properties}
import scala.jdk.CollectionConverters._
import io.circe.parser._
import io.circe.generic.auto._
import com.example.common.WeatherReading

object WeatherConsumer extends App {

  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "weather-consumer-group")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val consumer = new KafkaConsumer[String, String](props)
  consumer.subscribe(Collections.singletonList("weather.readings"))

  println("WeatherConsumer is now listening on topic 'weather.readings'...")

  while (true) {
    val records = consumer.poll(Duration.ofSeconds(1))
    for (record <- records.asScala) {
      val json = record.value()
      parse(json).flatMap(_.as[WeatherReading]) match {
        case Right(reading) =>
          println(s"Received: $reading")
        case Left(error) =>
          println(s"Failed to parse: $error")
      }
    }
  }

  sys.addShutdownHook {
    consumer.close()
    println("Consumer shut down.")
  }
}
