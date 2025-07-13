package com.example.storage

import com.example.common.WeatherAggregate
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.ReplaceOptions

import play.api.libs.json.Json

class MongoWriter {

  private val database = MongoClientProvider.client.getDatabase("weather-db")
  private val collection = database.getCollection("weather_aggregates")

  def writeAggregate(aggregate: WeatherAggregate): Unit = {
    val jsonString = Json.toJson(aggregate).toString()
    val document = Document(jsonString)

    val filter = Filters.eq("hour", aggregate.hour)
    val options = new ReplaceOptions().upsert(true)

    collection.replaceOne(filter, document, options).subscribe(
      _ => println(s"[MongoDB] ✅ Aggregate for ${aggregate.hour} written."),
      e => println(s"[MongoDB] ❌ Error: ${e.getMessage}")
    )
  }
}
