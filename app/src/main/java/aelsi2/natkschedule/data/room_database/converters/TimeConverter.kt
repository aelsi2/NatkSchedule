package aelsi2.natkschedule.data.room_database.converters

import androidx.room.TypeConverter
import java.time.LocalTime

class TimeConverter {
    @TypeConverter
    fun localTimeToLong(localTime : LocalTime?) = localTime?.toSecondOfDay()?.toLong()

    @TypeConverter
    fun longToLocalTime(long : Long?) = if (long == null) { null } else { LocalTime.ofSecondOfDay(long) }
}