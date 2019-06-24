package com.knoldus.streamapp

import java.util.Properties

import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.knoldus.streamapp.SchemaRegistry.SchemaConfig

class Producer  {

  implicit val system: ActorSystem = ActorSystem("PlainSinkProducerMain")

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val schema_registry_host = SchemaConfig.getString("host")

  val schema_registry_port = SchemaConfig.getInt("port")

  val schema_registry_url = s"http://$schema_registry_host:$schema_registry_port"

  val props = new Properties()

  props.put("bootstrap.servers", "localhost:9092")

  props.put("schema.registry.url", schema_registry_url)

  props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")

  props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")


  val producer = new KafkaProducer[String, GenericRecord](props)

  def sendWithTopic(key: String, msg: GenericRecord, topicName: String): RecordMetadata = {

    try {

      val record = new ProducerRecord(topicName, key, msg)

      val ack = producer.send(record).get()

      ack

    } catch {

      case e: Throwable =>

        throw e

    }

  }
}
