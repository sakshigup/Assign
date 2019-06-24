package com.knoldus.streamapp

import org.apache.kafka.common.serialization.Deserializer
import java.io.{ByteArrayInputStream, ObjectInputStream}
import java.util

class CalenderDeserializer extends Deserializer[Calender] {

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, bytes: Array[Byte]): Calender = {
    val byteIn = new ByteArrayInputStream(bytes)
    val objIn = new ObjectInputStream(byteIn)
    val obj = objIn.readObject().asInstanceOf[Calender]
    byteIn.close()
    objIn.close()
    obj
  }

  override def close(): Unit = {}
}
