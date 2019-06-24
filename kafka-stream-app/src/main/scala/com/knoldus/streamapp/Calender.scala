package com.knoldus.streamapp

import play.api.libs.json.{Format, Json}

case class Calender(Year: String, WeekNo: String,
                    Monday: String, Tuesday: String, Wednesday: String, Thursday: String ,Friday: String,
                    Saturday: String, Sunday: String) {
}

object Calender extends App {
  implicit val Format: Format[Calender] = Json.format
}
