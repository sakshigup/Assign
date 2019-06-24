package com.knoldus.streamapp

import java.io.ByteArrayOutputStream
import java.util.Properties

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.io.{BinaryEncoder, EncoderFactory}
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.streams.StreamsConfig.{APPLICATION_ID_CONFIG, BOOTSTRAP_SERVERS_CONFIG}
import org.apache.kafka.streams.kstream.{KStreamBuilder, KeyValueMapper}
import org.apache.kafka.streams.{KafkaStreams, KeyValue}

import scala.io.Source

object StreamApplication {

  def main(args: Array[String]): Unit = {
    val streamsConfiguration = new Properties()

    streamsConfiguration.put(APPLICATION_ID_CONFIG, "StreamApplication")
    streamsConfiguration.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    streamsConfiguration.put("key.serializer", "com.knoldus.streamapp.CalenderSerializer")
    streamsConfiguration.put("value.serializer", "com.knoldus.streamapp.CalenderSerializer")
    streamsConfiguration.put("key.deserializer", "com.knoldus.streamapp.CalenderDeserializer")
    streamsConfiguration.put("value.deserializer", "com.knoldus.streamapp.CalenderDeserializer")

    val builder = new KStreamBuilder
    val kStream = builder.stream("calender")

    val producer = new KafkaProducer[CalenderAvro, Array[Byte]](streamsConfiguration)

    val mappedStream = kStream.map[Calender, CalenderAvro] {
      new KeyValueMapper[Calender, CalenderAvro, KeyValue[Calender, CalenderAvro]] {
        override def apply(key: Calender, value: CalenderAvro): KeyValue[Calender, CalenderAvro] = {

          // Read the avro schema file and
          val schema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/calender-avro.avsc")).mkString)

          def send(topic: String, calenderAvro: CalenderAvro) = {
            val genericUser: GenericRecord = new GenericData.Record(schema)
            try {
              val queueMessages: ProducerRecord[CalenderAvro, Array[Byte]] = {

                // Create avro generic record object
                //Put data in that generic record object
                genericUser.put("year", key.Year.toInt)
                genericUser.put("name", key.WeekNo.toInt)
                genericUser.put("weekday", List(key.Monday.toInt, key.Tuesday.toInt, key.Wednesday.toInt, key.Thursday.toInt, key.Friday.toInt))
                genericUser.put("weekend", List(key.Saturday, key.Sunday))
                genericUser.put("weekdayTotal", key.Monday.toInt + key.Tuesday.toInt + key.Wednesday.toInt + key.Thursday.toInt + key.Friday.toInt)
                genericUser.put("weekendTotal", key.Saturday.toInt + key.Sunday.toInt)
                genericUser.put("ratio", (key.Monday.toInt + key.Tuesday.toInt + key.Wednesday.toInt + key.Thursday.toInt + key.Friday.toInt) / (key.Saturday.toInt + key.Sunday.toInt))


                // Serialize generic record object into byte array
                val writer = new SpecificDatumWriter[GenericRecord](schema)
                val out = new ByteArrayOutputStream()
                val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)
                writer.write(genericUser, encoder)
                encoder.flush()
                out.close()

                val serializedBytes: Array[Byte] = out.toByteArray

                val record: ProducerRecord[CalenderAvro, Array[Byte]] = new ProducerRecord[CalenderAvro, Array[Byte]](topic, serializedBytes)
                record
              }
              producer.send(queueMessages)

            } catch {
              case ex: Exception =>
                println(ex.printStackTrace().toString)
                ex.printStackTrace()
            }
          }
          new KeyValue[Calender,CalenderAvro](key, CalenderAvro(0,0,List(0),List(0),0,0,0))
        }
      }
    }

    //sending data to calenderAvro topic
    mappedStream.to("calenderAvro")

    val stream: KafkaStreams = new KafkaStreams(builder, streamsConfiguration)
    stream.start()
  }
}
