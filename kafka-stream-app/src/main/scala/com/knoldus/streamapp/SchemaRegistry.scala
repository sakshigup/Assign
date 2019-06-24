package com.knoldus.streamapp

import com.typesafe.config.{Config, ConfigFactory}
object SchemaRegistry {

  val SchemaConfig: Config = ConfigFactory.load()

  val MAX_SUBJECTS_IN_TOPIC = 1000

  val schema_registry_host = SchemaConfig.getString("host")

  val schema_registry_port = SchemaConfig.getInt("port")

  val schema_registry_url = s"http://$schema_registry_host:$schema_registry_port"

  val props: Map[String, String] = Map[String, String](

    "key.subject.name.strategy" -> "io.confluent.kafka.serializers.subject.TopicRecordNameStrategy",

    "value.subject.name.strategy" -> "io.confluent.kafka.serializers.subject.TopicRecordNameStrategy",

    "schema.registry.url" -> schema_registry_url)
}
