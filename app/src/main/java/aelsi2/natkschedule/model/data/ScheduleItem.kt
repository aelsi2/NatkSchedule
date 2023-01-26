package aelsi2.natkschedule.model.data

import java.time.LocalDateTime

data class ScheduleItem(
    val discipline : String,
    val startTime : LocalDateTime,
    val endTime : LocalDateTime,
    val group: Group,
    val subgroup : Int,
    val teacher: Teacher,
    val classroom: Classroom
    ) {
    val ongoing : Boolean
        get() = (startTime < LocalDateTime.now()) and (LocalDateTime.now() < endTime)
}
