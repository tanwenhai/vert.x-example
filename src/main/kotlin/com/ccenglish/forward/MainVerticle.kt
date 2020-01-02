package com.ccenglish.forward

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.kafka.client.consumer.KafkaConsumer

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)
    router.route().handler(CorsHandler.create("*").allowedMethods(HttpMethod.values().toSet()))
    var config = mutableMapOf<String, Any?>()
    config["bootstrap.servers"] = "localhost:9092"
    println(org.apache.kafka.common.serialization.StringDeserializer::class.java.name)
    config["key.deserializer"] = org.apache.kafka.common.serialization.StringDeserializer::class.java.name
    config["value.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer"
    config["group.id"] = "my_group"
    config["auto.offset.reset"] = "earliest"
    config["enable.auto.commit"] = "true"

    // use consumer for interacting with Apache Kafka
    var consumer = KafkaConsumer.create<Any, Any>(vertx, config)
    router.route().handler { routingContext ->
      val response = routingContext.response()
      response.putHeader("content-type", "text/plain")
      response.end("Hello World from Vert.x-Web!")
    }

    server.requestHandler(router).listen(8080)
  }
}
