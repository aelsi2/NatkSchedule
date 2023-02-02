package aelsi2.natkschedule.data.database.converters

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class InstantConverter {
    @TypeConverter
    fun instantToLong(instant : Instant?) = instant?.epochSecond

    @TypeConverter
    fun longToInstant(long : Long?) = if (long == null) null else Instant.ofEpochSecond(long)
}