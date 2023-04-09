package aelsi2.natkschedule.data.room_database.model

import aelsi2.natkschedule.model.ScheduleDay
import aelsi2.natkschedule.model.ScheduleIdentifier
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "ScheduleDays")
data class ScheduleDayEntity(
    val scheduleDayDate: LocalDate,
    val scheduleDayScheduleIdentifier: ScheduleIdentifier,
) {
    @PrimaryKey(autoGenerate = true) var scheduleDayId: Long = 0
    companion object {
        fun fromDay(scheduleDay: ScheduleDay, scheduleIdentifier: ScheduleIdentifier) =
            ScheduleDayEntity(
                scheduleDay.date,
                scheduleIdentifier
            )
    }
}
