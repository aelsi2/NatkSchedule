package aelsi2.natkschedule.data.room_database.converters

import aelsi2.natkschedule.model.ScheduleIdentifier
import androidx.room.TypeConverter

class ScheduleIdentifierConverter {
    @TypeConverter
    fun scheduleIdentifierToString(scheduleIdentifier: ScheduleIdentifier?) = scheduleIdentifier?.toString()

    @TypeConverter
    fun stringToScheduleIdentifier(string: String?) = ScheduleIdentifier.fromString(string)
}