package com.example.storage

import org.mongodb.scala._

object MongoClientProvider {
  val mongoClient: MongoClient = MongoClient("mongodb://localhost:27017")
  val database: MongoDatabase = mongoClient.getDatabase("weather")
}
