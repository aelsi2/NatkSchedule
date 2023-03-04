package aelsi2.natkschedule.data.database.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class DateConverter {
    @TypeConverter
    fun localDateToLong(localDate : LocalDate?) = localDate?.toEpochDay()

    @TypeConverter
    fun longToLocalDate(long : Long?) = if (long == null) { null } else { LocalDate.ofEpochDay(long) }
}