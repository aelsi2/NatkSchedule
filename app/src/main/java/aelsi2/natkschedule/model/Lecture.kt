package aelsi2.natkschedule.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

enum class LectureState {
    HAS_NOT_STARTED,
    ONGOING,
    MID_BREAK,
    ENDED,
    ALL_DAY
}
data class Lecture(
    val disciplineName : String,
    val date : LocalDate,
    val startTime : LocalTime?,
    val endTime : LocalTime?,
    val teacher: Teacher?,
    val classroom: Classroom?,
    val group: Group?,
    val subgroupNumber : Int? = null,
    val breakStartTime: LocalTime? = null,
    val breakEndTime : LocalTime? = null,
    val id : String =
        "${startTime}_${teacher?.fullName}_${classroom?.fullName}_${group?.name}_${subgroupNumber}"
) {
    val hasBreak : Boolean
        get() = breakStartTime != null && breakEndTime != null
    val isAllDay : Boolean
        get() = startTime == null || endTime == null

    fun getState(timeZone : ZoneId = ZoneId.of(DEFAULT_TIME_ZONE_NAME)) : LectureState {
            val zonedStartTime = if (startTime == null) { date.atStartOfDay()} else { date.atTime(startTime) }.atZone(timeZone)
            val zonedEndTime = if (startTime == null) { date.plusDays(1).atStartOfDay()} else { date.atTime(endTime) }.atZone(timeZone)

            val zonedBreakStartTime = if (breakStartTime == null) { null } else date.atTime(breakStartTime).atZone(timeZone)
            val zonedBreakEndTime = if (breakStartTime == null) { null } else date.atTime(breakEndTime).atZone(timeZone)

            val zonedCurrentTime = ZonedDateTime.now()

            return when {
                zonedCurrentTime < zonedStartTime -> LectureState.HAS_NOT_STARTED
                zonedCurrentTime > zonedEndTime -> LectureState.ENDED
                hasBreak && zonedCurrentTime in zonedBreakStartTime!!..zonedBreakEndTime!! -> LectureState.MID_BREAK
                isAllDay && zonedCurrentTime in zonedStartTime..zonedEndTime -> LectureState.ALL_DAY
                else -> LectureState.ONGOING
            }
        }
    companion object {
        const val DEFAULT_TIME_ZONE_NAME = "Asia/Novosibirsk"
    }
}
