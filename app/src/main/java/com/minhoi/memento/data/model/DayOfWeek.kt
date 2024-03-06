package com.minhoi.memento.data.model

import java.util.Calendar

enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

   companion object {
       fun getDayOfWeek(year: Int, month: Int, dayOfMonth: Int): DayOfWeek {
           val calendar = Calendar.getInstance()
           calendar.set(year, month, dayOfMonth)
           val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

           return when (dayOfWeek) {
               Calendar.MONDAY -> MONDAY
               Calendar.TUESDAY -> TUESDAY
               Calendar.WEDNESDAY -> WEDNESDAY
               Calendar.THURSDAY -> THURSDAY
               Calendar.FRIDAY -> FRIDAY
               Calendar.SATURDAY -> SATURDAY
               Calendar.SUNDAY -> SUNDAY
               else -> throw IllegalArgumentException("Invalid day of week")
           }
       }
   }
}

