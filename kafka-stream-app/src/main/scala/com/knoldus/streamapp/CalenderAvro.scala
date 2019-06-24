package com.knoldus.streamapp

  case class CalenderAvro(Year: Integer, WeekNo: Integer,
                          Weekday: List[Integer], Weekend: List[Integer],
                          WeekDayTotal: Integer, WeekendTotal: Integer,
                          Ratio: Integer)


